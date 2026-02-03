package com.flipperdevices.bridge.connection.feature.networkproxy.impl.api

import com.flipperdevices.bridge.connection.feature.networkproxy.api.NetworkConnection
import com.flipperdevices.bridge.connection.feature.networkproxy.api.NetworkConnectionState
import com.flipperdevices.bridge.connection.feature.networkproxy.api.NetworkError
import com.flipperdevices.bridge.connection.feature.networkproxy.api.NetworkProtocol
import com.flipperdevices.bridge.connection.feature.networkproxy.api.ReceivedData
import com.flipperdevices.bridge.connection.feature.networkproxy.api.SendResult
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket
import java.net.SocketException
import java.net.UnknownHostException

private const val MAX_CONNECTIONS = 8
private const val RECEIVE_BUFFER_SIZE = 512
private const val UDP_RECEIVE_BUFFER_SIZE = 65535

internal class SocketManager(
    private val scope: CoroutineScope
) : LogTagProvider {
    override val TAG = "SocketManager"

    private val connections = mutableMapOf<Int, ManagedConnection>()
    private var nextConnectionId = 0

    private val _receivedDataFlow = MutableSharedFlow<ReceivedData>(extraBufferCapacity = 64)
    val receivedDataFlow: SharedFlow<ReceivedData> = _receivedDataFlow.asSharedFlow()

    private val _connectionStateFlow = MutableSharedFlow<NetworkConnection>(extraBufferCapacity = 16)
    val connectionStateFlow: SharedFlow<NetworkConnection> = _connectionStateFlow.asSharedFlow()

    suspend fun connect(
        host: String,
        port: Int,
        protocol: NetworkProtocol,
        timeoutMs: Int,
        clientConnectionId: Int
    ): Result<NetworkConnection> = withContext(Dispatchers.IO) {
        if (connections.size >= MAX_CONNECTIONS) {
            return@withContext Result.failure(
                NetworkProxyException(NetworkError.MaxConnections)
            )
        }

        // Use the client-provided connection ID
        val connectionId = if (clientConnectionId > 0) clientConnectionId else ++nextConnectionId
        info { "Connecting $connectionId: $protocol to $host:$port (timeout: ${timeoutMs}ms)" }

        try {
            val resolvedAddress = try {
                InetAddress.getByName(host)
            } catch (e: UnknownHostException) {
                error(e) { "DNS resolution failed for $host" }
                return@withContext Result.failure(
                    NetworkProxyException(NetworkError.DnsFailed)
                )
            }

            val resolvedIp = resolvedAddress.hostAddress

            when (protocol) {
                NetworkProtocol.TCP -> {
                    val socket = Socket()
                    val connectResult = withTimeoutOrNull(timeoutMs.toLong()) {
                        try {
                            socket.connect(InetSocketAddress(resolvedAddress, port), timeoutMs)
                            true
                        } catch (e: IOException) {
                            error(e) { "TCP connection failed to $host:$port" }
                            false
                        }
                    }

                    if (connectResult != true) {
                        socket.close()
                        return@withContext Result.failure(
                            NetworkProxyException(NetworkError.Timeout)
                        )
                    }

                    val connection = ManagedConnection.Tcp(
                        id = connectionId,
                        socket = socket,
                        host = host,
                        port = port,
                        resolvedIp = resolvedIp
                    )
                    connections[connectionId] = connection
                    startTcpReceiver(connection)

                    val networkConnection = NetworkConnection(
                        connectionId = connectionId,
                        state = NetworkConnectionState.CONNECTED,
                        resolvedIp = resolvedIp
                    )
                    // Don't emit to connectionStateFlow here - the response is sent directly
                    // The flow is only for async state changes (disconnection, errors)
                    Result.success(networkConnection)
                }

                NetworkProtocol.UDP -> {
                    val socket = DatagramSocket()
                    socket.connect(resolvedAddress, port)

                    val connection = ManagedConnection.Udp(
                        id = connectionId,
                        socket = socket,
                        remoteAddress = resolvedAddress,
                        remotePort = port,
                        host = host,
                        resolvedIp = resolvedIp
                    )
                    connections[connectionId] = connection
                    startUdpReceiver(connection)

                    val networkConnection = NetworkConnection(
                        connectionId = connectionId,
                        state = NetworkConnectionState.CONNECTED,
                        resolvedIp = resolvedIp
                    )
                    // Don't emit to connectionStateFlow here - the response is sent directly
                    Result.success(networkConnection)
                }
            }
        } catch (e: Exception) {
            error(e) { "Connection failed" }
            Result.failure(NetworkProxyException(NetworkError.InternalError))
        }
    }

    suspend fun send(connectionId: Int, data: ByteArray): Result<SendResult> = withContext(Dispatchers.IO) {
        val connection = connections[connectionId]
            ?: return@withContext Result.failure(
                NetworkProxyException(NetworkError.InvalidConnection)
            )

        try {
            when (connection) {
                is ManagedConnection.Tcp -> {
                    if (connection.socket.isClosed || !connection.socket.isConnected) {
                        return@withContext Result.failure(
                            NetworkProxyException(NetworkError.NotConnected)
                        )
                    }
                    connection.socket.getOutputStream().write(data)
                    connection.socket.getOutputStream().flush()
                }

                is ManagedConnection.Udp -> {
                    if (connection.socket.isClosed) {
                        return@withContext Result.failure(
                            NetworkProxyException(NetworkError.NotConnected)
                        )
                    }
                    val packet = DatagramPacket(
                        data,
                        data.size,
                        connection.remoteAddress,
                        connection.remotePort
                    )
                    connection.socket.send(packet)
                }
            }

            Result.success(
                SendResult(
                    connectionId = connectionId,
                    bytesSent = data.size
                )
            )
        } catch (e: IOException) {
            error(e) { "Send failed on connection $connectionId" }
            Result.failure(NetworkProxyException(NetworkError.SendFailed))
        }
    }

    suspend fun close(connectionId: Int): Result<Unit> = withContext(Dispatchers.IO) {
        val connection = connections.remove(connectionId)
            ?: return@withContext Result.failure(
                NetworkProxyException(NetworkError.InvalidConnection)
            )

        info { "Closing connection $connectionId" }

        try {
            // Cancel receiver job (don't wait - socket close will unblock it)
            connection.receiverJob?.cancel()
            // Close socket - this will cause blocked reads to throw and receiver to exit
            when (connection) {
                is ManagedConnection.Tcp -> connection.socket.close()
                is ManagedConnection.Udp -> connection.socket.close()
            }
            // Don't emit to connectionStateFlow - the response is sent directly
            // The flow is only for async disconnections (remote side closing)
            Result.success(Unit)
        } catch (e: Exception) {
            error(e) { "Error closing connection $connectionId" }
            Result.success(Unit) // Still consider it closed
        }
    }

    fun closeAll() {
        connections.keys.toList().forEach { connectionId ->
            scope.launch {
                close(connectionId)
            }
        }
    }

    private fun startTcpReceiver(connection: ManagedConnection.Tcp) {
        connection.receiverJob = scope.launch(Dispatchers.IO) {
            val buffer = ByteArray(RECEIVE_BUFFER_SIZE)
            try {
                val inputStream = connection.socket.getInputStream()
                while (isActive && !connection.socket.isClosed) {
                    val bytesRead = inputStream.read(buffer)
                    if (bytesRead == -1) {
                        // Connection closed by remote
                        info { "TCP connection ${connection.id} closed by remote" }
                        break
                    }
                    if (bytesRead > 0) {
                        val data = buffer.copyOf(bytesRead)
                        _receivedDataFlow.emit(
                            ReceivedData(
                                connectionId = connection.id,
                                data = data
                            )
                        )
                    }
                }
            } catch (e: SocketException) {
                if (isActive) {
                    info { "TCP socket ${connection.id} closed: ${e.message}" }
                }
            } catch (e: IOException) {
                if (isActive) {
                    error(e) { "TCP receive error on connection ${connection.id}" }
                }
            } finally {
                if (connections.containsKey(connection.id)) {
                    connections.remove(connection.id)
                    _connectionStateFlow.emit(
                        NetworkConnection(
                            connectionId = connection.id,
                            state = NetworkConnectionState.DISCONNECTED
                        )
                    )
                }
            }
        }
    }

    private fun startUdpReceiver(connection: ManagedConnection.Udp) {
        connection.receiverJob = scope.launch(Dispatchers.IO) {
            val buffer = ByteArray(UDP_RECEIVE_BUFFER_SIZE)
            try {
                while (isActive && !connection.socket.isClosed) {
                    val packet = DatagramPacket(buffer, buffer.size)
                    connection.socket.receive(packet)
                    if (packet.length > 0) {
                        val data = buffer.copyOf(packet.length)
                        _receivedDataFlow.emit(
                            ReceivedData(
                                connectionId = connection.id,
                                data = data
                            )
                        )
                    }
                }
            } catch (e: SocketException) {
                if (isActive) {
                    info { "UDP socket ${connection.id} closed: ${e.message}" }
                }
            } catch (e: IOException) {
                if (isActive) {
                    error(e) { "UDP receive error on connection ${connection.id}" }
                }
            } finally {
                if (connections.containsKey(connection.id)) {
                    connections.remove(connection.id)
                    _connectionStateFlow.emit(
                        NetworkConnection(
                            connectionId = connection.id,
                            state = NetworkConnectionState.DISCONNECTED
                        )
                    )
                }
            }
        }
    }
}

private sealed class ManagedConnection {
    abstract val id: Int
    abstract var receiverJob: Job?

    data class Tcp(
        override val id: Int,
        val socket: Socket,
        val host: String,
        val port: Int,
        val resolvedIp: String?
    ) : ManagedConnection() {
        override var receiverJob: Job? = null
    }

    data class Udp(
        override val id: Int,
        val socket: DatagramSocket,
        val remoteAddress: InetAddress,
        val remotePort: Int,
        val host: String,
        val resolvedIp: String?
    ) : ManagedConnection() {
        override var receiverJob: Job? = null
    }
}

class NetworkProxyException(val error: NetworkError) : Exception("Network proxy error: $error")

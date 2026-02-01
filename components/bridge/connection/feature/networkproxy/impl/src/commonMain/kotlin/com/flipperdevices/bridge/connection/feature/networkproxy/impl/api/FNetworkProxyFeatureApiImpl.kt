package com.flipperdevices.bridge.connection.feature.networkproxy.impl.api

import androidx.datastore.core.DataStore
import com.flipperdevices.bridge.connection.feature.networkproxy.api.FNetworkProxyFeatureApi
import com.flipperdevices.bridge.connection.feature.networkproxy.api.NetworkConnection
import com.flipperdevices.bridge.connection.feature.networkproxy.api.NetworkConnectionState
import com.flipperdevices.bridge.connection.feature.networkproxy.api.NetworkError
import com.flipperdevices.bridge.connection.feature.networkproxy.api.NetworkProtocol
import com.flipperdevices.bridge.connection.feature.networkproxy.api.ReceivedData
import com.flipperdevices.bridge.connection.feature.networkproxy.api.SendResult
import com.flipperdevices.bridge.connection.feature.rpc.api.FRpcFeatureApi
import com.flipperdevices.bridge.connection.feature.rpc.model.wrapToRequest
import com.flipperdevices.core.di.provideDelegate
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.core.preference.pb.Settings
import com.flipperdevices.protobuf.Main
import com.flipperdevices.protobuf.network.ConnectionState
import com.flipperdevices.protobuf.network.ErrorCode
import com.flipperdevices.protobuf.network.Protocol
import com.flipperdevices.protobuf.network.ReceiveData
import com.flipperdevices.protobuf.network.StateChanged
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.job
import okio.ByteString.Companion.toByteString
import javax.inject.Provider

private const val MAX_CHUNK_SIZE = 512

class FNetworkProxyFeatureApiImpl @AssistedInject constructor(
    settingsStoreProvider: Provider<DataStore<Settings>>,
    @Assisted private val rpcFeatureApi: FRpcFeatureApi,
    @Assisted private val scope: CoroutineScope
) : FNetworkProxyFeatureApi, LogTagProvider {
    override val TAG = "FNetworkProxyFeatureApi"
    private val settingsStore by settingsStoreProvider

    private val socketManager = SocketManager(scope)

    private val _receivedDataFlow = MutableSharedFlow<ReceivedData>(extraBufferCapacity = 64)
    private val _connectionStateFlow = MutableSharedFlow<NetworkConnection>(extraBufferCapacity = 16)

    init {
        // Observe settings and start/stop foreground service when setting changes
        settingsStore.data
            .map { it.enable_network_proxy_service }
            .distinctUntilChanged()
            .onEach { enabled ->
                if (enabled) {
                    info { "Network proxy service enabled - starting foreground service" }
                    NetworkProxyServiceController.startService()
                } else {
                    info { "Network proxy service disabled - stopping foreground service" }
                    NetworkProxyServiceController.stopService()
                }
            }
            .launchIn(scope)

        // Forward socket manager flows
        socketManager.receivedDataFlow
            .onEach { data ->
                // Send received data to Flipper
                sendReceivedDataToFlipper(data)
                _receivedDataFlow.emit(data)
            }
            .launchIn(scope)

        socketManager.connectionStateFlow
            .onEach { connection ->
                // Send state change to Flipper
                sendStateChangedToFlipper(connection)
                _connectionStateFlow.emit(connection)
            }
            .launchIn(scope)

        // Listen for incoming requests from Flipper
        rpcFeatureApi.notificationFlow()
            .onEach { message -> handleFlipperMessage(message) }
            .catch { e -> info { "Notification flow error: ${e.message}" } }
            .launchIn(scope)

        // Stop foreground service when scope is cancelled (device disconnected)
        scope.coroutineContext.job.invokeOnCompletion {
            info { "Network proxy feature shutting down - stopping foreground service" }
            NetworkProxyServiceController.stopService()
        }
    }

    override fun receivedDataFlow(): Flow<ReceivedData> = _receivedDataFlow.asSharedFlow()

    override fun connectionStateFlow(): Flow<NetworkConnection> = _connectionStateFlow.asSharedFlow()

    override suspend fun connect(
        host: String,
        port: Int,
        protocol: NetworkProtocol,
        timeoutMs: Int
    ): Result<NetworkConnection> {
        info { "Connect request: $protocol to $host:$port" }
        return socketManager.connect(host, port, protocol, timeoutMs, clientConnectionId = 0)
    }

    override suspend fun send(connectionId: Int, data: ByteArray): Result<SendResult> {
        info { "Send request: ${data.size} bytes on connection $connectionId" }

        // Chunk data if needed
        if (data.size <= MAX_CHUNK_SIZE) {
            return socketManager.send(connectionId, data)
        }

        var totalBytesSent = 0
        var offset = 0
        while (offset < data.size) {
            val chunkSize = minOf(MAX_CHUNK_SIZE, data.size - offset)
            val chunk = data.copyOfRange(offset, offset + chunkSize)
            val result = socketManager.send(connectionId, chunk)
            if (result.isFailure) {
                return result
            }
            totalBytesSent += result.getOrNull()?.bytesSent ?: 0
            offset += chunkSize
        }

        return Result.success(
            SendResult(
                connectionId = connectionId,
                bytesSent = totalBytesSent
            )
        )
    }

    override suspend fun close(connectionId: Int): Result<Unit> {
        info { "Close request: connection $connectionId" }
        return socketManager.close(connectionId)
    }

    private suspend fun handleFlipperMessage(message: Main) {
        when {
            message.network_connect_request != null -> {
                handleConnectRequest(message)
            }
            message.network_send_request != null -> {
                handleSendRequest(message)
            }
            message.network_close_request != null -> {
                handleCloseRequest(message)
            }
        }
    }

    private suspend fun handleConnectRequest(message: Main) {
        val request = message.network_connect_request ?: return
        val clientConnectionId = request.connection_id.toInt()
        info { "Flipper connect request: ${request.host}:${request.port} (conn_id=$clientConnectionId)" }

        val protocol = when (request.protocol) {
            Protocol.UDP -> NetworkProtocol.UDP
            else -> NetworkProtocol.TCP
        }

        val result = socketManager.connect(
            host = request.host,
            port = request.port.toInt(),
            protocol = protocol,
            timeoutMs = request.timeout_ms.toInt().takeIf { it > 0 } ?: 30000,
            clientConnectionId = clientConnectionId
        )

        // Send response
        val response = result.fold(
            onSuccess = { conn ->
                info { "Connect success: conn_id=$clientConnectionId -> ${conn.resolvedIp}" }
                Main(
                    command_id = message.command_id,
                    network_connect_response = com.flipperdevices.protobuf.network.ConnectResponse(
                        connection_id = conn.connectionId,
                        state = ConnectionState.CONNECTED,
                        error = ErrorCode.NONE,
                        resolved_ip = conn.resolvedIp ?: ""
                    )
                )
            },
            onFailure = { e ->
                val errorCode = when ((e as? NetworkProxyException)?.error) {
                    NetworkError.DnsFailed -> ErrorCode.DNS_FAILED
                    NetworkError.Timeout -> ErrorCode.TIMEOUT
                    NetworkError.ConnectionRefused -> ErrorCode.CONNECTION_REFUSED
                    NetworkError.NetworkUnreachable -> ErrorCode.NETWORK_UNREACHABLE
                    NetworkError.HostUnreachable -> ErrorCode.HOST_UNREACHABLE
                    NetworkError.MaxConnections -> ErrorCode.MAX_CONNECTIONS
                    else -> ErrorCode.INTERNAL_ERROR
                }
                info { "Connect failed: conn_id=$clientConnectionId error=$errorCode (${e.message})" }
                Main(
                    command_id = message.command_id,
                    network_connect_response = com.flipperdevices.protobuf.network.ConnectResponse(
                        connection_id = 0,
                        state = ConnectionState.ERROR,
                        error = errorCode
                    )
                )
            }
        )

        info { "Sending connect response for conn_id=$clientConnectionId" }
        rpcFeatureApi.requestWithoutAnswer(response.wrapToRequest())
    }

    private suspend fun handleSendRequest(message: Main) {
        val request = message.network_send_request ?: return
        val connectionId = request.connection_id.toInt()
        val data = request.data_.toByteArray()

        info { "Flipper send request: ${data.size} bytes on connection $connectionId" }

        val result = socketManager.send(connectionId, data)

        val response = result.fold(
            onSuccess = { sendResult ->
                info { "Send success: conn_id=$connectionId bytes=${sendResult.bytesSent}" }
                Main(
                    command_id = message.command_id,
                    network_send_response = com.flipperdevices.protobuf.network.SendResponse(
                        connection_id = connectionId,
                        bytes_sent = sendResult.bytesSent,
                        error = ErrorCode.NONE
                    )
                )
            },
            onFailure = { e ->
                val errorCode = when ((e as? NetworkProxyException)?.error) {
                    NetworkError.InvalidConnection -> ErrorCode.INVALID_CONNECTION
                    NetworkError.NotConnected -> ErrorCode.NOT_CONNECTED
                    NetworkError.SendFailed -> ErrorCode.SEND_FAILED
                    else -> ErrorCode.INTERNAL_ERROR
                }
                info { "Send failed: conn_id=$connectionId error=$errorCode" }
                Main(
                    command_id = message.command_id,
                    network_send_response = com.flipperdevices.protobuf.network.SendResponse(
                        connection_id = connectionId,
                        bytes_sent = 0,
                        error = errorCode
                    )
                )
            }
        )

        info { "Sending send response for conn_id=$connectionId" }
        rpcFeatureApi.requestWithoutAnswer(response.wrapToRequest())
    }

    private suspend fun handleCloseRequest(message: Main) {
        val request = message.network_close_request ?: return
        val connectionId = request.connection_id.toInt()

        info { "Flipper close request: connection $connectionId" }

        val result = socketManager.close(connectionId)

        val response = result.fold(
            onSuccess = {
                Main(
                    command_id = message.command_id,
                    network_close_response = com.flipperdevices.protobuf.network.CloseResponse(
                        connection_id = connectionId,
                        error = ErrorCode.NONE
                    )
                )
            },
            onFailure = { e ->
                val errorCode = when ((e as? NetworkProxyException)?.error) {
                    NetworkError.InvalidConnection -> ErrorCode.INVALID_CONNECTION
                    else -> ErrorCode.INTERNAL_ERROR
                }
                Main(
                    command_id = message.command_id,
                    network_close_response = com.flipperdevices.protobuf.network.CloseResponse(
                        connection_id = connectionId,
                        error = errorCode
                    )
                )
            }
        )

        rpcFeatureApi.requestWithoutAnswer(response.wrapToRequest())
    }

    private suspend fun sendReceivedDataToFlipper(data: ReceivedData) {
        // Chunk data if needed and send to Flipper
        var offset = 0
        while (offset < data.data.size) {
            val chunkSize = minOf(MAX_CHUNK_SIZE, data.data.size - offset)
            val chunk = data.data.copyOfRange(offset, offset + chunkSize)
            val hasNext = offset + chunkSize < data.data.size

            val message = Main(
                has_next = hasNext,
                network_receive_data = ReceiveData(
                    connection_id = data.connectionId,
                    data_ = chunk.toByteString()
                )
            )

            rpcFeatureApi.requestWithoutAnswer(message.wrapToRequest())
            offset += chunkSize
        }
    }

    private suspend fun sendStateChangedToFlipper(connection: NetworkConnection) {
        val state = when (connection.state) {
            NetworkConnectionState.DISCONNECTED -> ConnectionState.DISCONNECTED
            NetworkConnectionState.CONNECTING -> ConnectionState.CONNECTING
            NetworkConnectionState.CONNECTED -> ConnectionState.CONNECTED
            NetworkConnectionState.ERROR -> ConnectionState.ERROR
        }

        val error = when (connection.error) {
            NetworkError.None -> ErrorCode.NONE
            NetworkError.DnsFailed -> ErrorCode.DNS_FAILED
            NetworkError.Timeout -> ErrorCode.TIMEOUT
            NetworkError.ConnectionRefused -> ErrorCode.CONNECTION_REFUSED
            NetworkError.NetworkUnreachable -> ErrorCode.NETWORK_UNREACHABLE
            NetworkError.HostUnreachable -> ErrorCode.HOST_UNREACHABLE
            NetworkError.InvalidConnection -> ErrorCode.INVALID_CONNECTION
            NetworkError.NotConnected -> ErrorCode.NOT_CONNECTED
            NetworkError.SendFailed -> ErrorCode.SEND_FAILED
            NetworkError.ReceiveFailed -> ErrorCode.RECEIVE_FAILED
            NetworkError.MaxConnections -> ErrorCode.MAX_CONNECTIONS
            NetworkError.InvalidProtocol -> ErrorCode.INVALID_PROTOCOL
            NetworkError.InternalError -> ErrorCode.INTERNAL_ERROR
        }

        val message = Main(
            network_state_changed = StateChanged(
                connection_id = connection.connectionId,
                state = state,
                error = error
            )
        )

        rpcFeatureApi.requestWithoutAnswer(message.wrapToRequest())
    }

    @AssistedFactory
    fun interface InternalFactory {
        operator fun invoke(
            rpcFeatureApi: FRpcFeatureApi,
            scope: CoroutineScope
        ): FNetworkProxyFeatureApiImpl
    }
}

package com.flipperdevices.bridge.connection.feature.networkproxy.api

import com.flipperdevices.bridge.connection.feature.common.api.FDeviceFeatureApi
import kotlinx.coroutines.flow.Flow

enum class NetworkProtocol {
    TCP,
    UDP
}

enum class NetworkConnectionState {
    DISCONNECTED,
    CONNECTING,
    CONNECTED,
    ERROR
}

sealed class NetworkError {
    data object None : NetworkError()
    data object DnsFailed : NetworkError()
    data object Timeout : NetworkError()
    data object ConnectionRefused : NetworkError()
    data object NetworkUnreachable : NetworkError()
    data object HostUnreachable : NetworkError()
    data object InvalidConnection : NetworkError()
    data object NotConnected : NetworkError()
    data object SendFailed : NetworkError()
    data object ReceiveFailed : NetworkError()
    data object MaxConnections : NetworkError()
    data object InvalidProtocol : NetworkError()
    data object InternalError : NetworkError()
}

data class NetworkConnection(
    val connectionId: Int,
    val state: NetworkConnectionState,
    val error: NetworkError = NetworkError.None,
    val resolvedIp: String? = null
)

data class SendResult(
    val connectionId: Int,
    val bytesSent: Int,
    val error: NetworkError = NetworkError.None
)

data class ReceivedData(
    val connectionId: Int,
    val data: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as ReceivedData
        return connectionId == other.connectionId && data.contentEquals(other.data)
    }

    override fun hashCode(): Int {
        var result = connectionId
        result = 31 * result + data.contentHashCode()
        return result
    }
}

interface FNetworkProxyFeatureApi : FDeviceFeatureApi {
    /**
     * Flow of received data from all connections.
     * Data is pushed here when the remote server sends data.
     */
    fun receivedDataFlow(): Flow<ReceivedData>

    /**
     * Flow of connection state changes.
     */
    fun connectionStateFlow(): Flow<NetworkConnection>

    /**
     * Connect to a remote host.
     * @param host The hostname or IP address
     * @param port The port number (1-65535)
     * @param protocol TCP or UDP
     * @param timeoutMs Connection timeout in milliseconds
     * @return Connection result with connection ID if successful
     */
    suspend fun connect(
        host: String,
        port: Int,
        protocol: NetworkProtocol = NetworkProtocol.TCP,
        timeoutMs: Int = 30000
    ): Result<NetworkConnection>

    /**
     * Send data on an established connection.
     * @param connectionId The connection ID from connect()
     * @param data The data to send (max 512 bytes per call, will be chunked if larger)
     * @return Result with bytes sent or error
     */
    suspend fun send(connectionId: Int, data: ByteArray): Result<SendResult>

    /**
     * Close a connection.
     * @param connectionId The connection ID to close
     * @return Result indicating success or error
     */
    suspend fun close(connectionId: Int): Result<Unit>
}

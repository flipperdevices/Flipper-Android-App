package com.flipperdevices.bridge.connection.transport.common.api.meta

import kotlinx.coroutines.flow.Flow

interface FTransportMetaInfoApi {
    fun get(key: TransportMetaInfoKey): Result<Flow<ByteArray?>>
}

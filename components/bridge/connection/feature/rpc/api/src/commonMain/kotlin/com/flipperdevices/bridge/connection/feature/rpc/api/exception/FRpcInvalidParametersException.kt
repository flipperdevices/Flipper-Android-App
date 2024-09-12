package com.flipperdevices.bridge.connection.feature.rpc.api.exception

import com.flipperdevices.protobuf.Main

data class FRpcInvalidParametersException(
    override val response: Main
) : FRpcException()

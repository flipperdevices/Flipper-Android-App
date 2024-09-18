package com.flipperdevices.bridge.connection.feature.rpc.api.exception

import com.flipperdevices.protobuf.Main

abstract class FRpcException : Throwable() {
    abstract val response: Main
}

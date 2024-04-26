package com.flipperdevices.bridge.connection.transport.common.api.di

import com.flipperdevices.bridge.connection.transport.common.api.DeviceConnectionApi

/**
 * Used to bypass DI issues and avoid having generics work in the dependency graph
 */
class DeviceConnectionApiHolder(val deviceConnectionApi: DeviceConnectionApi<*, *>)
fun DeviceConnectionApi<*, *>.toHolder() = DeviceConnectionApiHolder(this)

package com.flipperdevices.bridge.connection.common.api.di

import com.flipperdevices.bridge.connection.common.api.FDeviceConnectionConfig
import dagger.MapKey
import kotlin.reflect.KClass

@MapKey
annotation class ConnectionConfigQualifier(val configClazz: KClass<out FDeviceConnectionConfig<*>>)
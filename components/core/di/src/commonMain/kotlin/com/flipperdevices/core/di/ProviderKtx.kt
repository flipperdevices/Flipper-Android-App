package com.flipperdevices.core.di

import dev.zacsweers.metro.Provider
import kotlin.reflect.KProperty

operator fun <T> Provider<T>.provideDelegate(
    receiver: Any?,
    property: KProperty<*>
): Lazy<T> = lazy { invoke() }

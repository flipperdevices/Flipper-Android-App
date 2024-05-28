package com.flipperdevices.core.di

import javax.inject.Provider
import kotlin.reflect.KProperty

operator fun <T> Provider<T>.provideDelegate(
    receiver: Any?,
    property: KProperty<*>
): Lazy<T> = lazy { get() }

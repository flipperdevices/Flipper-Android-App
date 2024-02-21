package com.flipperdevices.core.ui.lifecycle

import com.arkivanov.essenty.lifecycle.LifecycleOwner
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import kotlin.coroutines.CoroutineContext

/**
 * To be able to mock coroutine scope
 */
object DecomposeViewModelCoroutineScopeProvider {
    fun provideCoroutineScope(
        lifecycleOwner: LifecycleOwner,
        context: CoroutineContext
    ) = lifecycleOwner.coroutineScope(context)
}

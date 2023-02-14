package com.flipperdevices.core.test

import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineExceptionHandler

class PendingCoroutineExceptionHandler :
    AbstractCoroutineContextElement(CoroutineExceptionHandler),
    CoroutineExceptionHandler {
    private val pendingExceptions = mutableListOf<Throwable>()

    override fun handleException(context: CoroutineContext, exception: Throwable) {
        pendingExceptions.add(exception)
    }

    fun throwAll() {
        pendingExceptions.firstOrNull()?.apply {
            pendingExceptions.drop(1).forEach { addSuppressed(it) }
            throw this
        }
    }
}
package com.flipperdevices.unhandledexception.api

import kotlinx.coroutines.flow.Flow

interface UnhandledExceptionApi {
    fun initExceptionHandler()

    fun isBleConnectionForbiddenFlow(): Flow<Boolean>
}

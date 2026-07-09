package com.flipperdevices.core.log

import com.flipperdevices.core.buildkonfig.BuildKonfig
import timber.log.Timber

actual inline fun error(logMessage: () -> String) {
    if (BuildKonfig.IS_LOG_ENABLED) {
        Timber.e(logMessage.invoke())
    }
}

actual inline fun error(error: Throwable, logMessage: () -> String) {
    if (BuildKonfig.IS_LOG_ENABLED) {
        Timber.e(error, logMessage.invoke())
    }
}

actual inline fun info(logMessage: () -> String) {
    if (BuildKonfig.IS_LOG_ENABLED) {
        Timber.i(logMessage.invoke())
    }
}

actual inline fun verbose(logMessage: () -> String) {
    if (BuildKonfig.IS_LOG_ENABLED) {
        Timber.v(logMessage.invoke())
    }
}

actual inline fun warn(logMessage: () -> String) {
    if (BuildKonfig.IS_LOG_ENABLED) {
        Timber.w(logMessage.invoke())
    }
}

actual inline fun debug(logMessage: () -> String) {
    if (BuildKonfig.IS_LOG_ENABLED) {
        Timber.d(logMessage.invoke())
    }
}

actual inline fun wtf(logMessage: () -> String) {
    if (BuildKonfig.IS_LOG_ENABLED) {
        Timber.wtf(logMessage.invoke())
    }
}

package com.flipperdevices.core.log

import timber.log.Timber

actual inline fun error(logMessage: () -> String) {
    if (BuildConfig.INTERNAL) {
        Timber.e(logMessage.invoke())
    }
}

actual inline fun error(error: Throwable, logMessage: () -> String) {
    if (BuildConfig.INTERNAL) {
        Timber.e(error, logMessage.invoke())
    }
}

actual inline fun info(logMessage: () -> String) {
    if (BuildConfig.INTERNAL) {
        Timber.i(logMessage.invoke())
    }
}

actual inline fun verbose(logMessage: () -> String) {
    if (BuildConfig.INTERNAL) {
        Timber.v(logMessage.invoke())
    }
}

actual inline fun warn(logMessage: () -> String) {
    if (BuildConfig.INTERNAL) {
        Timber.w(logMessage.invoke())
    }
}

actual inline fun debug(logMessage: () -> String) {
    if (BuildConfig.INTERNAL) {
        Timber.d(logMessage.invoke())
    }
}

actual inline fun wtf(logMessage: () -> String) {
    if (BuildConfig.INTERNAL) {
        Timber.wtf(logMessage.invoke())
    }
}

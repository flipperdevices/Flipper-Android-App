package com.flipperdevices.core.log

import timber.log.Timber

/**
 * Main idea of this class - remove debug log execution in prod
 */
inline fun error(logMessage: () -> String) {
    if (BuildConfig.INTERNAL) {
        Timber.e(logMessage.invoke())
    }
}

inline fun error(error: Throwable, logMessage: () -> String) {
    if (BuildConfig.INTERNAL) {
        Timber.e(error, logMessage.invoke())
    }
}

inline fun info(logMessage: () -> String) {
    if (BuildConfig.INTERNAL) {
        Timber.i(logMessage.invoke())
    }
}

inline fun verbose(logMessage: () -> String) {
    if (BuildConfig.INTERNAL) {
        Timber.v(logMessage.invoke())
    }
}

inline fun warn(logMessage: () -> String) {
    if (BuildConfig.INTERNAL) {
        Timber.w(logMessage.invoke())
    }
}

inline fun debug(logMessage: () -> String) {
    if (BuildConfig.INTERNAL) {
        Timber.d(logMessage.invoke())
    }
}

inline fun wtf(logMessage: () -> String) {
    if (BuildConfig.INTERNAL) {
        Timber.wtf(logMessage.invoke())
    }
}

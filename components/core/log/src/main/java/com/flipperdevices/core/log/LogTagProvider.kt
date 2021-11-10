package com.flipperdevices.core.log

import timber.log.Timber

@Suppress("PropertyName")
interface LogTagProvider {
    @Suppress("VariableNaming")
    val TAG: String
}

inline fun LogTagProvider.error(logMessage: () -> String) {
    if (BuildConfig.INTERNAL) {
        Timber.tag(TAG).e(logMessage.invoke())
    }
}

inline fun LogTagProvider.error(error: Throwable?, logMessage: () -> String) {
    if (BuildConfig.INTERNAL) {
        if (error == null) {
            Timber.tag(TAG).e(logMessage.invoke())
        } else {
            Timber.tag(TAG).e(error, logMessage.invoke())
        }
    }
}

inline fun LogTagProvider.info(logMessage: () -> String) {
    if (BuildConfig.INTERNAL) {
        Timber.tag(TAG).i(logMessage.invoke())
    }
}

inline fun LogTagProvider.verbose(logMessage: () -> String) {
    if (BuildConfig.INTERNAL) {
        Timber.tag(TAG).v(logMessage.invoke())
    }
}

inline fun LogTagProvider.warn(logMessage: () -> String) {
    if (BuildConfig.INTERNAL) {
        Timber.tag(TAG).w(logMessage.invoke())
    }
}

inline fun LogTagProvider.debug(logMessage: () -> String) {
    if (BuildConfig.INTERNAL) {
        Timber.tag(TAG).d(logMessage.invoke())
    }
}

inline fun LogTagProvider.wtf(logMessage: () -> String) {
    if (BuildConfig.INTERNAL) {
        Timber.tag(TAG).wtf(logMessage.invoke())
    }
}

package com.flipperdevices.core.log

import com.flipperdevices.core.buildkonfig.BuildKonfig
import timber.log.Timber

actual inline fun LogTagProvider.error(logMessage: () -> String) {
    if (BuildKonfig.IS_LOG_ENABLED) {
        Timber.tag(TAG).e(logMessage.invoke())
    }
}

actual inline fun LogTagProvider.error(error: Throwable?, logMessage: () -> String) {
    if (BuildKonfig.IS_LOG_ENABLED) {
        if (error == null) {
            Timber.tag(TAG).e(logMessage.invoke())
        } else {
            Timber.tag(TAG).e(error, logMessage.invoke())
        }
    }
}

actual inline fun LogTagProvider.info(logMessage: () -> String) {
    if (BuildKonfig.IS_LOG_ENABLED) {
        Timber.tag(TAG).i(logMessage.invoke())
    }
}

actual inline fun LogTagProvider.verbose(logMessage: () -> String) {
    if (BuildKonfig.IS_LOG_ENABLED) {
        Timber.tag(TAG).v(logMessage.invoke())
    }
}

actual inline fun LogTagProvider.warn(logMessage: () -> String) {
    if (BuildKonfig.IS_LOG_ENABLED) {
        Timber.tag(TAG).w(logMessage.invoke())
    }
}

actual inline fun LogTagProvider.debug(logMessage: () -> String) {
    if (BuildKonfig.IS_LOG_ENABLED) {
        Timber.tag(TAG).d(logMessage.invoke())
    }
}

actual inline fun LogTagProvider.wtf(logMessage: () -> String) {
    if (BuildKonfig.IS_LOG_ENABLED) {
        Timber.tag(TAG).wtf(logMessage.invoke())
    }
}

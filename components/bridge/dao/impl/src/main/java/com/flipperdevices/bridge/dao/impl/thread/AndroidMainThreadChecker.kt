package com.flipperdevices.bridge.dao.impl.thread

import android.os.Looper
import com.flipperdevices.core.log.BuildConfig

internal object AndroidMainThreadChecker : MainThreadChecker {
    override fun checkMainThread(message: () -> String) {
        if (BuildConfig.INTERNAL && Looper.getMainLooper() == Looper.myLooper()) {
            error(message.invoke())
        }
    }
}

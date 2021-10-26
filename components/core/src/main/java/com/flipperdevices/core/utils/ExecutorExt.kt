package com.flipperdevices.core.utils

import java.util.concurrent.Executor
import java.util.concurrent.Executors

fun newSingleThreadExecutor(name: String): Executor {
    return Executors.newSingleThreadExecutor {
        Thread(it, name)
    }
}

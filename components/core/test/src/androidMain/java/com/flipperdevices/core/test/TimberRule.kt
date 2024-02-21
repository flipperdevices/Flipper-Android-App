package com.flipperdevices.core.test

import org.junit.rules.TestWatcher
import org.junit.runner.Description
import timber.log.Timber

class TimberRule : TestWatcher() {

    private val printlnTree = object : Timber.DebugTree() {
        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
            println("$tag: $message")
        }
    }

    override fun starting(description: Description?) {
        super.starting(description)
        Timber.plant(printlnTree)
    }

    override fun finished(description: Description?) {
        super.finished(description)
        Timber.uproot(printlnTree)
    }
}

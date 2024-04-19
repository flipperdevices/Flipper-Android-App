package com.flipperdevices.singleactivity.impl.utils

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.flipperdevices.core.ktx.jre.FlipperDispatchers
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.metric.api.MetricApi
import com.flipperdevices.metric.api.events.SimpleEvent
import com.flipperdevices.selfupdater.api.SelfUpdaterApi
import com.flipperdevices.unhandledexception.api.UnhandledExceptionApi
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

private var appOpenReported = false

class OnCreateHandlerDispatcher @Inject constructor(
    private val unhandledExceptionApiProvider: Provider<UnhandledExceptionApi>,
    private val selfUpdaterApiProvider: Provider<SelfUpdaterApi>,
    private val metricApi: MetricApi
) : LogTagProvider {
    override val TAG = "OnCreateHandlerDispatcher"

    fun onCreate(lifecycleOwner: LifecycleOwner) {
        if (!appOpenReported) {
            metricApi.reportSimpleEvent(SimpleEvent.APP_OPEN)
            appOpenReported = true
        }

        try {
            unhandledExceptionApiProvider.get().initExceptionHandler()
        } catch (@Suppress("TooGenericExceptionCaught") throwable: Throwable) {
            error(throwable) { "Failed init unhandledExceptionApi" }
        }

        lifecycleOwner.lifecycleScope.launch(FlipperDispatchers.workStealingDispatcher) {
            lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                try {
                    selfUpdaterApiProvider.get().startCheckUpdate()
                } catch (@Suppress("TooGenericExceptionCaught") throwable: Throwable) {
                    error(throwable) { "Failed initial check update" }
                }
            }
        }
    }
}

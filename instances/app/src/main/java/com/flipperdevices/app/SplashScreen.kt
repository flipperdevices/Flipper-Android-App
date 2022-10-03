package com.flipperdevices.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.flipperdevices.app.di.MainComponent
import com.flipperdevices.bridge.synchronization.api.SynchronizationApi
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.ktx.android.toFullString
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.deeplink.api.DeepLinkParser
import com.flipperdevices.deeplink.model.Deeplink
import com.flipperdevices.metric.api.MetricApi
import com.flipperdevices.metric.api.events.SimpleEvent
import com.flipperdevices.singleactivity.api.SingleActivityApi
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SplashScreen : AppCompatActivity(), LogTagProvider {
    override val TAG = "SplashScreen"

    @Inject
    lateinit var singleActivityApi: SingleActivityApi

    @Inject
    lateinit var deepLinkParser: DeepLinkParser

    @Inject
    lateinit var synchronizationApi: SynchronizationApi

    @Inject
    lateinit var metricApi: MetricApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ComponentHolder.component<MainComponent>().inject(this)

        info { "Open SplashScreen with ${intent.toFullString()}" }

        metricApi.reportSimpleEvent(SimpleEvent.APP_OPEN)
        synchronizationApi.startSynchronization()

        // Open deeplink
        lifecycleScope.launch {
            val deeplink = deepLinkParser.fromIntent(this@SplashScreen, intent)
            withContext(Dispatchers.Main) {
                openSingleActivityAndFinish(deeplink)
            }
        }
    }

    private fun openSingleActivityAndFinish(deeplink: Deeplink?) {
        singleActivityApi.open(deeplink)
        finish()
    }
}

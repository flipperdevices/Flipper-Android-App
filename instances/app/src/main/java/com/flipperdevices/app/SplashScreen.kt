package com.flipperdevices.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.flipperdevices.app.di.MainComponent
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.deeplink.api.DeepLinkContentProvider
import com.flipperdevices.deeplink.model.Deeplink
import com.flipperdevices.pair.api.PairComponentApi
import com.flipperdevices.singleactivity.api.SingleActivityApi
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SplashScreen : AppCompatActivity(), LogTagProvider {
    override val TAG = "SplashScreen"

    @Inject
    lateinit var pairComponentApi: PairComponentApi

    @Inject
    lateinit var singleActivityApi: SingleActivityApi

    @Inject
    lateinit var deepLinkContentProvider: DeepLinkContentProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ComponentHolder.component<MainComponent>().inject(this)

        info { "Open SplashScreen with $intent" }

        // Open pair screen if not yet
        if (pairComponentApi.shouldWeOpenPairScreen()) {
            pairComponentApi.openPairScreen(this)
            return
        }

        // Open single activity if it is not deeplink
        val uri = intent.data
        if (uri == null) {
            openSingleActivityAndFinish(null)
            return
        }

        // Open deeplink
        lifecycleScope.launch {
            val deeplinkContent = deepLinkContentProvider.fromUri(this@SplashScreen, uri)
            val deeplink = Deeplink(deeplinkContent)
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

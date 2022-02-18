package com.flipperdevices.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.flipperdevices.app.di.MainComponent
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.deeplink.api.DeepLinkParser
import com.flipperdevices.deeplink.model.Deeplink
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ComponentHolder.component<MainComponent>().inject(this)

        info { "Open SplashScreen with $intent" }

        // Open single activity if it is not deeplink
        val uri = intent.data
        if (uri == null) {
            openSingleActivityAndFinish(null)
            return
        }

        // Open deeplink
        lifecycleScope.launch {
            val deeplink = deepLinkParser.fromUri(this@SplashScreen, uri)
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

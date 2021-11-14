package com.flipperdevices.app

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.flipperdevices.app.di.MainComponent
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.deeplink.model.Deeplink
import com.flipperdevices.deeplink.model.DeeplinkContent
import com.flipperdevices.pair.api.PairComponentApi
import com.flipperdevices.singleactivity.api.SingleActivityApi
import javax.inject.Inject

class SplashScreen : AppCompatActivity(), LogTagProvider {
    override val TAG = "SplashScreen"

    @Inject
    lateinit var pairComponentApi: PairComponentApi

    @Inject
    lateinit var singleActivityApi: SingleActivityApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ComponentHolder.component<MainComponent>().inject(this)

        info { "Open SplashScreen with $intent" }

        if (pairComponentApi.shouldWeOpenPairScreen()) {
            pairComponentApi.openPairScreen(this)
        } else {
            singleActivityApi.open(getDeepLink())
        }
        finish()
    }

    private fun getDeepLink(): Deeplink? {
        val uri = intent.data ?: return null
        contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
        return Deeplink(DeeplinkContent.ExternalUri(uri))
    }
}

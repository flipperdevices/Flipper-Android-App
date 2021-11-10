package com.flipperdevices.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.flipperdevices.app.di.MainComponent
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.pair.api.PairComponentApi
import com.flipperdevices.singleactivity.api.SingleActivityApi
import javax.inject.Inject

class SplashScreen : AppCompatActivity() {
    @Inject
    lateinit var pairComponentApi: PairComponentApi

    @Inject
    lateinit var singleActivityApi: SingleActivityApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ComponentHolder.component<MainComponent>().inject(this)
        if (pairComponentApi.shouldWeOpenPairScreen()) {
            pairComponentApi.openPairScreen(this)
        } else {
            singleActivityApi.open()
        }
        finish()
    }
}

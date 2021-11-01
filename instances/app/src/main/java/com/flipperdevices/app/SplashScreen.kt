package com.flipperdevices.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.flipperdevices.app.di.MainComponent
import com.flipperdevices.bottombar.api.BottomNavigationActivityApi
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.pair.api.PairComponentApi
import javax.inject.Inject

class SplashScreen : AppCompatActivity() {
    @Inject
    lateinit var pairComponentApi: PairComponentApi

    @Inject
    lateinit var bottomNavigationActivityApi: BottomNavigationActivityApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ComponentHolder.component<MainComponent>().inject(this)
        if (pairComponentApi.shouldWeOpenPairScreen()) {
            pairComponentApi.openPairScreen(this)
        } else {
            bottomNavigationActivityApi.openBottomNavigationScreen()
        }
        finish()
    }
}

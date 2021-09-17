package com.flipper.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.flipper.app.di.MainComponent
import com.flipper.core.api.BottomNavigationActivityApi
import com.flipper.core.api.PairComponentApi
import com.flipper.core.di.ComponentHolder
import javax.inject.Inject

class SplashScreen : AppCompatActivity() {
    @Inject
    lateinit var pairComponentApi: PairComponentApi

    @Inject
    lateinit var bottomNavigationActivityApi: BottomNavigationActivityApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ComponentHolder.component<MainComponent>().inject(this)
        if (pairComponentApi.isAtLeastOneTimePaired()) {
            bottomNavigationActivityApi.openBottomNavigationScreen()
        } else {
            pairComponentApi.openPairScreen(this)
        }
        finish()
    }
}

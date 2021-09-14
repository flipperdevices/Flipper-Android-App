package com.flipper.app

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.flipper.app.di.MainComponent
import com.flipper.bottombar.main.BottomNavigationActivity
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
            bottomNavigationActivityApi.openBottomNavigationScreen(this)
        } else {
            pairComponentApi.openPairScreen(this)
        }
        startActivity(
            Intent(this, BottomNavigationActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
        )
        finish()
    }
}

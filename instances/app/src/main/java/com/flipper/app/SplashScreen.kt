package com.flipper.app

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.flipper.app.di.MainComponent
import com.flipper.core.di.ComponentHolder
import com.flipper.core.utils.preference.FlipperSharedPreferences
import javax.inject.Inject

class SplashScreen : AppCompatActivity() {
    @Inject
    lateinit var sharedPreferences: FlipperSharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ComponentHolder.component<MainComponent>().inject(this)
        // We are warming up the Shared Preference here because we will need it afterwards.
        // It's better for the user to see a nice logo a little more than just a white screen.
        sharedPreferences.warmUp()
        startActivity(
            Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
        )
        finish()
    }
}

package com.flipper.app

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.flipper.app.databinding.ActivityMainBinding
import com.flipper.core.navigation.delegates.OnBackPressListener
import com.flipper.core.navigation.screen.PairScreenProvider
import com.github.terrakok.cicerone.NavigatorHolder
import com.github.terrakok.cicerone.Router
import com.github.terrakok.cicerone.androidx.AppNavigator
import com.lionzxy.trex_offline.TRexOfflineActivity
import javax.inject.Inject

const val ALPHA_VERSION_TEXT = 0.25F

class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var navigatorHolder: NavigatorHolder

    @Inject
    lateinit var router: Router

    @Inject
    lateinit var pairScreenProvider: PairScreenProvider

    private val navigator = AppNavigator(this, R.id.container)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FlipperApplication.component.inject(this)

        if (savedInstanceState == null) {
            router.replaceScreen(pairScreenProvider.startPairScreen())
        }

        if (BuildConfig.INTERNAL) {
            binding.versionName.visibility = View.VISIBLE
            binding.versionName.text = BuildConfig.VERSION_NAME
            binding.versionName.alpha = ALPHA_VERSION_TEXT
            binding.versionName.setOnClickListener {
                TRexOfflineActivity.open(this)
                false
            }
        }
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        navigatorHolder.setNavigator(navigator)
    }

    override fun onPause() {
        navigatorHolder.removeNavigator()
        super.onPause()
    }

    override fun onBackPressed() {
        val fragment = supportFragmentManager.findFragmentById(R.id.content)
        if ((fragment as? OnBackPressListener)?.onBackPressed() == true) {
            return
        } else {
            super.onBackPressed()
        }
    }
}

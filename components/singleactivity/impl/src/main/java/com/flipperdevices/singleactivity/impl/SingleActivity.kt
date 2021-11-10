package com.flipperdevices.singleactivity.impl

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.flipperdevices.bottombar.api.BottomNavigationActivityApi
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.navigation.delegates.OnBackPressListener
import com.flipperdevices.core.navigation.global.CiceroneGlobal
import com.flipperdevices.singleactivity.impl.databinding.SingleActivityBinding
import com.flipperdevices.singleactivity.impl.di.SingleActivityComponent
import com.github.terrakok.cicerone.Navigator
import com.github.terrakok.cicerone.androidx.AppNavigator
import javax.inject.Inject

class SingleActivity : AppCompatActivity() {
    @Inject
    lateinit var cicerone: CiceroneGlobal

    @Inject
    lateinit var bottomBarApi: BottomNavigationActivityApi

    lateinit var binding: SingleActivityBinding

    private val navigator: Navigator = AppNavigator(this, R.id.fragment_container)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ComponentHolder.component<SingleActivityComponent>().inject(this)
        binding = SingleActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            cicerone.getRouter().newRootScreen(bottomBarApi.getBottomNavigationFragment())
        }
    }

    override fun onResume() {
        super.onResume()
        cicerone.getNavigationHolder().setNavigator(navigator)
    }

    override fun onPause() {
        cicerone.getNavigationHolder().removeNavigator()
        super.onPause()
    }

    override fun onBackPressed() {
        val currentFragment: Fragment? = supportFragmentManager.fragments.find { it.isVisible }
        if ((currentFragment as? OnBackPressListener)?.onBackPressed() == true) {
            return
        } else {
            cicerone.getRouter().exit()
        }
    }
}

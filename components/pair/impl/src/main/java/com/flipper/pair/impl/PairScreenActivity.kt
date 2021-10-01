package com.flipper.pair.impl

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.flipper.core.di.ComponentHolder
import com.flipper.core.navigation.delegates.OnBackPressListener
import com.flipper.pair.impl.di.PairComponent
import com.flipper.pair.impl.navigation.machine.PairScreenStateDispatcher
import com.flipper.pair.impl.navigation.storage.PairStateStorage
import com.github.terrakok.cicerone.NavigatorHolder
import com.github.terrakok.cicerone.androidx.AppNavigator
import javax.inject.Inject

class PairScreenActivity : FragmentActivity() {
    @Inject
    lateinit var navigatorHolder: NavigatorHolder

    @Inject
    lateinit var stateDispatcher: PairScreenStateDispatcher

    @Inject
    lateinit var pairStateStorage: PairStateStorage

    private val navigator = AppNavigator(this, R.id.container)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pair)
        ComponentHolder.component<PairComponent>().inject(this)

        if (savedInstanceState == null) {
            stateDispatcher.invalidate(pairStateStorage.getSavedPairState())
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
        val fragment = supportFragmentManager.findFragmentById(R.id.container)
        if ((fragment as? OnBackPressListener)?.onBackPressed() == true) {
            return
        } else {
            stateDispatcher.back()
        }
    }
}

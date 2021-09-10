package com.flipper.bottombar.main

import android.os.Bundle
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.FragmentActivity
import com.flipper.bottombar.R
import com.flipper.bottombar.di.BottomBarComponent
import com.flipper.bottombar.main.compose.ComposeBottomBar
import com.flipper.core.di.ComponentHolder
import com.github.terrakok.cicerone.NavigatorHolder
import com.github.terrakok.cicerone.Router
import com.github.terrakok.cicerone.androidx.AppNavigator
import com.github.terrakok.cicerone.androidx.FragmentScreen
import javax.inject.Inject

class BottomNavigationActivity : FragmentActivity() {
    @Inject
    lateinit var router: Router

    @Inject
    lateinit var navigatorHolder: NavigatorHolder

    private val navigator = AppNavigator(this, R.id.fragment_container)

    override fun onCreate(savedInstanceState: Bundle?) {
        ComponentHolder.component<BottomBarComponent>().inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_with_bottombar)

        findViewById<ComposeView>(R.id.bottom_bar).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                ComposeBottomBar()
            }
        }
        if (savedInstanceState == null) {
            router.replaceScreen(FragmentScreen { TestFragment() })
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

}
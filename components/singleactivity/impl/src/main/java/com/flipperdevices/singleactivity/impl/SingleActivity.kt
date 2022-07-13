package com.flipperdevices.singleactivity.impl

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.flipperdevices.bottombar.api.BottomNavigationApi
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.core.navigation.delegates.OnBackPressListener
import com.flipperdevices.core.navigation.delegates.RouterProvider
import com.flipperdevices.core.navigation.global.CiceroneGlobal
import com.flipperdevices.deeplink.api.DeepLinkDispatcher
import com.flipperdevices.deeplink.model.Deeplink
import com.flipperdevices.firstpair.api.FirstPairApi
import com.flipperdevices.singleactivity.impl.databinding.SingleActivityBinding
import com.flipperdevices.singleactivity.impl.di.SingleActivityComponent
import com.flipperdevices.updater.api.UpdaterApi
import com.flipperdevices.updater.api.UpdaterUIApi
import com.github.terrakok.cicerone.Navigator
import com.github.terrakok.cicerone.Router
import com.github.terrakok.cicerone.androidx.AppNavigator
import java.util.EmptyStackException
import java.util.Stack
import javax.inject.Inject

const val LAUNCH_PARAMS_INTENT = "launch_params_intent"

class SingleActivity : AppCompatActivity(), RouterProvider, LogTagProvider {
    override val TAG = "SingleActivity"

    @Inject
    lateinit var cicerone: CiceroneGlobal

    @Inject
    lateinit var bottomBarApi: BottomNavigationApi

    @Inject
    lateinit var deepLinkDispatcher: DeepLinkDispatcher

    @Inject
    lateinit var firstPairApi: FirstPairApi

    @Inject
    lateinit var updaterApi: UpdaterApi

    @Inject
    lateinit var updaterUIApi: UpdaterUIApi

    lateinit var binding: SingleActivityBinding

    override val router: Router
        get() = cicerone.getRouter()

    private val navigator: Navigator = AppNavigator(this, R.id.fragment_container)

    // TODO move to singleton/persist storage
    private val deeplinkStack = Stack<Deeplink>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ComponentHolder.component<SingleActivityComponent>().inject(this)
        binding = SingleActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        info {
            "Create new activity with hashcode: ${SingleActivity::class.hashCode()}. " +
                "Deeplink size is ${deeplinkStack.size}"
        }

        if (savedInstanceState == null) {
            val deeplink = intent.getParcelableExtra<Deeplink>(LAUNCH_PARAMS_INTENT)
            info { "Initial open activity with deeplink $deeplink" }
            if (deeplink != null) {
                deeplinkStack.push(deeplink)
            }
            invalidate()
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val deeplink = intent?.getParcelableExtra<Deeplink>(LAUNCH_PARAMS_INTENT)
        info { "Nonfirst open activity with deeplink $deeplink" }
        if (deeplink != null) {
            deeplinkStack.push(deeplink)
        }
        invalidate()
    }

    fun invalidate() {
        info { "Open clear screen, pending deeplinks size is ${deeplinkStack.size}" }

        if (firstPairApi.shouldWeOpenPairScreen()) {
            cicerone.getRouter().newRootScreen(firstPairApi.getFirstPairScreen())
            return
        }

        if (updaterApi.isUpdateInProcess()) {
            updaterUIApi.openUpdateScreen(silent = true)
            return
        }

        cicerone.getRouter().newRootScreen(bottomBarApi.getBottomNavigationFragment())
        try {
            val deeplink = deeplinkStack.pop()
            info { "Process deeplink $deeplink" }
            deepLinkDispatcher.process(router, deeplink)
        } catch (ignored: EmptyStackException) {
            // Ignore it, normal behavior
        }
    }

    override fun onResume() {
        super.onResume()
        SingleActivityHolder.setUpSingleActivity(this)
        cicerone.getNavigationHolder().setNavigator(navigator)
    }

    override fun onPause() {
        cicerone.getNavigationHolder().removeNavigator()
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        SingleActivityHolder.removeSingleActivity()
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

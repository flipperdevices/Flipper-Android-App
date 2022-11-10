package com.flipperdevices.singleactivity.impl

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.flipperdevices.bridge.synchronization.api.SynchronizationApi
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.ktx.android.toFullString
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.core.navigation.delegates.OnBackPressListener
import com.flipperdevices.core.navigation.delegates.RouterProvider
import com.flipperdevices.core.navigation.global.CiceroneGlobal
import com.flipperdevices.deeplink.model.Deeplink
import com.flipperdevices.metric.api.MetricApi
import com.flipperdevices.metric.api.events.SimpleEvent
import com.flipperdevices.singleactivity.impl.databinding.SingleActivityBinding
import com.flipperdevices.singleactivity.impl.di.SingleActivityComponent
import com.github.terrakok.cicerone.Navigator
import com.github.terrakok.cicerone.Router
import com.github.terrakok.cicerone.androidx.AppNavigator
import javax.inject.Inject
import kotlinx.coroutines.launch

const val LAUNCH_PARAMS_INTENT = "launch_params_intent"

class SingleActivity :
    AppCompatActivity(),
    RouterProvider,
    LogTagProvider {
    override val TAG = "SingleActivity"

    @Inject
    lateinit var cicerone: CiceroneGlobal

    @Inject
    lateinit var deepLinkHelper: DeepLinkHelper

    @Inject
    lateinit var metricApi: MetricApi

    @Inject
    lateinit var synchronizationApi: SynchronizationApi

    lateinit var binding: SingleActivityBinding

    override val router: Router
        get() = cicerone.getRouter()

    private val navigator: Navigator = AppNavigator(this, R.id.fragment_container)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ComponentHolder.component<SingleActivityComponent>().inject(this)
        binding = SingleActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        info {
            "Create new activity with hashcode: ${this.hashCode()} " +
                "and intent ${intent.toFullString()}"
        }

        if (savedInstanceState != null) {
            return
        }

        metricApi.reportSimpleEvent(SimpleEvent.APP_OPEN)
        synchronizationApi.startSynchronization()

        // Open deeplink
        lifecycleScope.launch {
            deepLinkHelper.onNewIntent(this@SingleActivity, intent)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent == null) {
            return
        }
        val deeplink = intent.getParcelableExtra<Deeplink>(LAUNCH_PARAMS_INTENT)
        if (deeplink != null) {
            deepLinkHelper.onNewDeeplink(deeplink)
            return
        }
        lifecycleScope.launch {
            deepLinkHelper.onNewIntent(this@SingleActivity, intent)
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

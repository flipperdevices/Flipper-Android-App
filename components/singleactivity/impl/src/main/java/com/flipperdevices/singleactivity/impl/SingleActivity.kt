package com.flipperdevices.singleactivity.impl

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.CompositionLocalProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.flipperdevices.bottombar.api.BottomNavigationFeatureEntry
import com.flipperdevices.bridge.synchronization.api.SynchronizationApi
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.ktx.android.parcelableExtra
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.navigation.delegates.RouterProvider
import com.flipperdevices.core.navigation.global.CiceroneGlobal
import com.flipperdevices.core.ui.navigation.AggregateFeatureEntry
import com.flipperdevices.core.ui.navigation.ComposableFeatureEntry
import com.flipperdevices.core.ui.navigation.LocalGlobalNavigationNavStack
import com.flipperdevices.core.ui.theme.FlipperTheme
import com.flipperdevices.deeplink.model.Deeplink
import com.flipperdevices.metric.api.MetricApi
import com.flipperdevices.singleactivity.impl.composable.ComposableSingleActivityNavHost
import com.flipperdevices.singleactivity.impl.di.SingleActivityComponent
import com.github.terrakok.cicerone.Router
import javax.inject.Inject
import kotlinx.collections.immutable.toPersistentSet
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

    @Inject
    lateinit var bottomNavigationFeatureEntry: BottomNavigationFeatureEntry

    @Inject
    lateinit var featureEntriesMutable: MutableSet<AggregateFeatureEntry>

    @Inject
    lateinit var composableEntriesMutable: MutableSet<ComposableFeatureEntry>

    override val router: Router
        get() = cicerone.getRouter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ComponentHolder.component<SingleActivityComponent>().inject(this)
        setContent {
            val globalNavController = rememberNavController()
            FlipperTheme(content = {
                CompositionLocalProvider(
                    LocalGlobalNavigationNavStack provides globalNavController
                ) {
                    ComposableSingleActivityNavHost(
                        navController = globalNavController,
                        bottomNavigationFeatureEntry = bottomNavigationFeatureEntry,
                        featureEntries = featureEntriesMutable.toPersistentSet(),
                        composableEntries = composableEntriesMutable.toPersistentSet()
                    )
                }
            })
        }


        /*binding = SingleActivityBinding.inflate(layoutInflater)
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
        }*/
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent == null) {
            return
        }
        val deeplink = intent.parcelableExtra<Deeplink>(LAUNCH_PARAMS_INTENT)
        if (deeplink != null) {
            deepLinkHelper.onNewDeeplink(deeplink)
            return
        }
        lifecycleScope.launch {
            deepLinkHelper.onNewIntent(this@SingleActivity, intent)
        }
    }
}

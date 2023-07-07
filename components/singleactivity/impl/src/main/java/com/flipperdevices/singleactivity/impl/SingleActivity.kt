package com.flipperdevices.singleactivity.impl

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.ktx.android.parcelableExtra
import com.flipperdevices.core.ktx.android.toFullString
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.core.ui.navigation.AggregateFeatureEntry
import com.flipperdevices.core.ui.navigation.ComposableFeatureEntry
import com.flipperdevices.core.ui.navigation.LocalGlobalNavigationNavStack
import com.flipperdevices.core.ui.theme.FlipperTheme
import com.flipperdevices.deeplink.model.Deeplink
import com.flipperdevices.selfupdater.api.SelfUpdaterApi
import com.flipperdevices.singleactivity.impl.composable.ComposableSingleActivityNavHost
import com.flipperdevices.singleactivity.impl.di.SingleActivityComponent
import com.flipperdevices.singleactivity.impl.utils.AppOpenMetricReported
import kotlinx.collections.immutable.toPersistentSet
import kotlinx.coroutines.launch
import javax.inject.Inject

const val LAUNCH_PARAMS_INTENT = "launch_params_intent"

class SingleActivity :
    AppCompatActivity(),
    LogTagProvider {
    override val TAG = "SingleActivity"

    @Inject
    lateinit var deepLinkHelper: DeepLinkHelper

    @Inject
    lateinit var featureEntriesMutable: MutableSet<AggregateFeatureEntry>

    @Inject
    lateinit var composableEntriesMutable: MutableSet<ComposableFeatureEntry>

    @Inject
    lateinit var selfUpdaterApi: SelfUpdaterApi

    @Inject
    lateinit var appOpenMetricReported: AppOpenMetricReported

    private var globalNavController: NavHostController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ComponentHolder.component<SingleActivityComponent>().inject(this)

        info {
            "Create new activity with hashcode: ${this.hashCode()} " +
                "and intent ${intent.toFullString()}"
        }

        appOpenMetricReported.report()

        WindowCompat.setDecorFitsSystemWindows(window, false)

        selfUpdaterApi.startCheckUpdateAsync(activity = this@SingleActivity)

        val featureEntries = featureEntriesMutable.toPersistentSet()
        val composableEntries = composableEntriesMutable.toPersistentSet()
        val startDestination = deepLinkHelper.getStartDestination()

        setContent {
            val navControllerLocal = rememberNavController().also {
                globalNavController = it
            }
            LaunchedEffect(Unit) {
                // Open deeplink
                deepLinkHelper.onNewIntent(this@SingleActivity, navControllerLocal, intent)
            }
            FlipperTheme(content = {
                CompositionLocalProvider(
                    LocalGlobalNavigationNavStack provides navControllerLocal
                ) {
                    ComposableSingleActivityNavHost(
                        navController = navControllerLocal,
                        startDestination = startDestination,
                        featureEntries = featureEntries,
                        composableEntries = composableEntries,
                        modifier = Modifier
                            .safeDrawingPadding()
                    )
                }
            })
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        info { "Receive new intent: ${intent?.toFullString()}" }
        if (intent == null) {
            return
        }
        val deeplink = intent.parcelableExtra<Deeplink>(LAUNCH_PARAMS_INTENT)
        if (deeplink != null) {
            lifecycleScope.launch {
                globalNavController?.let { navController ->
                    deepLinkHelper.onNewDeeplink(navController, deeplink)
                }
            }
            return
        }
        lifecycleScope.launch {
            globalNavController?.let { navController ->
                deepLinkHelper.onNewIntent(this@SingleActivity, navController, intent)
            }
        }
    }
}

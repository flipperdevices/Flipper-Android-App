package com.flipperdevices.singleactivity.impl

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.arkivanov.decompose.defaultComponentContext
import com.arkivanov.decompose.extensions.compose.stack.animation.LocalStackAnimationProvider
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.ktx.android.parcelableExtra
import com.flipperdevices.core.ktx.android.toFullString
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.core.ui.theme.FlipperTheme
import com.flipperdevices.deeplink.model.Deeplink
import com.flipperdevices.metric.api.MetricApi
import com.flipperdevices.metric.api.events.SessionState
import com.flipperdevices.rootscreen.api.LocalRootNavigation
import com.flipperdevices.rootscreen.api.RootDecomposeComponent
import com.flipperdevices.singleactivity.impl.di.SingleActivityComponent
import com.flipperdevices.singleactivity.impl.utils.FlipperStackAnimationProvider
import com.flipperdevices.singleactivity.impl.utils.OnCreateHandlerDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

const val LAUNCH_PARAMS_INTENT = "launch_params_intent"

@Suppress("ForbiddenComment")
class SingleActivity : AppCompatActivity(), LogTagProvider {
    override val TAG = "SingleActivity"

    @Inject
    lateinit var rootComponentFactory: RootDecomposeComponent.Factory

    @Inject
    lateinit var metricApi: MetricApi

    @Inject
    lateinit var onCreateHandlerDispatcher: OnCreateHandlerDispatcher

    private var rootDecomposeComponent: RootDecomposeComponent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ComponentHolder.component<SingleActivityComponent>().inject(this)

        onCreateHandlerDispatcher.onCreate(this)

        info {
            "Create new activity with hashcode: ${this.hashCode()} " + "and intent ${intent.toFullString()}"
        }

        WindowCompat.setDecorFitsSystemWindows(window, false)

        val root = rootComponentFactory(
            componentContext = defaultComponentContext(),
            onBack = this::finish
        ).also { rootDecomposeComponent = it }

        setContent {
            LaunchedEffect(Unit) {
                // Open deeplink
                // TODO: Process deeplink
            }
            FlipperTheme(content = {
                CompositionLocalProvider(
                    LocalRootNavigation provides root,
                    LocalStackAnimationProvider provides FlipperStackAnimationProvider
                ) {
                    root.Render(
                        modifier = Modifier
                            .safeDrawingPadding()
                            .fillMaxSize()
                            .background(MaterialTheme.colors.background)
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
                // TODO: Process deeplink
            }
            return
        }
        lifecycleScope.launch {
            // TODO: Process deeplink
        }
    }

    override fun onStart() {
        super.onStart()
        metricApi.reportSessionState(SessionState.StartSession(this))
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        metricApi.reportSessionState(SessionState.ConfigurationChanged(newConfig))
    }

    override fun onStop() {
        super.onStop()
        metricApi.reportSessionState(SessionState.StopSession)
    }
}

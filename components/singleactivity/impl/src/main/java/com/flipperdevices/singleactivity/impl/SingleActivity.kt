package com.flipperdevices.singleactivity.impl

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.arkivanov.decompose.defaultComponentContext
import com.arkivanov.decompose.extensions.compose.stack.animation.LocalStackAnimationProvider
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.ktx.android.toFullString
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.core.ui.lifecycle.viewModelWithFactory
import com.flipperdevices.core.ui.theme.FlipperTheme
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.viewmodel.ThemeViewModel
import com.flipperdevices.deeplink.api.DeepLinkParser
import com.flipperdevices.deeplink.model.Deeplink
import com.flipperdevices.metric.api.MetricApi
import com.flipperdevices.metric.api.events.SessionState
import com.flipperdevices.rootscreen.api.LocalDeeplinkHandler
import com.flipperdevices.rootscreen.api.LocalRootNavigation
import com.flipperdevices.rootscreen.api.RootDecomposeComponent
import com.flipperdevices.singleactivity.impl.di.SingleActivityComponent
import com.flipperdevices.singleactivity.impl.utils.FlipperStackAnimationProvider
import com.flipperdevices.singleactivity.impl.utils.OnCreateHandlerDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Provider

class SingleActivity : AppCompatActivity(), LogTagProvider {
    override val TAG = "SingleActivity"

    @Inject
    lateinit var rootComponentFactory: RootDecomposeComponent.Factory

    @Inject
    lateinit var metricApi: MetricApi

    @Inject
    lateinit var onCreateHandlerDispatcher: OnCreateHandlerDispatcher

    @Inject
    lateinit var deeplinkParser: DeepLinkParser

    @Inject
    lateinit var themeViewModelProvider: Provider<ThemeViewModel>

    private var rootDecomposeComponent: RootDecomposeComponent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ComponentHolder.component<SingleActivityComponent>().inject(this)

        onCreateHandlerDispatcher.onCreate(this)

        info {
            "Create new activity with hashcode: ${this.hashCode()} " + "and intent ${intent.toFullString()}"
        }

        enableEdgeToEdge()

        val root = rootComponentFactory(
            componentContext = defaultComponentContext(),
            onBack = this::finish,
            initialDeeplink = runBlocking {
                deeplinkParser.parseOrLog(this@SingleActivity, intent)
            }
        ).also { rootDecomposeComponent = it }

        setContent {
            FlipperTheme(
                content = {
                    CompositionLocalProvider(
                        LocalRootNavigation provides root,
                        LocalDeeplinkHandler provides root,
                        LocalStackAnimationProvider provides FlipperStackAnimationProvider
                    ) {
                        root.Render(
                            modifier = Modifier
                                .fillMaxSize()
                                .imePadding()
                                .background(LocalPallet.current.background)
                        )
                    }
                },
                themeViewModel = root.viewModelWithFactory(key = null) {
                    themeViewModelProvider.get()
                }
            )
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        info { "Receive new intent: ${intent?.toFullString()}" }
        if (intent == null) {
            return
        }
        lifecycleScope.launch(Dispatchers.Default) {
            deeplinkParser.parseOrLog(this@SingleActivity, intent)?.let {
                rootDecomposeComponent?.handleDeeplink(it)
            }
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

    private suspend fun DeepLinkParser.parseOrLog(context: Context, intent: Intent): Deeplink? {
        return try {
            fromIntent(context, intent)
        } catch (throwable: Exception) {
            error(throwable) { "Failed parse deeplink" }
            null
        }
    }
}

package com.flipperdevices.wearable

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.arkivanov.decompose.defaultComponentContext
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.core.log.warn
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.wearable.core.ui.ktx.WearFlipperTheme
import com.flipperdevices.wearable.di.WearableComponent
import com.flipperdevices.wearable.emulate.api.ChannelClientHelper
import com.flipperdevices.wearable.sync.wear.api.FindPhoneApi
import com.flipperdevices.wearrootscreen.api.WearRootDecomposeComponent
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.CapabilityInfo
import com.google.android.gms.wearable.ChannelClient
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

private const val CAPABILITY_PHONE_APP = "verify_remote_flipper_phone_app"

class MainWearActivity : ComponentActivity(), LogTagProvider {
    override val TAG: String = "MainWearActivity"

    @Inject
    lateinit var channelClientHelper: ChannelClientHelper

    @Inject
    lateinit var findPhoneApi: FindPhoneApi

    @Inject
    lateinit var rootScreenFactory: WearRootDecomposeComponent.Factory

    private var activeChannel: ChannelClient.Channel? = null

    private val channelClient: ChannelClient by lazy { Wearable.getChannelClient(this) }
    private val capabilityClient: CapabilityClient by lazy { Wearable.getCapabilityClient(this) }

    private val channelClientCallback = object : ChannelClient.ChannelCallback() {
        override fun onChannelClosed(
            channel: ChannelClient.Channel,
            closeReason: Int,
            appSpecificErrorCode: Int
        ) {
            super.onChannelClosed(channel, closeReason, appSpecificErrorCode)
            info { "#channelClientCallback onChannelClosed $closeReason" }
            lifecycleScope.launch(Dispatchers.Default) {
                activeChannel = if (closeReason == CLOSE_REASON_REMOTE_CLOSE) {
                    // Close service only, try reset
                    channelClientHelper.onChannelReset(lifecycleScope)
                } else {
                    // Off connection from phone(bt)
                    channelClientHelper.onChannelClose(lifecycleScope)
                    null
                }
            }
        }
    }

    private val capabilityClientCallback =
        CapabilityClient.OnCapabilityChangedListener { capabilityInfo ->
            lifecycleScope.launch(Dispatchers.Default) {
                val successFindNode = capabilityUpdate(capabilityInfo)
                if (successFindNode) {
                    // Success find phone, try to reopen channel
                    activeChannel = channelClientHelper.onChannelOpen(lifecycleScope)
                }
            }
        }

    private suspend fun capabilityUpdate(capabilityInfo: CapabilityInfo): Boolean {
        val node = capabilityInfo.nodes.firstOrNull { it.isNearby }
        val nodeId = node?.id

        info { "#processCapabilityInfo $nodeId" }
        findPhoneApi.update(nodeId)

        return nodeId != null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)
        info { "#onCreate" }

        ComponentHolder.component<WearableComponent>().inject(this)
        channelClient.registerChannelCallback(channelClientCallback)
        capabilityClient.addListener(capabilityClientCallback, CAPABILITY_PHONE_APP)

        lifecycleScope.launch(Dispatchers.Default) {
            val capabilityInfo = capabilityClient.getCapability(
                CAPABILITY_PHONE_APP,
                CapabilityClient.FILTER_ALL
            ).await()
            capabilityUpdate(capabilityInfo)
            activeChannel = channelClientHelper.onChannelOpen(lifecycleScope)
        }

        val rootComponent = rootScreenFactory(
            componentContext = defaultComponentContext(),
            onBack = { finish() }
        )

        setContent {
            WearFlipperTheme {
                SwipeToDismissBox(
                    stack = rootComponent.stack,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(LocalPallet.current.background),
                    onDismissed = rootComponent::onBack
                ) {
                    it.instance.Render()
                }
            }
        }
    }

    override fun onDestroy() {
        info { "#onDestroy" }
        runBlocking {
            channelClient.unregisterChannelCallback(channelClientCallback)
            capabilityClient.removeListener(capabilityClientCallback, CAPABILITY_PHONE_APP)
            closeChannel()
        }
        super.onDestroy()
    }

    private suspend fun closeChannel() {
        val currentChannel = activeChannel
        if (currentChannel == null) {
            warn { "Active channel was null" }
            return
        }

        runCatching {
            channelClient.close(currentChannel).await()
        }.onFailure {
            warn { "Failed to close channel" }
        }.onSuccess {
            info { "Channel closed" }
        }
    }
}

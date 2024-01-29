package com.flipperdevices.unhandledexception.impl.api

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.datastore.core.DataStore
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.di.provideDelegate
import com.flipperdevices.core.preference.pb.Settings
import com.flipperdevices.deeplink.model.Deeplink
import com.flipperdevices.deeplink.model.DeeplinkBottomBarTab
import com.flipperdevices.rootscreen.api.LocalDeeplinkHandler
import com.flipperdevices.unhandledexception.api.UnhandledExceptionRenderApi
import com.flipperdevices.unhandledexception.impl.composable.ComposableUnhandledExceptionDialog
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Provider

@ContributesBinding(AppGraph::class, UnhandledExceptionRenderApi::class)
class UnhandledExceptionRenderApiImpl @Inject constructor(
    dataStoreProvider: Provider<DataStore<Settings>>,
) : UnhandledExceptionRenderApi {

    val dataStore by dataStoreProvider

    @Composable
    override fun ComposableUnhandledExceptionRender(modifier: Modifier) {
        val settingData by dataStore.data.collectAsState(
            initial = null
        )

        if (settingData?.fatalBleSecurityExceptionHappens == true) {
            val deeplinkHandler = LocalDeeplinkHandler.current
            ComposableUnhandledExceptionDialog(
                modifier = modifier,
                onDismiss = {
                    runBlocking {
                        dataStore.updateData {
                            it.toBuilder()
                                .setFatalBleSecurityExceptionHappens(false)
                                .build()
                        }
                    }
                    deeplinkHandler.handleDeeplink(Deeplink.BottomBar.OpenTab(DeeplinkBottomBarTab.DEVICE))
                }
            )
        }
    }
}

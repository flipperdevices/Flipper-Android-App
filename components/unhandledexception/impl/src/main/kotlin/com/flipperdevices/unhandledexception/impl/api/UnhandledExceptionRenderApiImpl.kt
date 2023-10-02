package com.flipperdevices.unhandledexception.impl.api

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.datastore.core.DataStore
import com.flipperdevices.bottombar.api.BottomNavigationHandleDeeplink
import com.flipperdevices.bottombar.model.BottomBarTab
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.di.provideDelegate
import com.flipperdevices.core.preference.pb.Settings
import com.flipperdevices.unhandledexception.api.UnhandledExceptionRenderApi
import com.flipperdevices.unhandledexception.impl.composable.ComposableUnhandledExceptionDialog
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Provider

@ContributesBinding(AppGraph::class, UnhandledExceptionRenderApi::class)
class UnhandledExceptionRenderApiImpl @Inject constructor(
    dataStoreProvider: Provider<DataStore<Settings>>,
    bottomBarApiProvider: Provider<BottomNavigationHandleDeeplink>
) : UnhandledExceptionRenderApi {

    val dataStore by dataStoreProvider
    val bottomBarApi by bottomBarApiProvider

    @Composable
    override fun ComposableUnhandledExceptionRender(modifier: Modifier) {
        val settingData by dataStore.data.collectAsState(
            initial = null
        )

        if (settingData?.fatalBleSecurityExceptionHappens == true) {
            ComposableUnhandledExceptionDialog(
                modifier = modifier,
                onDismiss = {
                    bottomBarApi.onChangeTab(BottomBarTab.DEVICE, force = true)
                    runBlocking {
                        dataStore.updateData {
                            it.toBuilder()
                                .setFatalBleSecurityExceptionHappens(false)
                                .build()
                        }
                    }
                }
            )
        }
    }
}

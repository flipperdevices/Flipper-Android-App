package com.flipperdevices.unhandledexception.impl.api

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.core.DataStore
import com.flipperdevices.bottombar.api.BottomNavigationHandleDeeplink
import com.flipperdevices.bottombar.model.BottomBarTab
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.di.provideDelegate
import com.flipperdevices.core.preference.pb.Settings
import com.flipperdevices.core.ui.dialog.composable.multichoice.FlipperMultiChoiceDialog
import com.flipperdevices.core.ui.dialog.composable.multichoice.FlipperMultiChoiceDialogModel
import com.flipperdevices.unhandledexception.api.UnhandledExceptionApi
import com.flipperdevices.unhandledexception.api.UnhandledExceptionRenderApi
import com.flipperdevices.unhandledexception.impl.R
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Provider

@ContributesBinding(AppGraph::class, UnhandledExceptionRenderApi::class)
class UnhandledExceptionRenderApiImpl @Inject constructor(
    dataStoreProvider: Provider<DataStore<Settings>>,
    bottomBarApiProvider: Provider<BottomNavigationHandleDeeplink>,
    unhandledExceptionApiProvider: Provider<UnhandledExceptionApi>
) : UnhandledExceptionRenderApi {

    val dataStore by dataStoreProvider
    val bottomBarApi by bottomBarApiProvider
    val unhandledExceptionApi by unhandledExceptionApiProvider

    @Composable
    override fun ComposableUnhandledExceptionRender(modifier: Modifier) {
        var dialogShow by remember {
            mutableStateOf(
                runBlocking {
                    unhandledExceptionApi.isBleConnectionForbiddenFlow().first()
                }
            )
        }

        val context = LocalContext.current
        val dialogModel = remember(context) {
            FlipperMultiChoiceDialogModel.Builder()
                .setTitle(R.string.unhandledexception_dialog_title)
                .setDescription(R.string.unhandledexception_dialog_desc)
                .addButton(R.string.unhandledexception_dialog_btn_continue, {
                    runBlocking {
                        dataStore.updateData {
                            it.toBuilder()
                                .setFatalBleSecurityExceptionHappens(false)
                                .build()
                        }
                    }
                    bottomBarApi.onChangeTab(BottomBarTab.DEVICE, force = true)
                    dialogShow = false
                })
                .addButton(R.string.unhandledexception_dialog_btn_go_to_settings, {
                    val intent = Intent(
                        android.provider.Settings.ACTION_BLUETOOTH_SETTINGS
                    )
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                })
                .addButton(R.string.unhandledexception_dialog_btn_cancel, {
                    dialogShow = false
                })
                .build()
        }

        if (dialogShow) {
            FlipperMultiChoiceDialog(model = dialogModel, modifier = modifier)
        }
    }
}

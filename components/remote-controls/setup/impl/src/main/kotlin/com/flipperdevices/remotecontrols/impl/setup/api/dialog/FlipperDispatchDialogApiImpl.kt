package com.flipperdevices.remotecontrols.impl.setup.api.dialog

import androidx.compose.runtime.Composable
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.deeplink.model.Deeplink
import com.flipperdevices.deeplink.model.DeeplinkBottomBarTab
import com.flipperdevices.remotecontrols.api.FlipperDispatchDialogApi
import com.flipperdevices.remotecontrols.impl.setup.api.dialog.composable.SetupFlipperDialogComposable
import com.flipperdevices.rootscreen.api.LocalDeeplinkHandler
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import me.gulya.anvil.assisted.ContributesAssistedFactory

@ContributesAssistedFactory(AppGraph::class, FlipperDispatchDialogApi.Factory::class)
class FlipperDispatchDialogApiImpl @AssistedInject constructor(
    @Assisted private val onBack: DecomposeOnBackParameter
) : FlipperDispatchDialogApi {
    @Composable
    override fun Render(
        dialogType: FlipperDispatchDialogApi.DialogType?,
        onDismiss: () -> Unit
    ) {
        val deeplinkHandler = LocalDeeplinkHandler.current
        SetupFlipperDialogComposable(
            flipperDialog = dialogType,
            onDismiss = onDismiss,
            openDeviceTab = {
                deeplinkHandler.handleDeeplink(
                    Deeplink.BottomBar.OpenTab(
                        DeeplinkBottomBarTab.DEVICE
                    )
                )
                onBack.invoke()
            }
        )
    }
}

package com.flipperdevices.settings.impl.composable.category

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.flipperdevices.core.preference.pb.Settings
import com.flipperdevices.settings.impl.R
import com.flipperdevices.settings.impl.composable.elements.CategoryElement
import com.flipperdevices.settings.impl.composable.elements.ClickableElement
import com.flipperdevices.settings.impl.composable.elements.GrayDivider
import com.flipperdevices.settings.impl.composable.elements.SimpleElement
import com.flipperdevices.settings.impl.composable.elements.SwitchableElement
import com.flipperdevices.settings.impl.model.DebugSettingAction
import com.flipperdevices.settings.impl.model.DebugSettingSwitch
import com.flipperdevices.settings.impl.viewmodels.DebugViewModel
import tangle.viewmodel.compose.tangleViewModel

@Composable
fun DebugCategory(
    settings: Settings,
    navController: NavController,
    onSwitchDebug: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    debugViewModel: DebugViewModel = tangleViewModel()
) {
    CardCategory(modifier = modifier) {
        CategoryElement(
            titleId = R.string.debug_options,
            descriptionId = R.string.debug_options_desc,
            state = settings.enabledDebugSettings,
            onSwitchState = onSwitchDebug
        )
        if (settings.enabledDebugSettings) {
            DebugCategoryItems(
                settings = settings,
                onAction = { debugViewModel.onAction(it, navController) },
                onSwitch = debugViewModel::onSwitch,
                onBrokeByte = debugViewModel::brokeBytes
            )
        }
    }
}

@Composable
@Suppress("LongMethod")
private fun DebugCategoryItems(
    settings: Settings,
    onAction: (DebugSettingAction) -> Unit,
    onSwitch: (DebugSettingSwitch, Boolean) -> Unit,
    onBrokeByte: () -> Unit
) {
    ClickableElement(
        titleId = R.string.debug_stress_test,
        descriptionId = R.string.debug_stress_test_desc,
        onClick = { onAction(DebugSettingAction.StressTest) }
    )
    GrayDivider()
    SimpleElement(
        titleId = R.string.debug_start_synchronization,
        onClick = { onAction(DebugSettingAction.StartSynchronization) }
    )
    GrayDivider()
    SwitchableElement(
        titleId = R.string.debug_ignored_unsupported_version,
        descriptionId = R.string.debug_ignored_unsupported_version_desc,
        state = settings.ignoreUnsupportedVersion,
        onSwitchState = { onSwitch(DebugSettingSwitch.IgnoreSupportedVersion, it) }
    )
    GrayDivider()
    SwitchableElement(
        titleId = R.string.debug_ignored_update_version,
        descriptionId = R.string.debug_ignored_update_version_desc,
        state = settings.alwaysUpdate,
        onSwitchState = { onSwitch(DebugSettingSwitch.IgnoreUpdaterVersion, it) }
    )
    GrayDivider()
    SwitchableElement(
        titleId = R.string.debug_subghz_provisioning_ignore,
        descriptionId = R.string.debug_subghz_provisioning_ignore_desc,
        state = settings.ignoreSubghzProvisioningOnZeroRegion,
        onSwitchState = { onSwitch(DebugSettingSwitch.SkipProvisioning, it) }
    )
    GrayDivider()
    ClickableElement(
        titleId = R.string.debug_restart_rpc,
        onClick = { onAction(DebugSettingAction.RestartRPC) }
    )
    GrayDivider()
    SwitchableElement(
        titleId = R.string.debug_skip_autosync,
        state = settings.skipAutoSyncInDebug,
        onSwitchState = { onSwitch(DebugSettingSwitch.SkipAutoSync, it) }
    )
    GrayDivider()
    SwitchableElement(
        titleId = R.string.debug_application_catalog_dev,
        state = settings.useDevCatalog,
        onSwitchState = { onSwitch(DebugSettingSwitch.FapHubDev, it) }
    )
    GrayDivider()
    SwitchableElement(
        titleId = R.string.debug_selfupdater,
        state = settings.selfUpdaterDebug,
        onSwitchState = { onSwitch(DebugSettingSwitch.SelfUpdaterDebug, it) }
    )
    GrayDivider()
    SwitchableElement(
        titleId = R.string.experimental_new_infrared,
        state = settings.useNewInfrared,
        onSwitchState = { onSwitch(DebugSettingSwitch.NewInfrared, it) }
    )
    GrayDivider()
    ClickableElement(
        titleId = R.string.debug_application_installall_dev,
        onClick = { onAction(DebugSettingAction.InstallAllFap) }
    )
    GrayDivider()
    ClickableElement(
        titleId = R.string.debug_broke_session,
        onClick = onBrokeByte
    )
}

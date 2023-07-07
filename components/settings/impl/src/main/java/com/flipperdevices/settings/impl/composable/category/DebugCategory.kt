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
import com.flipperdevices.settings.impl.viewmodels.DebugViewModel
import com.flipperdevices.settings.impl.viewmodels.SettingsViewModel
import tangle.viewmodel.compose.tangleViewModel

@Composable
fun DebugCategory(
    settings: Settings,
    navController: NavController,
    settingsViewModel: SettingsViewModel,
    modifier: Modifier = Modifier,
    debugViewModel: DebugViewModel = tangleViewModel()
) {
    CardCategory(modifier = modifier) {
        CategoryElement(
            titleId = R.string.debug_options,
            descriptionId = R.string.debug_options_desc,
            state = settings.enabledDebugSettings,
            onSwitchState = settingsViewModel::onSwitchDebug
        )
        if (settings.enabledDebugSettings) {
            DebugCategoryItems(navController, debugViewModel, settings)
        }
    }
}

@Composable
@Suppress("LongMethod")
private fun DebugCategoryItems(
    navController: NavController,
    debugViewModel: DebugViewModel,
    settings: Settings
) {
    ClickableElement(
        titleId = R.string.debug_stress_test,
        descriptionId = R.string.debug_stress_test_desc,
        onClick = { debugViewModel.onOpenStressTest(navController) }
    )
    GrayDivider()
    SimpleElement(
        titleId = R.string.debug_start_synchronization,
        onClick = { debugViewModel.onStartSynchronization() }
    )
    GrayDivider()
    SwitchableElement(
        titleId = R.string.debug_ignored_unsupported_version,
        descriptionId = R.string.debug_ignored_unsupported_version_desc,
        state = settings.ignoreUnsupportedVersion,
        onSwitchState = debugViewModel::onSwitchIgnoreSupportedVersion
    )
    GrayDivider()
    SwitchableElement(
        titleId = R.string.debug_ignored_update_version,
        descriptionId = R.string.debug_ignored_update_version_desc,
        state = settings.alwaysUpdate,
        onSwitchState = debugViewModel::onSwitchIgnoreUpdaterVersion
    )
    GrayDivider()
    SwitchableElement(
        titleId = R.string.debug_subghz_provisioning_ignore,
        descriptionId = R.string.debug_subghz_provisioning_ignore_desc,
        state = settings.ignoreSubghzProvisioningOnZeroRegion,
        onSwitchState = debugViewModel::onSwitchIgnoreSubGhzProvisioning
    )
    GrayDivider()
    ClickableElement(
        titleId = R.string.debug_restart_rpc,
        onClick = debugViewModel::restartRpc
    )
    GrayDivider()
    SwitchableElement(
        titleId = R.string.debug_skip_autosync,
        state = settings.skipAutoSyncInDebug,
        onSwitchState = debugViewModel::onSwitchSkipAutoSync
    )
    GrayDivider()
    SwitchableElement(
        titleId = R.string.debug_application_catalog_dev,
        state = settings.useDevCatalog,
        onSwitchState = debugViewModel::onSwitchFapHubDev
    )
    GrayDivider()
    SwitchableElement(
        titleId = R.string.debug_selfupdater,
        state = settings.selfUpdaterDebug,
        onSwitchState = debugViewModel::onSwitchSelfUpdaterDebug
    )
}

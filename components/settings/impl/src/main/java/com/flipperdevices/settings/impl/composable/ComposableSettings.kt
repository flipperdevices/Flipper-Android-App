package com.flipperdevices.settings.impl.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.ktx.OrangeAppBar
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.settings.impl.R
import com.flipperdevices.settings.impl.composable.category.AppCategory
import com.flipperdevices.settings.impl.composable.category.DebugCategory
import com.flipperdevices.settings.impl.composable.category.ExperimentalCategory
import com.flipperdevices.settings.impl.composable.category.ExportKeysCategory
import com.flipperdevices.settings.impl.composable.category.OtherSettingsCategory
import com.flipperdevices.settings.impl.composable.category.VersionCategory
import com.flipperdevices.settings.impl.model.DebugSettingAction
import com.flipperdevices.settings.impl.model.DebugSettingSwitch
import com.flipperdevices.settings.impl.model.SettingsNavigationConfig
import com.flipperdevices.settings.impl.viewmodels.DebugViewModel
import com.flipperdevices.settings.impl.viewmodels.NotificationViewModel
import com.flipperdevices.settings.impl.viewmodels.SettingsViewModel
import com.flipperdevices.settings.impl.viewmodels.VersionViewModel

@Composable
@Suppress("NonSkippableComposable")
fun ComposableSettings(
    settingsViewModel: SettingsViewModel,
    notificationViewModel: NotificationViewModel,
    debugViewModel: DebugViewModel,
    versionViewModel: VersionViewModel,
    onOpen: (SettingsNavigationConfig) -> Unit,
    onDebugAction: (DebugSettingAction) -> Unit,
    modifier: Modifier = Modifier,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    val settings by settingsViewModel.getState().collectAsState()
    val s2rInitialized by settingsViewModel.getShake2ReportInitializationState().collectAsState()
    val exportState by settingsViewModel.getExportState().collectAsState()
    val notificationState by notificationViewModel.getNotificationToggleState().collectAsState()

    SafeStatusBarBox(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(LocalPallet.current.background),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            OrangeAppBar(R.string.options, onBack = onBack)
            AppCategory(
                theme = settings.selected_theme,
                onSelectTheme = settingsViewModel::onChangeSelectedTheme,
                notificationState = notificationState,
                onChangeNotificationState = notificationViewModel::switchToggle
            )
            if (settings.expert_mode) {
                DebugCategory(
                    settings = settings,
                    onSwitchDebug = settingsViewModel::onSwitchDebug,
                    onAction = onDebugAction,
                    onDebugSettingSwitch = debugViewModel::onSwitch
                )
            }
            ExperimentalCategory(
                settings = settings,
                onSwitchExperimental = settingsViewModel::onSwitchExperimental,
                onOpenFM = { onOpen(SettingsNavigationConfig.FileManager) },
                onSwitchRemoteControls = {
                    debugViewModel.onSwitch(DebugSettingSwitch.ShowRemoteControls, it)
                }
            )
            ExportKeysCategory(
                exportState = exportState,
                onExport = { settingsViewModel.onMakeExport(context) }
            )
            OtherSettingsCategory(
                s2rInitialized = s2rInitialized,
                onReportBug = { onOpen(SettingsNavigationConfig.Shake2Report) }
            )
            @Suppress("ViewModelForwarding")
            VersionCategory(
                onActivateExpertMode = settingsViewModel::onExpertModeActivate,
                versionViewModel = versionViewModel
            )
        }
    }
}

@Composable
private fun SafeStatusBarBox(
    modifier: Modifier = Modifier,
    statusBarColor: Color = LocalPallet.current.accent,
    backgroundColor: Color = LocalPallet.current.background,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(statusBarColor)
            .statusBarsPadding()
            .background(backgroundColor)
    ) {
        // Used to fill overflow color on top of StatusBars
        if (statusBarColor != backgroundColor) {
            Box(
                modifier = Modifier
                    .background(statusBarColor)
                    .statusBarsPadding()
                    .fillMaxWidth()
                    .padding(vertical = 20.dp)
            )
        }
        content.invoke()
    }
}

package com.flipperdevices.settings.impl.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.flipperdevices.core.ui.ktx.OrangeAppBar
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.settings.impl.R
import com.flipperdevices.settings.impl.composable.category.DebugCategory
import com.flipperdevices.settings.impl.composable.category.ExperimentalCategory
import com.flipperdevices.settings.impl.composable.category.ExportKeysCategory
import com.flipperdevices.settings.impl.composable.category.OtherSettingsCategory
import com.flipperdevices.settings.impl.composable.category.ThemeCategory
import com.flipperdevices.settings.impl.composable.category.VersionCategory
import com.flipperdevices.settings.impl.viewmodels.SettingsViewModel
import tangle.viewmodel.compose.tangleViewModel

@Composable
fun ComposableCommonSettings(
    navController: NavController,
    modifier: Modifier = Modifier,
    settingsViewModel: SettingsViewModel = tangleViewModel()
) {
    val context = LocalContext.current

    val settings by settingsViewModel.getState().collectAsState()
    val s2rInitialized by settingsViewModel.getShake2ReportInitializationState().collectAsState()
    val exportState by settingsViewModel.getExportState().collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(LocalPallet.current.background),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        OrangeAppBar(R.string.options, onBack = navController::popBackStack)
        ThemeCategory(
            theme = settings.selectedTheme,
            onSelectTheme = settingsViewModel::onChangeSelectedTheme
        )
        if (settings.expertMode) {
            DebugCategory(
                settings = settings,
                navController = navController,
                onSwitchDebug = settingsViewModel::onSwitchDebug
            )
        }
        ExperimentalCategory(
            settings = settings,
            navController = navController,
            onSwitchExperimental = settingsViewModel::onSwitchExperimental
        )
        ExportKeysCategory(
            exportState = exportState,
            onExport = { settingsViewModel.onMakeExport(context) }
        )
        OtherSettingsCategory(
            s2rInitialized = s2rInitialized,
            onReportBug = { settingsViewModel.onReportBug(navController) }
        )
        VersionCategory(
            onActivateExpertMode = settingsViewModel::onExpertModeActivate,
        )
    }
}

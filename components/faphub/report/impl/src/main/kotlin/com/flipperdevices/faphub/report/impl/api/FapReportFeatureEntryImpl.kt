package com.flipperdevices.faphub.report.impl.api

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ui.navigation.ComposableFeatureEntry
import com.flipperdevices.faphub.report.api.FapReportFeatureEntry
import com.flipperdevices.faphub.report.impl.composable.ComposableReport
import com.flipperdevices.faphub.report.impl.viewmodel.ReportViewModel
import com.squareup.anvil.annotations.ContributesBinding
import com.squareup.anvil.annotations.ContributesMultibinding
import tangle.viewmodel.compose.tangleViewModel
import javax.inject.Inject

internal const val EXTRA_KEY_UID = "uid"

@ContributesBinding(AppGraph::class, FapReportFeatureEntry::class)
@ContributesMultibinding(AppGraph::class, ComposableFeatureEntry::class)
class FapReportFeatureEntryImpl @Inject constructor() : FapReportFeatureEntry {
    private val fapReportArguments = listOf(
        navArgument(EXTRA_KEY_UID) {
            nullable = false
            type = NavType.StringType
        }
    )

    override fun start(applicationUid: String) = "@${ROUTE.name}?uid=$applicationUid"

    override fun NavGraphBuilder.composable(navController: NavHostController) {
        composable(
            route = "@${ROUTE.name}?uid={$EXTRA_KEY_UID}",
            arguments = fapReportArguments
        ) {
            val reportViewModel = tangleViewModel<ReportViewModel>()
            val state by reportViewModel.getFapReportState().collectAsState()
            ComposableReport(
                onBack = navController::popBackStack,
                fapReportState = state,
                submit = { reportViewModel.submit(navController::popBackStack, it) }
            )
        }
    }
}

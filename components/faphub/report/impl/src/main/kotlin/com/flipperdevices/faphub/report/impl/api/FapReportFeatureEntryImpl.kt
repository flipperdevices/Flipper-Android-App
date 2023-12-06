package com.flipperdevices.faphub.report.impl.api

import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ui.navigation.AggregateFeatureEntry
import com.flipperdevices.faphub.report.api.FapReportFeatureEntry
import com.flipperdevices.faphub.report.impl.composable.bug.ComposableReportBugInformation
import com.flipperdevices.faphub.report.impl.composable.concern.ComposableReport
import com.flipperdevices.faphub.report.impl.composable.main.ComposableMainReport
import com.flipperdevices.faphub.report.impl.viewmodel.ReportViewModel
import com.squareup.anvil.annotations.ContributesBinding
import com.squareup.anvil.annotations.ContributesMultibinding
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.Charset
import tangle.viewmodel.compose.tangleViewModel
import javax.inject.Inject

internal const val EXTRA_KEY_UID = "uid"
internal const val EXTRA_URL_UID = "url"

@ContributesBinding(AppGraph::class, FapReportFeatureEntry::class)
@ContributesMultibinding(AppGraph::class, AggregateFeatureEntry::class)
class FapReportFeatureEntryImpl @Inject constructor() : FapReportFeatureEntry {
    private val fapReportArguments = listOf(
        navArgument(EXTRA_KEY_UID) {
            nullable = false
            type = NavType.StringType
        },
        navArgument(EXTRA_URL_UID) {
            nullable = false
            type = NavType.StringType
        }
    )

    override fun start(applicationUid: String, reportUrl: String) =
        "@${ROUTE.name}?uid=$applicationUid&url=${
            Uri.decode(reportUrl)
        }"

    private fun concernReport(applicationUid: String) = "@${ROUTE.name}concern?uid=$applicationUid"
    private fun bugReport(urlEncoded: String) = "@${ROUTE.name}report?url=$urlEncoded"

    override fun NavGraphBuilder.navigation(navController: NavHostController) {
        navigation(startDestination = "@${ROUTE.name}", route = ROUTE.name) {
            composable(
                route = "@${ROUTE.name}concern?uid={$EXTRA_KEY_UID}",
                arguments = listOf(
                    navArgument(EXTRA_KEY_UID) {
                        nullable = false
                        type = NavType.StringType
                    }
                )
            ) {
                val reportViewModel = tangleViewModel<ReportViewModel>()
                val state by reportViewModel.getFapReportState().collectAsState()
                ComposableReport(
                    onBack = navController::popBackStack,
                    fapReportState = state,
                    submit = { reportViewModel.submit(navController::popBackStack, it) }
                )
            }
            composable(
                route = "@${ROUTE.name}report?url={$EXTRA_URL_UID}",
                arguments = listOf(
                    navArgument(EXTRA_URL_UID) {
                        nullable = false
                        type = NavType.StringType
                    }
                )
            ) { backStackEntry ->
                val url = remember(backStackEntry.arguments) {
                    backStackEntry.arguments?.getString(EXTRA_URL_UID)
                        ?.let { Uri.decode(it) }
                }
                val context = LocalContext.current
                ComposableReportBugInformation(
                    onClick = {
                        if (!url.isNullOrBlank()) {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            context.startActivity(intent)
                        }
                    },
                    onBack = navController::popBackStack
                )
            }
            composable(
                route = "@${ROUTE.name}?uid={$EXTRA_KEY_UID}&url={$EXTRA_URL_UID}",
                arguments = fapReportArguments
            ) { backStackEntry ->
                val uid = remember(backStackEntry.arguments) {
                    backStackEntry.arguments?.getString(EXTRA_KEY_UID)
                }
                val urlEncoded = remember(backStackEntry.arguments) {
                    backStackEntry.arguments?.getString(EXTRA_URL_UID)
                }
                ComposableMainReport(
                    onBack = navController::popBackStack,
                    onOpenBug = {
                        if (urlEncoded != null) {
                            navController.navigate(bugReport(urlEncoded))
                        }
                    },
                    onOpenConcern = {
                        if (uid != null) {
                            navController.navigate(concernReport(uid))
                        }
                    }
                )
            }
        }
    }
}

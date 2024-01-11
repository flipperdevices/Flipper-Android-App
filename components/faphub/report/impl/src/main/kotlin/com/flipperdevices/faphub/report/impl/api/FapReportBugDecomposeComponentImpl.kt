package com.flipperdevices.faphub.report.impl.api

import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.faphub.report.impl.composable.bug.ComposableReportBugInformation
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import com.flipperdevices.ui.decompose.ScreenDecomposeComponent
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class FapReportBugDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted private val reportUrl: String,
    @Assisted private val onBack: DecomposeOnBackParameter
) : ScreenDecomposeComponent(componentContext) {
    @Composable
    @Suppress("NonSkippableComposable")
    override fun Render() {
        val context = LocalContext.current
        ComposableReportBugInformation(
            onClick = {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(reportUrl))
                context.startActivity(intent)
            },
            onBack = onBack::invoke
        )
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            reportUrl: String,
            onBack: DecomposeOnBackParameter
        ): FapReportBugDecomposeComponentImpl
    }
}

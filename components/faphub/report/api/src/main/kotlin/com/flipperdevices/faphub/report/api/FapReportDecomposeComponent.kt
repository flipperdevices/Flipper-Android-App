package com.flipperdevices.faphub.report.api

import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.ui.decompose.CompositeDecomposeComponent
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter

abstract class FapReportDecomposeComponent<C : Any> : CompositeDecomposeComponent<C>() {
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            fapReportArgument: FapReportArgument,
            onBack: DecomposeOnBackParameter
        ): FapReportDecomposeComponent<*>
    }
}

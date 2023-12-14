package com.flipperdevices.shake2report.api

import com.flipperdevices.ui.decompose.DecomposeComponent
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter

interface Shake2ReportDecomposeComponent : DecomposeComponent {
    fun interface Factory {
        operator fun invoke(
            onBack: DecomposeOnBackParameter
        ): Shake2ReportDecomposeComponent
    }
}

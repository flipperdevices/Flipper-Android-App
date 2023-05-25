package com.flipperdevices.faphub.installation.stateprovider.api.api

import com.flipperdevices.core.data.SemVer
import com.flipperdevices.faphub.installation.stateprovider.api.model.FapState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow

interface FapInstallationStateManager {
    fun getFapStateFlow(
        scope: CoroutineScope,
        applicationId: String,
        currentVersion: SemVer
    ): StateFlow<FapState>
}

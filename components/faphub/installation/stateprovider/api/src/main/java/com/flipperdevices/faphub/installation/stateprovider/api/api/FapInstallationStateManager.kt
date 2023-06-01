package com.flipperdevices.faphub.installation.stateprovider.api.api

import com.flipperdevices.core.data.SemVer
import com.flipperdevices.faphub.installation.stateprovider.api.model.FapState
import kotlinx.coroutines.flow.Flow

interface FapInstallationStateManager {
    fun getFapStateFlow(
        applicationUid: String,
        currentVersion: SemVer
    ): Flow<FapState>
}

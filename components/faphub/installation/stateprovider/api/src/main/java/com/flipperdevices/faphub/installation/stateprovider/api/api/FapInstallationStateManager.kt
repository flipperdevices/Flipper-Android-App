package com.flipperdevices.faphub.installation.stateprovider.api.api

import com.flipperdevices.faphub.dao.api.model.FapItemVersion
import com.flipperdevices.faphub.installation.stateprovider.api.model.FapState
import kotlinx.coroutines.flow.Flow

interface FapInstallationStateManager {
    fun getFapStateFlow(
        applicationUid: String,
        currentVersion: FapItemVersion
    ): Flow<FapState>
}

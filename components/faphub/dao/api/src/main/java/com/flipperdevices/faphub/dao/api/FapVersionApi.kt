package com.flipperdevices.faphub.dao.api

import com.flipperdevices.faphub.dao.api.model.FapItemVersion

interface FapVersionApi {
    suspend fun getVersions(versions: List<String>): List<FapItemVersion>
}

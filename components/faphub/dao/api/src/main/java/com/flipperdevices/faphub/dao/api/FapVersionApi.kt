package com.flipperdevices.faphub.dao.api

import com.flipperdevices.core.data.SemVer

interface FapVersionApi {
    suspend fun getVersionsMap(versions: List<String>): Map<String, SemVer>
}

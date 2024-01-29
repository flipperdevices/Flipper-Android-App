package com.flipperdevices.faphub.dao.api

interface FapReportApi {
    suspend fun report(applicationUid: String, description: String)
}

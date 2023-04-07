package com.flipperdevices.selfupdater.api

interface SelfUpdateParserApi {
    fun getName(): String
    suspend fun getLastUpdate(): SelfUpdate?
}

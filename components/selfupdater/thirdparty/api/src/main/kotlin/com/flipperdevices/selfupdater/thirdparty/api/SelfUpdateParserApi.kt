package com.flipperdevices.selfupdater.thirdparty.api

interface SelfUpdateParserApi {
    fun getName(): String
    suspend fun getLastUpdate(): SelfUpdate?
}

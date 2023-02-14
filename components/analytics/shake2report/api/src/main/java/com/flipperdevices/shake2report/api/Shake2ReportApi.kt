package com.flipperdevices.shake2report.api

import android.content.Context
import com.flipperdevices.bridge.api.model.FlipperGATTInformation
import com.flipperdevices.info.api.model.FlipperRpcInformation
import com.flipperdevices.info.api.model.FlipperStorageInformation
import com.github.terrakok.cicerone.Screen

interface Shake2ReportApi {
    /**
     * Call init for debug reporting in debug and internal build
     * And call empty method in release build
     */
    fun init()

    fun reportBugScreen(context: Context): Screen?

    fun updateGattInformation(gattInformation: FlipperGATTInformation)
    fun updateRpcInformation(rpcInformation: FlipperRpcInformation)
    fun updateStorageInformation(storageInfo: FlipperStorageInformation)

    fun reportException(
        throwable: Throwable,
        tag: String? = null,
        extras: Map<String, String>? = null
    )
}

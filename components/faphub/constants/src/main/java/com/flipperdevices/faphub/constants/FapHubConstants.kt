package com.flipperdevices.faphub.constants

import com.flipperdevices.core.data.SemVer

object FapHubConstants {
    const val FLIPPER_TMP_FOLDER = "/ext/.tmp/android"
    const val FLIPPER_APPS_FOLDER = "/ext/apps"
    val RPC_SUPPORTED_VERSION = SemVer(
        majorVersion = 0,
        minorVersion = 16
    ) // TODO migrate to 17
}
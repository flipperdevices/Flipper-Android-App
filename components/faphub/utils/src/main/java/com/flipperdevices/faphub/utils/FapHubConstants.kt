package com.flipperdevices.faphub.utils

import com.flipperdevices.core.data.SemVer

object FapHubConstants {
    const val FLIPPER_APPS_FOLDER = "/ext/apps"
    val RPC_SUPPORTED_VERSION = SemVer(
        majorVersion = 0,
        minorVersion = 16
    )
}

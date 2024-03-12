package com.flipperdevices.faphub.installedtab.impl.model

enum class InstalledNetworkErrorEnum {
    GENERAL
}

fun Throwable.toInstalledNetworkErrorEnum(): InstalledNetworkErrorEnum {
    return InstalledNetworkErrorEnum.GENERAL
}

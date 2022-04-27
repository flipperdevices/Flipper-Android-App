package com.flipperdevices.updater.ui.utils

import com.flipperdevices.updater.model.FirmwareChannel
import com.flipperdevices.updater.model.FirmwareVersion

fun FirmwareVersion.isGreaterThan(other: FirmwareVersion): Boolean? {
    if (this.channel != other.channel) {
        return null
    }

    if (channel == FirmwareChannel.DEV) {
        return version != other.version
    }

    val versionParts = version.split(".")
    val versionPartsOther = version.split(".")
    val major = versionParts.getOrNull(0).extractDigitOrZero()
    val majorOther = versionPartsOther.getOrNull(0).extractDigitOrZero()
    if (major > majorOther) {
        return true
    } else if (major < majorOther) {
        return false
    }

    val minor = versionParts.getOrNull(1).extractDigitOrZero()
    val minorOther = versionPartsOther.getOrNull(1).extractDigitOrZero()
    if (minor > minorOther) {
        return true
    } else if (minor < minorOther) {
        return false
    }

    val micro = versionParts.getOrNull(2).extractDigitOrZero()
    val microOther = versionPartsOther.getOrNull(2).extractDigitOrZero()
    if (micro > microOther) {
        return true
    }

    return false
}

private fun String?.extractDigitOrZero(): Int {
    return this?.filter { it.isDigit() }?.toInt() ?: 0
}

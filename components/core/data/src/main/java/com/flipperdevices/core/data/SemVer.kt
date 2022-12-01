package com.flipperdevices.core.data

data class SemVer(
    val majorVersion: Int,
    val minorVersion: Int,
    val patchVersion: Int? = null
) : Comparable<SemVer> {

    override fun compareTo(other: SemVer): Int {
        var comparableNumber = majorVersion.compareTo(other.majorVersion)
        if (comparableNumber != 0) {
            return comparableNumber
        }
        comparableNumber = minorVersion.compareTo(other.minorVersion)
        if (other.patchVersion == null ||
            patchVersion == null ||
            comparableNumber != 0
        ) {
            return comparableNumber
        }
        return patchVersion.compareTo(other.patchVersion)
    }

    override fun toString(): String {
        return if (patchVersion == null) {
            "$majorVersion.$minorVersion"
        } else "$majorVersion.$minorVersion.$patchVersion"
    }
}

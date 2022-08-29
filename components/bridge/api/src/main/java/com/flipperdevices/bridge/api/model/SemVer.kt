package com.flipperdevices.bridge.api.model

data class SemVer(
    val majorVersion: Int,
    val minorVersion: Int
) : Comparable<SemVer> {

    override fun compareTo(other: SemVer): Int {
        val comparableNumber = majorVersion.compareTo(other.majorVersion)
        if (comparableNumber != 0) {
            return comparableNumber
        }
        return minorVersion.compareTo(other.minorVersion)
    }

    override fun toString(): String {
        return "$majorVersion.$minorVersion"
    }
}

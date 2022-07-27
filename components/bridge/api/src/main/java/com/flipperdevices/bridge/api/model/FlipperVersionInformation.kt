package com.flipperdevices.bridge.api.model

data class FlipperVersionInformation(
    val majorVersion: Int,
    val minorVersion: Int
) : Comparable<FlipperVersionInformation> {

    override fun compareTo(other: FlipperVersionInformation): Int {
        val comparableNumber = majorVersion.compareTo(other.majorVersion)
        if (comparableNumber != 0) {
            return comparableNumber
        }
        return minorVersion.compareTo(other.minorVersion)
    }
}

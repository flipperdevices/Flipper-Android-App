package com.flipperdevices.bridge.api.model

data class FlipperVersionInformation(
    val majorVersion: Int,
    val minorVersion: Int
) : Comparable<FlipperVersionInformation> {

    companion object {
        val Zero = FlipperVersionInformation(0, 0)
    }

    override fun compareTo(other: FlipperVersionInformation): Int {
        val comparableNumber = majorVersion.compareTo(other.majorVersion)
        if (comparableNumber != 0) {
            return comparableNumber
        }
        return minorVersion.compareTo(other.minorVersion)
    }
}

package com.flipperdevices.core.data

data class SemVer(
    val majorVersion: Int,
    val minorVersion: Int,
    val patchVersion: Int? = null,
    val additionalVersion: Int? = null
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

        comparableNumber = patchVersion.compareTo(other.patchVersion)
        if (other.additionalVersion == null ||
            additionalVersion == null ||
            comparableNumber != 0
        ) {
            return comparableNumber
        }

        return additionalVersion.compareTo(other.additionalVersion)
    }

    override fun toString(): String {
        return when {
            additionalVersion != null -> "$majorVersion.$minorVersion.$patchVersion.$additionalVersion"
            patchVersion != null -> "$majorVersion.$minorVersion.$patchVersion"
            else -> "$majorVersion.$minorVersion"
        }
    }

    companion object {
        @SuppressWarnings("MagicNumber")
        fun fromString(version: String): SemVer? {
            val versionParts = version.split(".")
            if (versionParts.size < 2 || versionParts.size > 4) {
                return null
            }
            return runCatching {
                SemVer(
                    majorVersion = versionParts[0].toInt(),
                    minorVersion = versionParts[1].toInt(),
                    patchVersion = versionParts.getOrNull(2)?.toInt(),
                    additionalVersion = versionParts.getOrNull(3)?.toInt()
                )
            }.getOrNull()
        }
    }
}

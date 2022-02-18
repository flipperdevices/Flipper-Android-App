package com.flipperdevices.bridge.dao.api.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FlipperFileFormat(
    val orderedDict: List<Pair<String, String>>
) : Parcelable {
    companion object {
        fun fromFileContent(fileContent: String): FlipperFileFormat {
            val pairs = fileContent.split("\n")
                .filterNot { it.startsWith("#") }
                .map {
                    it.substringBefore(":").trim() to
                        it.substringAfter(":").trim()
                }

            return FlipperFileFormat(pairs)
        }
    }
}

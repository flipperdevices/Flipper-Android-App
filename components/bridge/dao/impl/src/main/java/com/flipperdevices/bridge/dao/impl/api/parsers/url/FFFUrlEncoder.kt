package com.flipperdevices.bridge.dao.impl.api.parsers.url

import com.flipperdevices.bridge.dao.api.model.FlipperFileFormat
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.core.log.LogTagProvider
import java.net.URL
import java.net.URLEncoder

class FFFUrlEncoder : LogTagProvider {
    override val TAG = "FFFUrlEncoder"

    fun keyToUri(path: FlipperKeyPath, fff: FlipperFileFormat): URL {
        val query = listOf(QUERY_KEY_PATH to path.pathToKey).plus(fff.orderedDict)
            .filterNot { it.first.isBlank() || it.second.isBlank() }
            .joinToString(QUERY_DELIMITED_CHAR) {
                val field = URLEncoder.encode(it.first.trim(), "UTF-8")
                val value = URLEncoder.encode(it.second.trim(), "UTF-8")
                    .replace("%2F", "/") // We want safe / for readability
                "$field$QUERY_VALUE_DELIMITED_CHAR$value"
            }
        return URL(
            PREFFERED_SCHEME,
            PREFFERED_HOST,
            "$PATH_FOR_FFF_LIHK#$query"
        )
    }
}

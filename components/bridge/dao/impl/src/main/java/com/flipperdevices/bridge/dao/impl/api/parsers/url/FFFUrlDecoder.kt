package com.flipperdevices.bridge.dao.impl.api.parsers.url

import android.net.Uri
import com.flipperdevices.bridge.dao.api.model.FlipperFileFormat
import java.net.URLDecoder

object FFFUrlDecoder {
    /**
     * @return path and fff content
     */
    fun uriToContent(uri: Uri): Pair<String, FlipperFileFormat>? {
        if (!SUPPORTED_SCHEMES.contains(uri.scheme)) {
            return null
        }
        if (!SUPPORTED_HOSTS.contains(uri.host)) {
            return null
        }
        if (uri.path != SUPPORTED_PATH) {
            return null
        }

        val fffContent = uri.fragment

        if (fffContent.isNullOrEmpty()) {
            throw IllegalArgumentException("Sharing file content can't be empty")
        }

        val (path, pairs) = parseFragment(fffContent)

        return path to FlipperFileFormat(pairs)
    }

    /**
     * @return path to content
     */
    private fun parseFragment(urlFragment: String): Pair<String, List<Pair<String, String>>> {
        val parsedContentPairs = urlFragment.split(QUERY_DELIMITED_CHAR).map {
            it.substringBefore(QUERY_VALUE_DELIMITED_CHAR) to
                it.substringAfter(QUERY_VALUE_DELIMITED_CHAR)
        }.map {
            URLDecoder.decode(it.first, QUERY_VALUE_CHARSET) to
                URLDecoder.decode(it.second, QUERY_VALUE_CHARSET)
        }

        var path: String? = null
        val fileContent = mutableListOf<Pair<String, String>>()
        for (pair in parsedContentPairs) {
            if (pair.first.equals(QUERY_KEY_PATH, ignoreCase = true) &&
                path == null // Respect only first path value
            ) {
                path = pair.second
                continue
            }
            fileContent.add(pair)
        }

        if (path == null) {
            throw IllegalArgumentException("Url fragment doesn't contains path")
        }

        return path to fileContent
    }
}

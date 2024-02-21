package com.flipperdevices.keyparser.impl.parsers.url

import android.net.Uri
import com.flipperdevices.bridge.dao.api.model.FlipperFileFormat
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.warn
import java.net.URLDecoder

class FFFUrlDecoder : LogTagProvider {
    override val TAG = "FFFUrlDecoder"

    /**
     * @return path and fff content
     */
    fun uriToContent(uri: Uri): Pair<String, FlipperFileFormat>? {
        if (!SUPPORTED_SCHEMES.contains(uri.scheme)) {
            warn {
                "Ignore $uri because ${uri.scheme} is unsupported scheme " +
                    "(Supported $SUPPORTED_SCHEMES)"
            }
            return null
        }
        if (!SUPPORTED_HOSTS.contains(uri.host)) {
            warn {
                "Ignore $uri because ${uri.host} is unsupported host " +
                    "(Supported: $SUPPORTED_HOSTS)"
            }
            return null
        }
        if (!SUPPORTED_PATHS.contains(uri.path)) {
            warn {
                "Ignore $uri because ${uri.path} is unsupported path " +
                    "(Supported: $SUPPORTED_PATHS)"
            }
            return null
        }

        val fffContent = uri.fragment

        require(!fffContent.isNullOrEmpty()) { "Sharing file content can't be empty" }

        val (path, pairs) = parseFragment(fffContent)

        return path to FlipperFileFormat(pairs)
    }

    /**
     * @return path to content
     */
    private fun parseFragment(urlFragment: String): Pair<String, List<Pair<String, String>>> {
        val parsedContentPairs = decodeQuery(urlFragment)

        var path: String? = null
        val fileContent = mutableListOf<Pair<String, String>>()
        for (pair in parsedContentPairs) {
            if (pair.first.equals(QUERY_KEY_PATH, ignoreCase = true) &&
                path == null // Respect only first path value
            ) {
                path = pair.second
            } else if (pair.first.isNotBlank() && pair.second.isNotBlank()) {
                fileContent.add(pair)
            }
        }

        requireNotNull(path) { "Url fragment doesn't contains path" }

        return path to fileContent
    }

    fun decodeQuery(query: String): List<Pair<String, String>> {
        return query.split(QUERY_DELIMITED_CHAR).map {
            it.substringBefore(QUERY_VALUE_DELIMITED_CHAR) to
                it.substringAfter(QUERY_VALUE_DELIMITED_CHAR)
        }.map {
            URLDecoder.decode(it.first, QUERY_VALUE_CHARSET) to
                URLDecoder.decode(it.second, QUERY_VALUE_CHARSET)
        }
    }
}

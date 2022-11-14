package com.flipperdevices.bridge.dao.impl.api.parsers.url

import com.flipperdevices.bridge.dao.impl.BuildConfig

internal val SUPPORTED_SCHEMES = listOf("http", "https")
internal val SUPPORTED_PATHS = listOf("/s", "/s/")
internal const val QUERY_DELIMITED_CHAR = "&"
internal const val QUERY_VALUE_DELIMITED_CHAR = "="
internal const val QUERY_VALUE_CHARSET = "UTF-8"
internal const val QUERY_KEY_PATH = "path"
internal const val QUERY_KEY = "key"

// Don't forget add host in components/deeplink/impl/src/main/AndroidManifest.xml
internal val SUPPORTED_HOSTS = listOf("dev.flpr.app", "flpr.app")

internal const val PREFFERED_SCHEME = "https"
internal val PREFFERED_HOST = if (BuildConfig.INTERNAL) "dev.flpr.app" else "flpr.app"
internal const val PATH_FOR_FFF_CRYPTO_LIHK = "/sf"
internal const val PATH_FOR_FFF_LIHK = "/s"

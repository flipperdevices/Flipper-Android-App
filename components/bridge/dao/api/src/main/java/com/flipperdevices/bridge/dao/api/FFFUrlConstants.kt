package com.flipperdevices.bridge.dao.api

val SUPPORTED_SCHEMES = listOf("http", "https")
val SUPPORTED_PATHS = listOf("/s", "/s/")
const val QUERY_DELIMITED_CHAR = "&"
const val QUERY_VALUE_DELIMITED_CHAR = "="
const val QUERY_VALUE_CHARSET = "UTF-8"
const val QUERY_KEY_PATH = "path"
const val QUERY_KEY = "key"

// Don't forget add host in components/deeplink/impl/src/main/AndroidManifest.xml
val SUPPORTED_HOSTS = listOf("dev.flpr.app", "flpr.app")

const val PREFFERED_SCHEME = "https"
val PREFFERED_HOST = if (BuildConfig.INTERNAL) "dev.flpr.app" else "flpr.app"
const val PATH_FOR_FFF_SECURE_LIHK = "/sf"
const val PATH_FOR_FFF_LIHK = "/s"

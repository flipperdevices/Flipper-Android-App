package com.flipperdevices.faphub.dao.network.network.model

enum class FapNetworkHostEnum(val hostUrl: String) {
    PROD("https://catalog.flipperzero.one"),
    DEV("https://catalog.flipp.dev");

    val baseUrl = "$hostUrl/api"
}

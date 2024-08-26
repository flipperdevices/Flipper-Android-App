package com.flipperdevices.ifrmvp.api.infrared.model

enum class InfraredHost(val url: String) {
    DEV("http://192.168.0.101:8080/"),
    PROD("https://infrared.flipperzero.one/")
}

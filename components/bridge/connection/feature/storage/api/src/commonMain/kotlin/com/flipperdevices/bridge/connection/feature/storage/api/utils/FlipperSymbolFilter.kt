package com.flipperdevices.bridge.connection.feature.storage.api.utils

// http://elm-chan.org/fsw/ff/pf/filename.html
private const val ALLOWED_CHARS =
    "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!#\$%&'()-@^_`{}~ "

object FlipperSymbolFilter {
    /**
     * @return `true` if this character can be used in the flipper file system
     */
    private fun isAcceptableSymbol(symbol: Char): Boolean {
        return ALLOWED_CHARS.contains(symbol)
    }

    fun filterUnacceptableSymbol(text: String): String {
        return text.filter { isAcceptableSymbol(it) }
            .toCharArray().let { String(it) }
    }

    fun filterUnacceptableSymbolInFileName(text: String): String {
        return text.filter { isAcceptableSymbol(it) || it == '.' }
            .toCharArray().let { String(it) }
    }

    fun isAcceptableString(text: String): Boolean {
        return text.none { !isAcceptableSymbol(it) }
    }
}

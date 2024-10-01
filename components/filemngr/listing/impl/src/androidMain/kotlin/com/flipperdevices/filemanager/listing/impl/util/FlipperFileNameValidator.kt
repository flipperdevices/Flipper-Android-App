package com.flipperdevices.filemanager.listing.impl.util

class FlipperFileNameValidator {
    private val regex = "^[0-9a-zA-Z!#\\\\\\/\$%&'()\\-@^_`{}~. ]+\$".toRegex()

    fun isValid(fileName: String): Boolean {
        return regex.matches(fileName) && fileName.isNotBlank()
    }
}

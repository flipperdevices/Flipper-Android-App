package com.flipperdevices.filemanager.listing.impl.util

/**
 * Validates name for files which can be created on flippers
 *
 * This regex contains all available symbols for files names
 */
class FlipperFileNameValidator {
    private val regex = "^[0-9a-zA-Z!#\\\\\\/\$%&'()\\-@^_`{}~. ]+\$".toRegex()

    fun isValid(fileName: String): Boolean {
        return regex.matches(fileName) && fileName.isNotBlank()
    }
}

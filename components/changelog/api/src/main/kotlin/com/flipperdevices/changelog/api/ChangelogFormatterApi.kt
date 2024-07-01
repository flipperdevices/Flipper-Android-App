package com.flipperdevices.changelog.api

interface ChangelogFormatterApi {
    fun format(changelog: String): String
}

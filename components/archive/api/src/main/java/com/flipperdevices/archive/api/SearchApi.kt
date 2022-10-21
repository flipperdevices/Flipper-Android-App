package com.flipperdevices.archive.api

import com.github.terrakok.cicerone.Screen

interface SearchApi {
    fun getSearchScreen(exitOnOpen: Boolean = false): Screen

    companion object {
        const val SEARCH_RESULT_KEY = "search_result"
    }
}

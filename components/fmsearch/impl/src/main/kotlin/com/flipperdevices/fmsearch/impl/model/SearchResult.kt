package com.flipperdevices.fmsearch.impl.model

import kotlinx.collections.immutable.ImmutableList

data class SearchResult(
    val inProgress: Boolean,
    val items: ImmutableList<SearchItem>
)

package com.flipperdevices.faphub.dao.api.model

import kotlinx.collections.immutable.ImmutableList

data class FapItemShort(
    val id: String,
    val picUrl: String,
    val shortDescription: String,
    val name: String,
    val category: FapCategory,
    val screenshots: ImmutableList<String>,
    val applicationAlias: String,
    val upToDateVersion: FapItemVersion
)

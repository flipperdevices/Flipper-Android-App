package com.flipperdevices.faphub.dao.api.model

import kotlinx.collections.immutable.ImmutableList

data class FapItem(
    val id: String,
    val picUrl: String,
    val description: String,
    val shortDescription: String,
    val changelog: String,
    val name: String,
    val category: FapCategory,
    val screenshots: ImmutableList<String>,
    val metaInformation: FapMetaInformation,
    val fapDeveloperInformation: FapDeveloperInformation,
    val applicationAlias: String,
    val upToDateVersion: FapItemVersion
)

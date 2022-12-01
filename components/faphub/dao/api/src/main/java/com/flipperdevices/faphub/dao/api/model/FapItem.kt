package com.flipperdevices.faphub.dao.api.model

data class FapItem(
    val id: String,
    val picUrl: String,
    val description: String,
    val name: String,
    val category: FapCategory,
    val screenshots: List<String>,
    val metaInformation: FapMetaInformation
)

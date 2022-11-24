package com.flipperdevices.faphub.dao.api.model

data class FapItem(
    val picUrl: String,
    val description: String,
    val name: String,
    val category: FapCategory
)
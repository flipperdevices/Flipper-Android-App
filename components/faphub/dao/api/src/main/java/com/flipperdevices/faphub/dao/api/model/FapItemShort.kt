package com.flipperdevices.faphub.dao.api.model

data class FapItemShort(
    val id: String,
    val picUrl: String,
    val description: String,
    val name: String,
    val category: FapCategory,
)

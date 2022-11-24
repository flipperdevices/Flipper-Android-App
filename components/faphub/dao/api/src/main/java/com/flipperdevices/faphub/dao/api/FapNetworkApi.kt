package com.flipperdevices.faphub.dao.api

import com.flipperdevices.faphub.dao.api.model.FapItem

interface FapNetworkApi {
    suspend fun getFeaturedItem(): FapItem
}
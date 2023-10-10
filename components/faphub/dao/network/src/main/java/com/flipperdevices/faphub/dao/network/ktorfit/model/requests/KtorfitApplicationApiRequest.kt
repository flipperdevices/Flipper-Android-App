package com.flipperdevices.faphub.dao.network.ktorfit.model.requests

import com.flipperdevices.faphub.dao.network.ktorfit.model.types.ApplicationSortType
import com.flipperdevices.faphub.dao.network.ktorfit.model.types.SortOrderType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class KtorfitApplicationApiRequest(
    @SerialName("limit")
    val limit: Int,
    @SerialName("offset")
    val offset: Int,
    @SerialName("query")
    val query: String? = null,
    @SerialName("category_id")
    val categoryId: String? = null,
    @SerialName("sort_by")
    @ApplicationSortType
    val sortBy: String?,
    @SerialName("sort_order")
    @SortOrderType
    val sortOrder: Int?,
    @SerialName("applications")
    val applications: List<String>?,
    @SerialName("target")
    val target: String?,
    @SerialName("api")
    val api: String?,
    @SerialName("is_latest_release_version")
    val isLatestReleaseVersion: Boolean? = null
) {
    constructor(
        limit: Int = 50,
        offset: Int = 0,
        query: String? = null,
        @ApplicationSortType
        sortBy: String? = null,
        @SortOrderType
        sortOrder: Int? = null,
        applications: List<String>? = null,
        target: String,
        sdkApiVersion: String,
        categoryId: String? = null
    ) : this(
        limit = limit,
        offset = offset,
        query = query,
        categoryId = categoryId,
        sortBy = sortBy,
        sortOrder = sortOrder,
        applications = applications,
        target = target,
        api = sdkApiVersion
    )

    constructor(
        limit: Int = 50,
        offset: Int = 0,
        query: String? = null,
        @ApplicationSortType
        sortBy: String? = null,
        @SortOrderType
        sortOrder: Int? = null,
        applications: List<String>? = null,
        categoryId: String? = null,
    ) : this(
        limit = limit,
        offset = offset,
        query = query,
        categoryId = categoryId,
        sortBy = sortBy,
        sortOrder = sortOrder,
        applications = applications,
        target = null,
        api = null,
        isLatestReleaseVersion = true
    )
}

package com.flipperdevices.faphub.dao.network.ktorfit.model.types

import androidx.annotation.StringDef
import com.flipperdevices.faphub.dao.api.model.SortType

@Retention(AnnotationRetention.SOURCE)
@StringDef(
    value = [
        ApplicationSortType.UPDATE_AT,
        ApplicationSortType.CREATED_AT,
        ApplicationSortType.NAME
    ]
)
annotation class ApplicationSortType {
    companion object {
        const val UPDATE_AT = "updated_at"
        const val CREATED_AT = "created_at"
        const val NAME = "name"

        fun fromSortType(sortType: SortType) = when (sortType) {
            SortType.UPDATE_AT_DESC,
            SortType.UPDATE_AT_ASC -> ApplicationSortType.UPDATE_AT

            SortType.CREATED_AT_DESC,
            SortType.CREATED_AT_ASC -> ApplicationSortType.CREATED_AT

            SortType.NAME_DESC,
            SortType.NAME_ASC -> ApplicationSortType.NAME
        }
    }
}

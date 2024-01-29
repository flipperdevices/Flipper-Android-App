package com.flipperdevices.faphub.dao.network.ktorfit.model.types

import androidx.annotation.IntDef
import com.flipperdevices.faphub.dao.api.model.SortType

@Retention(AnnotationRetention.SOURCE)
@IntDef(
    value = [
        SortOrderType.ASC,
        SortOrderType.DESC
    ]
)
annotation class SortOrderType {
    companion object {
        const val ASC = 1
        const val DESC = -1

        fun fromSortType(sortType: SortType) = when (sortType) {
            SortType.UPDATE_AT_DESC,
            SortType.CREATED_AT_DESC,
            SortType.NAME_DESC -> SortOrderType.DESC

            SortType.UPDATE_AT_ASC,
            SortType.CREATED_AT_ASC,
            SortType.NAME_ASC -> SortOrderType.ASC
        }
    }
}

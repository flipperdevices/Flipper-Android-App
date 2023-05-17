package com.flipperdevices.faphub.dao.network.retrofit.model.types

import androidx.annotation.IntDef

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
    }
}

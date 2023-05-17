package com.flipperdevices.faphub.dao.network.retrofit.model

import androidx.annotation.IntDef

@Retention(AnnotationRetention.SOURCE)
@IntDef(
    value = [
        SortOrder.ASC,
        SortOrder.DESC
    ]
)
annotation class SortOrder {
    companion object {
        const val ASC = 1
        const val DESC = -1
    }
}

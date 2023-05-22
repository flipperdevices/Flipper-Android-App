package com.flipperdevices.faphub.dao.network.retrofit.model.types

import androidx.annotation.StringDef

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
    }
}

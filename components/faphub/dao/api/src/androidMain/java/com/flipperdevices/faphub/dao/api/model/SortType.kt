package com.flipperdevices.faphub.dao.api.model

import com.flipperdevices.core.preference.pb.SelectedCatalogSort

enum class SortType {
    UPDATE_AT_DESC,
    UPDATE_AT_ASC,
    CREATED_AT_DESC,
    CREATED_AT_ASC,
    NAME_DESC,
    NAME_ASC;

    fun toSelectedSortType(): SelectedCatalogSort = when (this) {
        UPDATE_AT_DESC -> SelectedCatalogSort.UPDATE_AT_DESC
        UPDATE_AT_ASC -> SelectedCatalogSort.UPDATE_AT_ASC
        CREATED_AT_DESC -> SelectedCatalogSort.CREATED_AT_DESC
        CREATED_AT_ASC -> SelectedCatalogSort.CREATED_AT_ASC
        NAME_DESC -> SelectedCatalogSort.NAME_DESC
        NAME_ASC -> SelectedCatalogSort.NAME_ASC
    }

    companion object {
        fun SelectedCatalogSort.toSortType(): SortType = when (this) {
            SelectedCatalogSort.UPDATE_AT_DESC -> UPDATE_AT_DESC
            SelectedCatalogSort.UPDATE_AT_ASC -> UPDATE_AT_ASC
            SelectedCatalogSort.CREATED_AT_DESC -> CREATED_AT_DESC
            SelectedCatalogSort.CREATED_AT_ASC -> CREATED_AT_ASC
            SelectedCatalogSort.NAME_DESC -> NAME_DESC
            SelectedCatalogSort.NAME_ASC -> NAME_ASC
            SelectedCatalogSort.UNRECOGNIZED -> UPDATE_AT_DESC
        }
    }
}

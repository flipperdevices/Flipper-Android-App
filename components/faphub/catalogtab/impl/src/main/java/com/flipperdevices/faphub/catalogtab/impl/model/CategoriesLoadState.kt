package com.flipperdevices.faphub.catalogtab.impl.model

import com.flipperdevices.faphub.dao.api.model.FapCategory
import kotlinx.collections.immutable.ImmutableList

sealed class CategoriesLoadState {
    object Loading : CategoriesLoadState()

    data class Loaded(val categories: ImmutableList<FapCategory>) : CategoriesLoadState()

    data class Error(val throwable: Throwable) : CategoriesLoadState()
}

package com.flipperdevices.faphub.catalogtab.impl.model

import com.flipperdevices.faphub.dao.api.model.FapCategory

sealed class CategoriesLoadState {
    object Loading : CategoriesLoadState()

    class Loaded(val categories: List<FapCategory>) : CategoriesLoadState()
}
package com.flipperdevices.archive.category.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.flipperdevices.archive.model.CategoryType

class CategoryViewModelFactory(
    private val categoryType: CategoryType
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CategoryViewModel(categoryType) as T
    }
}

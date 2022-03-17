package com.flipperdevices.archive.category.fragments

import androidx.compose.runtime.Composable
import com.flipperdevices.archive.category.composable.ComposableCategory
import com.flipperdevices.archive.model.CategoryType
import com.flipperdevices.core.ktx.android.withArgs
import com.flipperdevices.core.ui.ComposeFragment

private const val EXTRA_CATEGORY_TYPE = "category_type"

class CategoryFragment : ComposeFragment() {
    @Composable
    override fun RenderView() {
        val categoryType = arguments?.getParcelable<CategoryType>(EXTRA_CATEGORY_TYPE)
        if (categoryType != null) {
            ComposableCategory(categoryType)
        }
    }

    companion object {
        fun getInstance(categoryType: CategoryType) = CategoryFragment().withArgs {
            putParcelable(EXTRA_CATEGORY_TYPE, categoryType)
        }
    }
}

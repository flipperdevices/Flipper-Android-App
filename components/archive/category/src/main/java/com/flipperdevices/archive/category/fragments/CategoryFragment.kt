package com.flipperdevices.archive.category.fragments

import android.os.Bundle
import androidx.compose.runtime.Composable
import com.flipperdevices.archive.category.composable.ComposableCategory
import com.flipperdevices.archive.category.composable.ComposableDeleted
import com.flipperdevices.archive.category.di.CategoryComponent
import com.flipperdevices.archive.model.CategoryType
import com.flipperdevices.bridge.synchronization.api.SynchronizationUiApi
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.ktx.android.parcelable
import com.flipperdevices.core.ktx.android.withArgs
import com.flipperdevices.core.ui.fragment.ComposeFragment
import javax.inject.Inject
import com.flipperdevices.core.ui.res.R as DesignSystem

private const val EXTRA_CATEGORY_TYPE = "category_type"

class CategoryFragment : ComposeFragment() {
    @Inject
    lateinit var synchronizationUiApi: SynchronizationUiApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ComponentHolder.component<CategoryComponent>().inject(this)
    }

    @Composable
    override fun RenderView() {
        val categoryType = arguments?.parcelable<CategoryType>(EXTRA_CATEGORY_TYPE)
        if (categoryType != null) {
            when (categoryType) {
                is CategoryType.ByFileType -> ComposableCategory(categoryType, synchronizationUiApi)
                CategoryType.Deleted -> ComposableDeleted()
            }
        }
    }

    override fun getStatusBarColor(): Int = DesignSystem.color.accent

    companion object {
        fun getInstance(categoryType: CategoryType) = CategoryFragment().withArgs {
            putParcelable(EXTRA_CATEGORY_TYPE, categoryType)
        }
    }
}

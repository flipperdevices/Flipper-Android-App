package com.flipperdevices.archive.category.api

import com.flipperdevices.archive.api.CategoryApi
import com.flipperdevices.archive.category.fragments.CategoryFragment
import com.flipperdevices.archive.model.CategoryType
import com.flipperdevices.core.di.AppGraph
import com.github.terrakok.cicerone.Screen
import com.github.terrakok.cicerone.androidx.FragmentScreen
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class)
class CategoryApiImpl @Inject constructor() : CategoryApi {
    override fun getCategoryScreen(categoryType: CategoryType): Screen {
        return FragmentScreen { CategoryFragment.getInstance(categoryType) }
    }
}

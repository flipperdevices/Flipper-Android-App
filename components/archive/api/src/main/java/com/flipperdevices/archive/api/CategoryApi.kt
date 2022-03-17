package com.flipperdevices.archive.api

import com.flipperdevices.archive.model.CategoryType
import com.github.terrakok.cicerone.Screen

interface CategoryApi {
    fun getCategoryScreen(categoryType: CategoryType): Screen
}

package com.flipperdevices.archive.impl.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.flipperdevices.archive.api.CategoryApi
import com.flipperdevices.archive.impl.R
import com.flipperdevices.archive.impl.di.ArchiveComponent
import com.flipperdevices.archive.impl.model.CategoryItem
import com.flipperdevices.archive.model.CategoryType
import com.flipperdevices.bridge.dao.api.delegates.key.DeleteKeyApi
import com.flipperdevices.bridge.dao.api.delegates.key.SimpleKeyApi
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.ktx.jre.map
import com.github.terrakok.cicerone.Router
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.TreeMap
import javax.inject.Inject

class CategoryViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val deletedCategoryName = application.getString(R.string.archive_tab_deleted)

    @Inject
    lateinit var simpleKeyApi: SimpleKeyApi

    @Inject
    lateinit var deleteKeyApi: DeleteKeyApi

    @Inject
    lateinit var categoryApi: CategoryApi

    private val categoriesFlow = MutableStateFlow<Map<FlipperKeyType, CategoryItem>>(
        FlipperKeyType.values().map {
            it to CategoryItem(it.icon, it.humanReadableName, null, CategoryType.ByFileType(it))
        }.toMap(TreeMap())
    )
    private val deletedCategoryFlow = MutableStateFlow(
        CategoryItem(null, deletedCategoryName, null, CategoryType.Deleted)
    )

    init {
        ComponentHolder.component<ArchiveComponent>().inject(this)
        viewModelScope.launch {
            subscribeOnCategoriesCount()
        }
    }

    fun getDeletedFlow(): StateFlow<CategoryItem> = deletedCategoryFlow

    fun getCategoriesFlow(): StateFlow<List<CategoryItem>> = categoriesFlow.map(viewModelScope) {
        it.values.toList()
    }

    fun onCategoryClick(router: Router, categoryItem: CategoryItem) {
        router.navigateTo(categoryApi.getCategoryScreen(categoryItem.categoryType))
    }

    private suspend fun subscribeOnCategoriesCount() {
        deleteKeyApi.getDeletedKeyAsFlow().onEach {
            deletedCategoryFlow.emit(
                CategoryItem(
                    iconId = null,
                    title = deletedCategoryName,
                    count = it.size,
                    categoryType = CategoryType.Deleted
                )
            )
        }.launchIn(viewModelScope)

        FlipperKeyType.values().forEach { fileType ->
            simpleKeyApi.getExistKeysAsFlow(fileType).onEach { keys ->
                categoriesFlow.update {
                    val mutableMap = TreeMap(it)
                    mutableMap[fileType] = CategoryItem(
                        fileType.icon,
                        fileType.humanReadableName,
                        keys.size,
                        categoryType = CategoryType.ByFileType(fileType)
                    )
                    return@update mutableMap
                }
            }.launchIn(viewModelScope)
        }
    }
}

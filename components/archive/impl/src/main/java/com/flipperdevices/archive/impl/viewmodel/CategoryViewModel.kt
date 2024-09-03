package com.flipperdevices.archive.impl.viewmodel

import android.app.Application
import com.flipperdevices.archive.impl.R
import com.flipperdevices.archive.impl.model.CategoryItem
import com.flipperdevices.archive.model.CategoryType
import com.flipperdevices.bridge.dao.api.delegates.key.DeleteKeyApi
import com.flipperdevices.bridge.dao.api.delegates.key.SimpleKeyApi
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import com.flipperdevices.bridge.dao.api.model.icon
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import java.util.TreeMap
import javax.inject.Inject

class CategoryViewModel @Inject constructor(
    private val application: Application,
    private val simpleKeyApi: SimpleKeyApi,
    private val deleteKeyApi: DeleteKeyApi,
) : DecomposeViewModel() {
    private val deletedCategoryName = application.getString(R.string.archive_tab_deleted)

    private val categoriesMapFlow = MutableStateFlow<Map<FlipperKeyType, CategoryItem>>(
        FlipperKeyType.entries.map {
            it to CategoryItem(it.icon, it.humanReadableName, null, CategoryType.ByFileType(it))
        }.toMap(TreeMap())
    )
    private val categoriesFlow = MutableStateFlow<PersistentList<CategoryItem>>(persistentListOf())
    private val deletedCategoryFlow = MutableStateFlow(
        CategoryItem(null, deletedCategoryName, null, CategoryType.Deleted)
    )

    init {
        viewModelScope.launch {
            subscribeOnCategoriesCount()
        }
        categoriesMapFlow.onEach {
            categoriesFlow.emit(it.values.toPersistentList())
        }.launchIn(viewModelScope)
    }

    fun getDeletedFlow(): StateFlow<CategoryItem> = deletedCategoryFlow

    fun getCategoriesFlow() = categoriesFlow.asStateFlow()

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

        FlipperKeyType.entries.forEach { fileType ->
            simpleKeyApi.getExistKeysAsFlow(fileType).onEach { keys ->
                categoriesMapFlow.update {
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

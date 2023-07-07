package com.flipperdevices.archive.impl.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.flipperdevices.archive.impl.R
import com.flipperdevices.archive.impl.model.CategoryItem
import com.flipperdevices.archive.model.CategoryType
import com.flipperdevices.bridge.dao.api.delegates.key.DeleteKeyApi
import com.flipperdevices.bridge.dao.api.delegates.key.SimpleKeyApi
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import com.flipperdevices.core.ktx.jre.map
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import tangle.viewmodel.VMInject
import java.util.TreeMap

class CategoryViewModel @VMInject constructor(
    application: Application,
    private val simpleKeyApi: SimpleKeyApi,
    private val deleteKeyApi: DeleteKeyApi,
) : AndroidViewModel(application) {
    private val deletedCategoryName = application.getString(R.string.archive_tab_deleted)

    private val categoriesFlow = MutableStateFlow<Map<FlipperKeyType, CategoryItem>>(
        FlipperKeyType.values().map {
            it to CategoryItem(it.icon, it.humanReadableName, null, CategoryType.ByFileType(it))
        }.toMap(TreeMap())
    )
    private val deletedCategoryFlow = MutableStateFlow(
        CategoryItem(null, deletedCategoryName, null, CategoryType.Deleted)
    )

    init {
        viewModelScope.launch(Dispatchers.Default) {
            subscribeOnCategoriesCount()
        }
    }

    fun getDeletedFlow(): StateFlow<CategoryItem> = deletedCategoryFlow

    fun getCategoriesFlow(): StateFlow<List<CategoryItem>> = categoriesFlow.map(viewModelScope) {
        it.values.toList()
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
        }.launchIn(viewModelScope + Dispatchers.Default)

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
            }.launchIn(viewModelScope + Dispatchers.Default)
        }
    }
}

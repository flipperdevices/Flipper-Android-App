package com.flipperdevices.archive.impl.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.flipperdevices.archive.impl.R
import com.flipperdevices.archive.impl.di.ArchiveComponent
import com.flipperdevices.archive.impl.model.CategoryItem
import com.flipperdevices.bridge.dao.api.delegates.KeyApi
import com.flipperdevices.bridge.dao.api.model.FlipperFileType
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.ktx.jre.map
import java.util.TreeMap
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CategoryViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val deletedCategoryName = application.getString(R.string.archive_tab_deleted)

    @Inject
    lateinit var keyApi: KeyApi

    private val categoriesFlow = MutableStateFlow<Map<FlipperFileType, CategoryItem>>(
        FlipperFileType.values().map {
            it to CategoryItem(it.icon, it.humanReadableName, null)
        }.toMap(TreeMap())
    )
    private val deletedCategoryFlow = MutableStateFlow(
        CategoryItem(null, deletedCategoryName, null)
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

    private suspend fun subscribeOnCategoriesCount() {
        keyApi.getDeletedKeyAsFlow().onEach {
            deletedCategoryFlow.emit(
                CategoryItem(
                    iconId = null,
                    title = deletedCategoryName,
                    count = it.size
                )
            )
        }.launchIn(viewModelScope)

        FlipperFileType.values().forEach { fileType ->
            keyApi.getExistKeysAsFlow(fileType).onEach { keys ->
                categoriesFlow.update {
                    val mutableMap = TreeMap(it)
                    mutableMap[fileType] = CategoryItem(
                        fileType.icon, fileType.humanReadableName, keys.size
                    )
                    return@update mutableMap
                }
            }.launchIn(viewModelScope)
        }
    }
}

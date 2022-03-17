package com.flipperdevices.archive.category.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flipperdevices.archive.category.di.CategoryComponent
import com.flipperdevices.archive.category.model.CategoryState
import com.flipperdevices.archive.model.CategoryType
import com.flipperdevices.bridge.dao.api.delegates.KeyApi
import com.flipperdevices.bridge.dao.api.delegates.KeyParser
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.keyscreen.api.KeyScreenApi
import com.github.terrakok.cicerone.Router
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class CategoryViewModel(
    categoryType: CategoryType
) : ViewModel() {
    private val categoryState = MutableStateFlow<CategoryState>(CategoryState.Loading)

    @Inject
    lateinit var parser: KeyParser

    @Inject
    lateinit var keyApi: KeyApi

    @Inject
    lateinit var screenApi: KeyScreenApi

    init {
        ComponentHolder.component<CategoryComponent>().inject(this)
        viewModelScope.launch {
            when (categoryType) {
                is CategoryType.ByFileType -> keyApi.getExistKeysAsFlow(categoryType.fileType)
                CategoryType.Deleted -> keyApi.getDeletedKeyAsFlow()
            }.map { list ->
                list.map {
                    parser.parseKey(it) to it.path
                }
            }.collect {
                categoryState.emit(CategoryState.Loaded(it))
            }
        }
    }

    fun getState(): StateFlow<CategoryState> = categoryState

    fun openKeyScreen(router: Router, flipperKeyPath: FlipperKeyPath) {
        router.navigateTo(screenApi.getKeyScreenScreen(flipperKeyPath))
    }
}

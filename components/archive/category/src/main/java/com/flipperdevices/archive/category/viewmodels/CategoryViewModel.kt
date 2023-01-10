package com.flipperdevices.archive.category.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flipperdevices.archive.category.di.CategoryComponent
import com.flipperdevices.archive.category.model.CategoryState
import com.flipperdevices.archive.model.CategoryType
import com.flipperdevices.bridge.dao.api.delegates.KeyParser
import com.flipperdevices.bridge.dao.api.delegates.key.DeleteKeyApi
import com.flipperdevices.bridge.dao.api.delegates.key.SimpleKeyApi
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.synchronization.api.SynchronizationApi
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.keyscreen.api.KeyScreenApi
import com.github.terrakok.cicerone.Router
import javax.inject.Inject
import kotlinx.collections.immutable.toImmutableList
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
    lateinit var simpleKeyApi: SimpleKeyApi

    @Inject
    lateinit var deleteKeyApi: DeleteKeyApi

    @Inject
    lateinit var screenApi: KeyScreenApi

    @Inject
    lateinit var synchronizationState: SynchronizationApi

    init {
        ComponentHolder.component<CategoryComponent>().inject(this)
        viewModelScope.launch {
            when (categoryType) {
                is CategoryType.ByFileType -> simpleKeyApi.getExistKeysAsFlow(categoryType.fileType)
                CategoryType.Deleted -> deleteKeyApi.getDeletedKeyAsFlow()
            }.map { list ->
                list.map {
                    parser.parseKey(it) to it
                }
            }.collect {
                categoryState.emit(CategoryState.Loaded(it.toImmutableList()))
            }
        }
    }

    fun getState(): StateFlow<CategoryState> = categoryState

    fun getSynchronizationState() = synchronizationState.getSynchronizationState()

    fun openKeyScreen(router: Router, flipperFilePath: FlipperKeyPath) {
        router.navigateTo(screenApi.getKeyScreenScreen(flipperFilePath))
    }
}

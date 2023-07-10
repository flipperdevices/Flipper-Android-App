package com.flipperdevices.archive.category.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flipperdevices.archive.category.api.EXTRA_CATEGORY_TYPE
import com.flipperdevices.archive.category.model.CategoryState
import com.flipperdevices.archive.model.CategoryType
import com.flipperdevices.bridge.dao.api.delegates.key.DeleteKeyApi
import com.flipperdevices.bridge.dao.api.delegates.key.SimpleKeyApi
import com.flipperdevices.bridge.synchronization.api.SynchronizationApi
import com.flipperdevices.keyparser.api.KeyParser
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import tangle.inject.TangleParam
import tangle.viewmodel.VMInject

class CategoryViewModel @VMInject constructor(
    @TangleParam(EXTRA_CATEGORY_TYPE)
    categoryType: CategoryType,
    private val parser: KeyParser,
    private val simpleKeyApi: SimpleKeyApi,
    private val deleteKeyApi: DeleteKeyApi,
    private val synchronizationState: SynchronizationApi
) : ViewModel() {
    private val categoryState = MutableStateFlow<CategoryState>(CategoryState.Loading)
    init {
        viewModelScope.launch(Dispatchers.Default) {
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
}

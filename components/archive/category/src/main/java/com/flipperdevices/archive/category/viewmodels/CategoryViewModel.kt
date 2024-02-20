package com.flipperdevices.archive.category.viewmodels

import androidx.lifecycle.viewModelScope
import com.flipperdevices.archive.category.model.CategoryState
import com.flipperdevices.archive.model.CategoryType
import com.flipperdevices.bridge.dao.api.delegates.key.DeleteKeyApi
import com.flipperdevices.bridge.dao.api.delegates.key.SimpleKeyApi
import com.flipperdevices.bridge.synchronization.api.SynchronizationApi
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.flipperdevices.keyparser.api.KeyParser
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class CategoryViewModel @AssistedInject constructor(
    @Assisted categoryType: CategoryType,
    private val parser: KeyParser,
    private val simpleKeyApi: SimpleKeyApi,
    private val deleteKeyApi: DeleteKeyApi,
    private val synchronizationState: SynchronizationApi
) : DecomposeViewModel() {
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

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            categoryType: CategoryType
        ): CategoryViewModel
    }
}

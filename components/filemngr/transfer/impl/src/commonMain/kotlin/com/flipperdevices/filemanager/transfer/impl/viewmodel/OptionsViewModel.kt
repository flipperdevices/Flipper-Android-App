package com.flipperdevices.filemanager.transfer.impl.viewmodel

import androidx.datastore.core.DataStore
import com.flipperdevices.core.preference.pb.FileManagerOrientation
import com.flipperdevices.core.preference.pb.FileManagerSort
import com.flipperdevices.core.preference.pb.Settings
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class OptionsViewModel @Inject constructor(
    private val settingsDataStore: DataStore<Settings>
) : DecomposeViewModel() {
    private val _state = MutableStateFlow(
        State(
            // need runBlocking to get rid of animation change from list to grid
            orientation = runBlocking { settingsDataStore.data.first().file_manager_orientation }
        )
    )
    val state = _state.asStateFlow()

    fun toggleMenu() {
        _state.update { it.copy(isVisible = !it.isVisible) }
    }

    private fun setHiddenFiles(isHiddenFilesVisible: Boolean) {
        toggleMenu()
        viewModelScope.launch {
            settingsDataStore.updateData { it.copy(show_hidden_files_on_flipper = isHiddenFilesVisible) }
        }
    }

    fun toggleHiddenFiles() {
        setHiddenFiles(!state.value.isHiddenFilesVisible)
    }

    private fun setOrientation(orientation: FileManagerOrientation) {
        toggleMenu()
        viewModelScope.launch {
            settingsDataStore.updateData { it.copy(file_manager_orientation = orientation) }
        }
    }

    fun setListOrientation() {
        setOrientation(FileManagerOrientation.LIST)
    }

    fun setGridOrientation() {
        setOrientation(FileManagerOrientation.GRID)
    }

    private fun setSort(sort: FileManagerSort) {
        toggleMenu()
        viewModelScope.launch {
            settingsDataStore.updateData { it.copy(file_manager_sort = sort) }
        }
    }

    fun setDefaultSort() {
        setSort(FileManagerSort.DEFAULT)
    }

    fun setSizeSort() {
        setSort(FileManagerSort.SIZE)
    }

    fun onAction(action: Action) {
        when (action) {
            Action.DisplayGrid -> setGridOrientation()
            Action.DisplayList -> setListOrientation()
            Action.SortByDefault -> setDefaultSort()
            Action.SortBySize -> setSizeSort()
            Action.ToggleHidden -> toggleHiddenFiles()
            Action.ToggleMenu -> toggleMenu()
        }
    }

    init {
        settingsDataStore.data
            .onEach { settings ->
                _state.update { state ->
                    state.copy(
                        orientation = settings.file_manager_orientation,
                        sortType = settings.file_manager_sort,
                        isHiddenFilesVisible = settings.show_hidden_files_on_flipper
                    )
                }
            }.launchIn(viewModelScope)
    }

    data class State(
        val isVisible: Boolean = false,
        val isHiddenFilesVisible: Boolean = false,
        val orientation: FileManagerOrientation = FileManagerOrientation.LIST,
        val sortType: FileManagerSort = FileManagerSort.DEFAULT
    )

    sealed interface Action {
        data object ToggleMenu : Action
        data object DisplayGrid : Action
        data object DisplayList : Action
        data object SortBySize : Action
        data object SortByDefault : Action
        data object ToggleHidden : Action
    }
}

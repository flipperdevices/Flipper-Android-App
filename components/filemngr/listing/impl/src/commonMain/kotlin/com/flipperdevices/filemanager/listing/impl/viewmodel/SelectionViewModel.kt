package com.flipperdevices.filemanager.listing.impl.viewmodel

import com.flipperdevices.bridge.connection.feature.storage.api.model.FileType
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.flipperdevices.filemanager.listing.impl.model.PathWithType
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toImmutableSet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okio.Path
import javax.inject.Inject

class SelectionViewModel @Inject constructor() : DecomposeViewModel() {
    private val _state = MutableStateFlow(State())
    val state = _state.asStateFlow()

    fun togglePath(path: PathWithType) {
        if (state.value.selected.contains(path)) {
            deselect(path)
        } else {
            select(path)
        }
    }

    fun deselect(path: PathWithType) {
        _state.update {
            it.copy(selected = it.selected.minus(path).toImmutableSet())
        }
    }

    fun deselect(path: Path) {
        viewModelScope.launch {
            state.first()
                .selected
                .firstOrNull { it.fullPath.name == path.name }
                ?.run(::deselect)
        }
    }

    fun select(path: PathWithType) {
        select(listOf(path))
    }

    fun select(paths: List<PathWithType>) {
        _state.update {
            it.copy(
                selected = it.selected.plus(paths).toImmutableSet(),
                isEnabled = true
            )
        }
    }

    fun deselectAll() {
        _state.update { it.copy(selected = persistentSetOf()) }
    }

    fun toggleMode() {
        _state.update {
            it.copy(
                selected = persistentSetOf(),
                isEnabled = !it.isEnabled
            )
        }
    }

    data class State(
        val selected: ImmutableSet<PathWithType> = persistentSetOf(),
        val isEnabled: Boolean = false
    ) {
        val canRename: Boolean = selected.size == 1
        val canExport: Boolean = selected.size == 1 && selected.all { it.fileType == FileType.FILE }
        val canMove: Boolean = selected.size >= 1
        val canDelete: Boolean = selected.size >= 1
    }
}

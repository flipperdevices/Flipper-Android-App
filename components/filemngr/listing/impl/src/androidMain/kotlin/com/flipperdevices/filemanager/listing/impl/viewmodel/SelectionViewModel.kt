package com.flipperdevices.filemanager.listing.impl.viewmodel

import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toImmutableSet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import okio.Path
import javax.inject.Inject

class SelectionViewModel @Inject constructor() : DecomposeViewModel() {
    private val _state = MutableStateFlow(State())
    val state = _state.asStateFlow()

    fun togglePath(path: Path) {
        if (state.value.selected.contains(path)) {
            deselect(path)
        } else {
            select(path)
        }
    }

    fun deselect(path: Path) {
        _state.update {
            it.copy(selected = it.selected.minus(path).toImmutableSet())
        }
    }

    fun select(path: Path) {
        select(listOf(path))
    }

    fun select(paths: List<Path>) {
        _state.update {
            it.copy(selected = it.selected.plus(paths).toImmutableSet())
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
        val selected: ImmutableSet<Path> = persistentSetOf(),
        val isEnabled: Boolean = false
    )
}

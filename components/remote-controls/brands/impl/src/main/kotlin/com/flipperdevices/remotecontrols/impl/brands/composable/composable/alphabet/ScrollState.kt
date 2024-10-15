package com.flipperdevices.remotecontrols.impl.brands.composable.composable.alphabet

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlin.math.abs

internal class ScrollState<T>(
    private val items: ImmutableList<T>,
    private val headers: ImmutableSet<Char>,
    private val toHeader: (T) -> Char,
) {
    private val _selectedHeaderIndex = mutableIntStateOf(0)
    private val _isScrollingToIndex = mutableStateOf(false)
    private val offsets = mutableStateMapOf<Int, Float>()

    val isScrollingToIndex by _isScrollingToIndex
    val selectedHeaderIndex by _selectedHeaderIndex

    suspend fun updateSelectedIndexIfNeeded(offset: Float, listState: LazyListState) {
        val index = offsets
            .mapValues { abs(it.value - offset) }
            .entries
            .minByOrNull { it.value }
            ?.key ?: return
        if (_selectedHeaderIndex.intValue == index) return
        _selectedHeaderIndex.intValue = index
        val selectedItemIndex = items
            .map { it.let(toHeader).uppercaseChar() }
            .indexOfFirst { char -> char.code == headers.elementAt(_selectedHeaderIndex.intValue).code }
            .coerceIn(0 until listState.layoutInfo.totalItemsCount)
        _isScrollingToIndex.value = true
        listState.scrollToItem(selectedItemIndex)
        _isScrollingToIndex.value = false
    }

    fun onScrolled(listState: LazyListState) {
        if (_isScrollingToIndex.value) return
        val ch = items.elementAtOrNull(listState.firstVisibleItemIndex)
            ?.let(toHeader) ?: return
        val i = headers.indexOfFirst { it.uppercaseChar() == ch }
            .coerceIn(0, listState.layoutInfo.totalItemsCount)
        if (i == -1) return
        _selectedHeaderIndex.intValue = i
    }

    fun updateOffset(i: Int, y: Float) {
        offsets[i] = y
    }
}

package com.flipperdevices.archive.impl.composable.tab

import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import com.flipperdevices.archive.impl.model.ArchiveTab
import com.flipperdevices.bridge.dao.FlipperFileFormat

@Composable
fun ComposableTabs() {
    val tab = FlipperFileFormat.values().map {
        ArchiveTab(it.humanReadableName)
    }.plus(ArchiveTab("All"))

    LazyRow() {
        items(tab.size) {
            ComposableSingleTab()
        }
    }
}

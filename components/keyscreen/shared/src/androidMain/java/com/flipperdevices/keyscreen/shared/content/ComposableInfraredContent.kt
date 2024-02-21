package com.flipperdevices.keyscreen.shared.content

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import kotlinx.collections.immutable.persistentListOf

@Composable
internal fun ColumnScope.ComposableInfraredContent() {
    ComposableKeyContent(lines = persistentListOf())
}

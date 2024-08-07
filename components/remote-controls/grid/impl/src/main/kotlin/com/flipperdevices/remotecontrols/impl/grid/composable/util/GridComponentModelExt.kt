package com.flipperdevices.remotecontrols.impl.grid.composable.util

import com.flipperdevices.remotecontrols.impl.grid.presentation.decompose.GridComponent

internal val GridComponent.Model.contentKey: Any
    get() = when (this) {
        GridComponent.Model.Error -> 0
        is GridComponent.Model.Loaded -> 1
        is GridComponent.Model.Loading -> 2
    }

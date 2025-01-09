package com.flipperdevices.bridge.connection.utils

import com.flipperdevices.bridge.connection.screens.utils.PermissionChecker
import com.flipperdevices.core.di.AppGraph
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class, PermissionChecker::class)
class PermissionCheckerNoop @Inject constructor() : PermissionChecker {
    override fun isPermissionGranted() = true
}
package com.flipper.pair.permission

import androidx.compose.runtime.Composable
import com.flipper.core.view.ComposeFragment
import com.flipper.pair.permission.compose.ComposePermission

class PermissionFragment : ComposeFragment() {
    @Composable
    override fun renderView() {
        ComposePermission()
    }
}
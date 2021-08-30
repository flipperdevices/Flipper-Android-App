package com.flipper.pair.permission

import android.widget.Toast
import androidx.compose.runtime.Composable
import com.flipper.core.view.ComposeFragment
import com.flipper.pair.permission.compose.ComposePermission

class PermissionFragment : ComposeFragment() {
    @Composable
    override fun renderView() {
        ComposePermission({ onTouch() })
    }

    private fun onTouch() {
        Toast.makeText(requireContext(), "Hi!", Toast.LENGTH_SHORT).show()
    }
}
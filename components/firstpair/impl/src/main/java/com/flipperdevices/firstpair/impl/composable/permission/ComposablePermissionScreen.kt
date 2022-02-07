package com.flipperdevices.firstpair.impl.composable.permission

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.flipperdevices.firstpair.impl.R

@Composable
fun ComposablePermissionScreen(
    @StringRes title: Int,
    @DrawableRes picResId: Int,
    @StringRes description: Int,
    onClick: () -> Unit,
    onClose: () -> Unit
) {
    TODO()
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
@Suppress("UnusedPrivateMember")
private fun ComposablePermissionScreenPreview() {
    ComposablePermissionScreen(
        title = R.string.firstpair_permission_enable_location_title,
        picResId = R.drawable.pic_ble_disabled,
        description = R.string.firstpair_permission_enable_location_desc,
        {}, {}
    )
}

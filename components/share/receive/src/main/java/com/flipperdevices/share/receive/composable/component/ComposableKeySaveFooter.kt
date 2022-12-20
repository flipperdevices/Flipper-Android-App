package com.flipperdevices.share.receive.composable.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.ktx.ComposableFlipperButton
import com.flipperdevices.share.receive.R

@Composable
fun ComposableKeySaveFooter(savingInProgress: Boolean, onSave: () -> Unit, onEdit: () -> Unit) {
    Row(
        modifier = Modifier
            .padding(horizontal = 55.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        if (savingInProgress) {
            LoadingView()
        } else {
            ActionButton(
                onSave = onSave,
                onEdit = onEdit
            )
        }
    }
}

@Composable
private fun LoadingView() {
    Box(
        modifier = Modifier
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ActionButton(onSave: () -> Unit, onEdit: () -> Unit) {
    ComposableFlipperButton(
        text = stringResource(R.string.receive_save_btn),
        onClick = onSave
    )
    ComposableFlipperButton(
        text = stringResource(R.string.receive_edit_btn),
        onClick = onEdit
    )
}

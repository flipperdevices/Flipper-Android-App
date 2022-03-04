package com.flipperdevices.share.receive.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flipperdevices.core.ui.R as DesignSystem
import com.flipperdevices.core.ui.composable.ComposableFlipperButton
import com.flipperdevices.share.receive.R

@Composable
fun ComposableKeySaveFooter(savingInProgress: Boolean, onSave: () -> Unit, onEdit: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier
                .weight(weight = 0.5f)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(bounded = false),
                    onClick = onEdit
                ),
            text = stringResource(R.string.receive_edit_btn),
            fontSize = 14.sp,
            color = colorResource(DesignSystem.color.black_40),
            fontWeight = FontWeight.W700,
            textAlign = TextAlign.Center
        )
        SaveButton(
            modifier = Modifier.weight(weight = 0.5f),
            savingInProgress = savingInProgress,
            onSave = onSave
        )
    }
}

@Composable
private fun SaveButton(modifier: Modifier, savingInProgress: Boolean, onSave: () -> Unit) {
    if (savingInProgress) {
        Box(
            modifier = modifier
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        ComposableFlipperButton(
            modifier = Modifier
                .padding(vertical = 12.dp, horizontal = 38.dp),
            text = stringResource(R.string.receive_save_btn),
            onClick = onSave
        )
    }
}

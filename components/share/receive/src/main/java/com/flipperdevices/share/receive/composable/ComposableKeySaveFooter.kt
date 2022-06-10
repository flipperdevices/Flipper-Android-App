package com.flipperdevices.share.receive.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flipperdevices.core.ui.ktx.ComposableFlipperButton
import com.flipperdevices.core.ui.res.R as DesignSystem
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
        Text(
            modifier = Modifier
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(bounded = false),
                    onClick = onEdit
                ),
            text = stringResource(R.string.receive_edit_btn),
            fontSize = 14.sp,
            color = colorResource(DesignSystem.color.black_40),
            fontWeight = FontWeight.W700
        )
        SaveButton(
            savingInProgress = savingInProgress,
            onSave = onSave
        )
    }
}

@Composable
private fun SaveButton(savingInProgress: Boolean, onSave: () -> Unit) {
    if (savingInProgress) {
        Box(
            modifier = Modifier
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    ComposableFlipperButton(
        textPadding = PaddingValues(vertical = 12.dp, horizontal = 38.dp),
        text = stringResource(R.string.receive_save_btn),
        onClick = onSave
    )
}

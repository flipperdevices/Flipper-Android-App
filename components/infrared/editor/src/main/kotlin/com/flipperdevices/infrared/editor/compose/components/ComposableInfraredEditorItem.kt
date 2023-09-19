package com.flipperdevices.infrared.editor.compose.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.ktx.clickableRipple
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.infrared.editor.R
import com.flipperdevices.core.ui.res.R as DesignSystem

@Composable
internal fun ComposableInfraredEditorItem(
    remoteName: String,
    onTap: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
    dragModifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        ComposableInfraredEditorButton(
            modifier = Modifier.weight(1f),
            dragModifier = dragModifier,
            remoteName = remoteName,
            onTap = onTap
        )
        Icon(
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .size(size = 24.dp)
                .clickableRipple(onClick = onDelete),
            painter = painterResource(DesignSystem.drawable.ic_trash_icon),
            contentDescription = remoteName,
            tint = LocalPallet.current.keyDelete
        )
    }
}

@Composable
private fun ComposableInfraredEditorButton(
    remoteName: String,
    onTap: () -> Unit,
    modifier: Modifier = Modifier,
    dragModifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(shape = RoundedCornerShape(12.dp))
            .clickable(onClick = onTap)
            .background(LocalPallet.current.accent)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = dragModifier
                .padding(vertical = 12.dp)
                .size(size = 24.dp),
            painter = painterResource(R.drawable.pic_infrared_drag),
            contentDescription = null,
            tint = LocalPallet.current.infraredEditorDrag
        )
        Text(
            text = remoteName,
            modifier = Modifier.weight(1f),
            style = LocalTypography.current.infraredEditButton,
            color = LocalPallet.current.infraredEditorKeyName,
            textAlign = TextAlign.Center
        )
    }
}

@Preview
@Composable
private fun PreviewComposableInfraredEditorItem() {
    FlipperThemeInternal {
        ComposableInfraredEditorItem(
            remoteName = "012345678901234567891",
            onDelete = {},
            onTap = {}
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewComposableInfraredEditorItemDark() {
    FlipperThemeInternal {
        ComposableInfraredEditorItem(
            remoteName = "Off",
            onDelete = {},
            onTap = {}
        )
    }
}

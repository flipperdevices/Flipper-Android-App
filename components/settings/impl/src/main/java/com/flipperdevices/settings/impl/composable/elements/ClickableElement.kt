package com.flipperdevices.settings.impl.composable.elements

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.ktx.clickableRipple
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.res.R as DesignSystem

@Composable
fun ClickableElement(
    @StringRes titleId: Int? = null,
    @StringRes descriptionId: Int? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.clickableRipple(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SimpleElement(
            Modifier.weight(weight = 1f),
            titleId = titleId,
            descriptionId = descriptionId
        )
        Icon(
            modifier = Modifier
                .size(size = 42.dp)
                .padding(16.dp),
            painter = painterResource(DesignSystem.drawable.ic_navigate),
            tint = LocalPallet.current.iconTint30,
            contentDescription = null
        )
    }
}

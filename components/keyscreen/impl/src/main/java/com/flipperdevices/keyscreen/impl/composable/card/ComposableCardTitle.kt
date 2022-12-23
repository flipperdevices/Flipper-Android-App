package com.flipperdevices.keyscreen.impl.composable.card

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.ktx.clickableRipple
import com.flipperdevices.core.ui.res.R as DesignSystem
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.keyscreen.impl.R
import com.flipperdevices.keyscreen.impl.model.DeleteState

@Composable
fun ComposableCardTitle(
    modifier: Modifier,
    keyName: String,
    deleteState: DeleteState,
    onEditName: (() -> Unit)?
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = keyName,
            style = LocalTypography.current.titleM18
        )
        if (deleteState != DeleteState.DELETED && onEditName != null) {
            Icon(
                modifier = Modifier.clickableRipple(onEditName),
                painter = painterResource(DesignSystem.drawable.ic_edit_icon),
                contentDescription = stringResource(R.string.keyscreen_edit_text)
            )
        }
    }
}

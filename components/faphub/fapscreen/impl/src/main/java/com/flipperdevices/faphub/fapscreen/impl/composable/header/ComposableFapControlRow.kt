package com.flipperdevices.faphub.fapscreen.impl.composable.header

import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flipperdevices.core.ui.ktx.placeholderConnecting
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.faphub.dao.api.model.FapItem
import com.flipperdevices.faphub.fapscreen.impl.R

@Composable
fun ComposableFapControlRow(
    fapItem: FapItem?,
    modifier: Modifier = Modifier,
    installationButton: @Composable (FapItem?, Modifier, TextUnit) -> Unit
) = Row(
    modifier = if (fapItem == null) {
        modifier.placeholderConnecting()
    } else {
        modifier.height(IntrinsicSize.Min)
    }
) {
    Icon(
        modifier = Modifier
            .padding(end = 12.dp)
            .size(46.dp),
        painter = painterResource(R.drawable.ic_share),
        contentDescription = stringResource(R.string.fapscreen_install_share_desc),
        tint = LocalPallet.current.accent
    )

    installationButton(
        fapItem,
        Modifier
            .weight(weight = 1f)
            .fillMaxHeight()
            .clip(RoundedCornerShape(6.dp)),
        32.sp
    )
}

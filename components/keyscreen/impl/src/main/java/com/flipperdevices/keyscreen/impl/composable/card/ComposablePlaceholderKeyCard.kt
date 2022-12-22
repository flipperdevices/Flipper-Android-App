package com.flipperdevices.keyscreen.impl.composable.card

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.ktx.placeholderConnecting
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.keyscreen.impl.model.DeleteState

@Suppress("LongMethod")
@Composable
fun ComposablePlaceholderKeyCard(
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(bottomEnd = 18.dp))
                    .placeholderConnecting(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier
                        .padding(all = 8.dp)
                        .size(size = 24.dp),
                    painter = painterResource(
                        id = com.flipperdevices.core.ui.res.R.drawable.ic_fileformat_nfc
                    ),
                    contentDescription = null,
                    tint = LocalPallet.current.keyIcon
                )
                Text(
                    modifier = Modifier.padding(end = 14.dp),
                    text = "                ",
                    style = LocalTypography.current.bodyM14,
                    color = LocalPallet.current.keyTitle
                )
            }
            ComposableCardTitle(
                modifier = Modifier.padding(
                    top = 16.dp,
                    bottom = 12.dp,
                    start = 12.dp,
                    end = 12.dp
                ).placeholderConnecting(),
                deleteState = DeleteState.NOT_DELETED,
                keyName = "                        ",
                onEditName = { }
            )
            Text(
                modifier = Modifier.padding(
                    bottom = 18.dp,
                    start = 12.dp,
                    end = 12.dp
                ).placeholderConnecting(),
                text = "               ",
                color = LocalPallet.current.text30,
                style = LocalTypography.current.bodyR14
            )

            Divider(
                modifier = Modifier.fillMaxWidth(),
                color = LocalPallet.current.divider12
            )

            Column(
                modifier = Modifier.padding(vertical = 18.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        modifier = Modifier.placeholderConnecting(),
                        text = "                          ",
                        color = LocalPallet.current.text30,
                        style = LocalTypography.current.bodyR16
                    )
                    Text(
                        modifier = Modifier.placeholderConnecting(),
                        text = "                          ",
                        style = LocalTypography.current.bodyR16
                    )
                }
            }
        }
    }
}

@Preview(
    showSystemUi = true,
    showBackground = true
)
@Composable
@Suppress("UnusedPrivateMember")
private fun ComposablePlaceholderKeyCardPreview() {
    ComposablePlaceholderKeyCard()
}

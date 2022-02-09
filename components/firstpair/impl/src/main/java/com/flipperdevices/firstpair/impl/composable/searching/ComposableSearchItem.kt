package com.flipperdevices.firstpair.impl.composable.searching

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flipperdevices.core.ui.R as DesignSystem
import com.flipperdevices.core.ui.composable.ComposableFlipperButton
import com.flipperdevices.firstpair.impl.R

@Composable
@Suppress("LongMethod")
fun ComposableSearchItem(
    modifier: Modifier = Modifier,
    text: String,
    isConnecting: Boolean,
    onConnectionClick: () -> Unit
) {
    Card(
        modifier = modifier.padding(horizontal = 14.dp),
        shape = RoundedCornerShape(size = 10.dp),
        elevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(height = 65.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.padding(top = 10.dp, bottom = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    modifier = Modifier
                        .size(width = 60.dp, height = 26.dp),
                    painter = painterResource(R.drawable.pic_flipper_status),
                    contentDescription = stringResource(R.string.firstpair_search_flipper_status),
                    tint = colorResource(DesignSystem.color.accent_secondary)
                )
                Text(
                    modifier = Modifier.padding(
                        start = 10.dp,
                        end = 10.dp,
                        top = 3.dp
                    ),
                    text = stringResource(R.string.firstpair_search_flipper_model_zero),
                    color = colorResource(DesignSystem.color.black_30),
                    fontWeight = FontWeight.W500,
                    fontSize = 12.sp
                )
            }

            Divider(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(width = 1.dp),
                color = colorResource(DesignSystem.color.black_8)
            )

            Text(
                modifier = Modifier
                    .weight(weight = 1f)
                    .padding(horizontal = 16.dp),
                text = text,
                textAlign = TextAlign.Start,
                fontWeight = FontWeight.W500,
                color = colorResource(DesignSystem.color.black_100),
                fontSize = 16.sp
            )

            if (isConnecting) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .padding(end = 32.dp)
                        .size(24.dp),
                    color = colorResource(DesignSystem.color.accent_secondary),
                    strokeWidth = 2.dp
                )
            } else {
                ComposableFlipperButton(
                    modifier = Modifier.padding(end = 12.dp),
                    text = stringResource(R.string.firstpair_search_connect_button),
                    textPadding = PaddingValues(horizontal = 18.dp, vertical = 10.dp),
                    fontSize = 14.sp,
                    onClick = onConnectionClick
                )
            }
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Suppress("UnusedPrivateMember")
@Composable
private fun ComposableSearchItemPreview() {
    Column {
        ComposableSearchItem(text = "Anmach0n", isConnecting = true, onConnectionClick = {})
    }
}

package com.flipperdevices.archive.impl.composable.key

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.core.ui.composable.ComposableKeyType

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun ComposableKeySmall(
    modifier: Modifier = Modifier,
    keyPath: FlipperKeyPath = FlipperKeyPath.DUMMY,
    onKeyClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .padding(horizontal = 7.dp, vertical = 6.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(),
                onClick = onKeyClick
            )
    ) {
        Column() {
            ComposableKeyType(keyPath.fileType)
            Text(
                modifier = Modifier.padding(
                    horizontal = 8.dp,
                    vertical = 12.dp
                ),
                text = keyPath.nameWithoutExtension,
                fontSize = 14.sp,
                fontWeight = FontWeight.W400,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

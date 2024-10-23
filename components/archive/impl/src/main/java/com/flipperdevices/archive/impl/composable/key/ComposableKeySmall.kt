package com.flipperdevices.archive.impl.composable.key

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.core.ui.ktx.clickableRipple
import com.flipperdevices.core.ui.ktx.elements.ComposableKeyType
import com.flipperdevices.core.ui.theme.LocalTypography

@Composable
fun ComposableKeySmall(
    keyPath: FlipperKeyPath,
    synchronizationContent: @Composable () -> Unit,
    onOpenKey: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.padding(horizontal = 7.dp, vertical = 6.dp)
    ) {
        Column(modifier = Modifier.clickableRipple(onClick = onOpenKey)) {
            Row {
                ComposableKeyType(keyPath.path.keyType)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    synchronizationContent()
                }
            }
            Text(
                modifier = Modifier.padding(
                    horizontal = 8.dp,
                    vertical = 12.dp
                ),
                text = keyPath.path.nameWithoutExtension,
                style = LocalTypography.current.bodyR14,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        }
    }
}

package com.flipperdevices.archive.impl.composable.key

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.flipperdevices.archive.impl.viewmodel.KeyItemViewModel
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.core.ui.ktx.ComposableKeyType
import com.flipperdevices.core.ui.ktx.LocalRouter

@Composable
fun ComposableKeySmall(
    modifier: Modifier = Modifier,
    keyPath: FlipperKeyPath = FlipperKeyPath.DUMMY,
    synchronizationContent: @Composable () -> Unit,
    keyItemViewModel: KeyItemViewModel = viewModel()
) {
    val router = LocalRouter.current
    Card(
        modifier = modifier
            .padding(horizontal = 7.dp, vertical = 6.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(),
                onClick = { keyItemViewModel.open(keyPath, router) }
            ),
        shape = RoundedCornerShape(size = 10.dp)
    ) {
        Column {
            Row {
                ComposableKeyType(keyPath.fileType)
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
                text = keyPath.nameWithoutExtension,
                fontSize = 14.sp,
                fontWeight = FontWeight.W400,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

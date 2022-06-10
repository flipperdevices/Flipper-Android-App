package com.flipperdevices.archive.shared.composable

import com.flipperdevices.core.ui.res.R as DesignSystem
import androidx.annotation.ColorRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flipperdevices.archive.shared.utils.ExtractKeyMetaInformation
import com.flipperdevices.bridge.dao.api.model.parsed.FlipperKeyParsed
import com.flipperdevices.core.ui.ktx.ComposableKeyType

@Composable
fun ComposableKeyCard(
    modifier: Modifier,
    synchronizationContent: (@Composable () -> Unit)?,
    flipperKeyParsed: FlipperKeyParsed,
    @ColorRes typeColorId: Int? = flipperKeyParsed.fileType?.color,
    onCardClicked: () -> Unit
) {
    Card(
        modifier = modifier
            .padding(horizontal = 14.dp)
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(),
                onClick = onCardClicked
            ),
        shape = RoundedCornerShape(size = 10.dp)
    ) {
        Column(Modifier.padding(bottom = 8.dp)) {
            ComposableKeyCardContent(
                flipperKeyParsed,
                typeColorId,
                synchronizationContent
            )
        }
    }
}

@Composable
private fun ColumnScope.ComposableKeyCardContent(
    flipperKeyParsed: FlipperKeyParsed,
    @ColorRes typeColorId: Int?,
    synchronizationContent: (@Composable () -> Unit)?
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        ComposableKeyType(flipperKeyParsed.fileType, colorId = typeColorId)
        val protocol = ExtractKeyMetaInformation.extractProtocol(flipperKeyParsed)
        if (protocol != null) {
            Text(
                modifier = Modifier.padding(horizontal = 14.dp),
                text = protocol,
                color = colorResource(DesignSystem.color.black_12),
                fontWeight = FontWeight.W400,
                fontSize = 12.sp
            )
        }
        if (synchronizationContent != null) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                synchronizationContent()
            }
        }
    }
    Text(
        modifier = Modifier.padding(
            top = 8.dp,
            start = 8.dp,
            end = 8.dp
        ),
        text = flipperKeyParsed.keyName,
        overflow = TextOverflow.Ellipsis,
        maxLines = 1,
        color = colorResource(DesignSystem.color.black_100),
        fontSize = 14.sp,
        fontWeight = FontWeight.W400
    )

    val notes = flipperKeyParsed.notes ?: return

    SelectionContainer {
        Text(
            modifier = Modifier.padding(
                top = 6.dp,
                start = 8.dp,
                end = 8.dp
            ),
            text = notes,
            fontSize = 14.sp,
            fontWeight = FontWeight.W400,
            color = colorResource(DesignSystem.color.black_30),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

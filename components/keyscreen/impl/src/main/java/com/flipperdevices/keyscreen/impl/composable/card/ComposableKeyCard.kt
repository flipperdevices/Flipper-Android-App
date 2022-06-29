package com.flipperdevices.keyscreen.impl.composable.card

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flipperdevices.bridge.dao.api.model.parsed.FlipperKeyParsed
import com.flipperdevices.core.ui.ktx.ComposableKeyType
import com.flipperdevices.core.ui.res.R as DesignSystem
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.keyscreen.impl.R
import com.flipperdevices.keyscreen.impl.model.DeleteState
import com.flipperdevices.keyscreen.impl.model.FavoriteState
import com.flipperdevices.keyscreen.shared.ComposableKeyContent

@Composable
@Suppress("LongMethod")
fun ComposableKeyCard(
    parsedKey: FlipperKeyParsed,
    deleteState: DeleteState,
    synchronizationState: (@Composable () -> Unit)? = null,
    favoriteState: FavoriteState? = null,
    onSwitchFavorites: ((Boolean) -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = 24.dp),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column {
            Row {
                if (deleteState == DeleteState.NOT_DELETED) {
                    ComposableKeyType(parsedKey.fileType)
                } else ComposableKeyType(
                    parsedKey.fileType,
                    colorKey = LocalPallet.current.keyDeleted
                )
                if (synchronizationState != null) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 6.dp, vertical = 6.dp),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        synchronizationState()
                    }
                }
            }

            ComposableCardTitle(
                modifier = Modifier.padding(
                    top = 16.dp,
                    bottom = 12.dp,
                    start = 12.dp,
                    end = 12.dp
                ),
                deleteState = deleteState,
                keyName = parsedKey.keyName,
                favoriteState = favoriteState,
                onSwitchFavorites = onSwitchFavorites
            )
            val notes = parsedKey.notes
            SelectionContainer {
                Text(
                    modifier = Modifier.padding(
                        bottom = 18.dp,
                        start = 12.dp,
                        end = 12.dp
                    ),
                    text = notes ?: stringResource(R.string.keyscreen_card_note_empty),
                    fontWeight = FontWeight.W400,
                    fontSize = 14.sp,
                    color = colorResource(DesignSystem.color.black_30)
                )
            }

            Divider(
                modifier = Modifier.fillMaxWidth(),
                color = colorResource(DesignSystem.color.black_12)
            )

            Column(
                modifier = Modifier.padding(vertical = 18.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                ComposableKeyContent(parsedKey)
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
private fun ComposableKeyCardPreview() {
    val parsedKey = FlipperKeyParsed.RFID(
        keyName = "Test_key",
        data = "DC 69 66 0F 12",
        keyType = "EM4100",
        notes = "Test"
    )
    ComposableKeyCard(
        parsedKey,
        DeleteState.NOT_DELETED,
        synchronizationState = {},
        FavoriteState.FAVORITE
    ) {}
}

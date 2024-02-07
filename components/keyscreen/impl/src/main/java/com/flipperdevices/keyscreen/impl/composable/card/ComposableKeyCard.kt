package com.flipperdevices.keyscreen.impl.composable.card

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.ktx.elements.ComposableKeyType
import com.flipperdevices.core.ui.ktx.placeholderByLocalProvider
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.keyparser.api.model.FlipperKeyParsed
import com.flipperdevices.keyscreen.impl.R
import com.flipperdevices.keyscreen.impl.composable.actions.ComposableFavorite
import com.flipperdevices.keyscreen.model.DeleteState
import com.flipperdevices.keyscreen.model.FavoriteState
import com.flipperdevices.keyscreen.shared.ComposableKeyContent

@Composable
@Suppress("LongMethod")
fun ComposableKeyCard(
    parsedKey: FlipperKeyParsed,
    deleteState: DeleteState,
    emulatingInProgress: Boolean,
    modifier: Modifier = Modifier,
    synchronizationState: (@Composable () -> Unit)? = null,
    favoriteState: FavoriteState? = null,
    onSwitchFavorites: ((Boolean) -> Unit)? = null,
    onEditName: (() -> Unit)? = null
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Column {
            Row {
                if (deleteState == DeleteState.NOT_DELETED) {
                    ComposableKeyType(parsedKey.fileType)
                } else {
                    ComposableKeyType(
                        parsedKey.fileType,
                        colorKey = LocalPallet.current.keyDeleted
                    )
                }
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 6.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    if (deleteState == DeleteState.NOT_DELETED &&
                        favoriteState != null &&
                        onSwitchFavorites != null
                    ) {
                        ComposableFavorite(
                            favoriteState,
                            Modifier.padding(horizontal = 12.dp),
                            onSwitchFavorites
                        )
                    }
                    if (synchronizationState != null) {
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
                ).placeholderByLocalProvider(defaultWidth = 128.dp),
                deleteState = deleteState,
                keyName = parsedKey.keyName,
                onEditName = onEditName,
                emulatingInProgress = emulatingInProgress
            )
            val notes = parsedKey.notes
            SelectionContainer {
                Text(
                    modifier = Modifier.padding(
                        bottom = 18.dp,
                        start = 12.dp,
                        end = 12.dp
                    ).placeholderByLocalProvider(defaultWidth = 96.dp, defaultHeight = 12.dp),
                    text = notes ?: stringResource(R.string.keyscreen_card_note_empty),
                    color = LocalPallet.current.text30,
                    style = LocalTypography.current.bodyR14
                )
            }
            ComposableKeyContent(parsedKey)
        }
    }
}

@Preview(
    showSystemUi = true,
    showBackground = true
)
@Composable
private fun ComposableKeyCardPreview() {
    val parsedKey = FlipperKeyParsed.RFID(
        keyName = "Test_key",
        data = "DC 69 66 0F 12",
        keyType = "EM4100",
        notes = "Test"
    )
    ComposableKeyCard(
        parsedKey = parsedKey,
        deleteState = DeleteState.NOT_DELETED,
        synchronizationState = {},
        favoriteState = FavoriteState.FAVORITE,
        emulatingInProgress = false
    )
}

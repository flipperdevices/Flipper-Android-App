package com.flipperdevices.info.shared

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.theme.LocalTypography

/**
 * @param isSelectionArea disable/enable selection on card
 */
@Composable
fun InfoElementCard(
    modifier: Modifier,
    @StringRes titleId: Int? = null,
    isSelectionArea: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp),
        shape = RoundedCornerShape(size = 10.dp)
    ) {
        if (isSelectionArea) {
            SelectionContainer {
                InfoElementCardInternal(titleId, content)
            }
        } else {
            InfoElementCardInternal(titleId, content)
        }
    }
}

@Composable
private fun InfoElementCardInternal(
    @StringRes titleId: Int? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(verticalArrangement = Arrangement.SpaceBetween) {
        if (titleId != null) {
            Text(
                modifier = Modifier.padding(
                    start = 12.dp,
                    end = 12.dp,
                    top = 12.dp,
                    bottom = 6.dp
                ),
                text = stringResource(titleId),
                style = LocalTypography.current.buttonB16
            )
        }
        content()
    }
}

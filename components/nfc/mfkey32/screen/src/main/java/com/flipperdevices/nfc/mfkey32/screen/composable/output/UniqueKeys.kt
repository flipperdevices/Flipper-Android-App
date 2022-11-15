package com.flipperdevices.nfc.mfkey32.screen.composable.output

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.nfc.mfkey32.screen.R

@Suppress("FunctionNaming")
fun LazyListScope.UniqueKeys(keys: Set<String>) {
    item {
        Text(
            modifier = Modifier.padding(top = 24.dp, start = 14.dp, end = 14.dp, bottom = 10.dp),
            text = stringResource(R.string.mfkey32_founded_unique_title, keys.size),
            style = LocalTypography.current.monoSpaceM16,
            color = LocalPallet.current.text100
        )
    }

    items(keys.toList()) { key ->
        SelectionContainer {
            Text(
                modifier = Modifier.padding(
                    start = 14.dp, end = 14.dp, top = 4.dp, bottom = 4.dp
                ),
                text = key,
                style = LocalTypography.current.monoSpaceR12
            )
        }
    }
}

package com.flipperdevices.keyscreen.shared.content

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.ktx.placeholderByLocalProvider
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import kotlinx.collections.immutable.ImmutableList

@Composable
private fun ComposableKeyItem(name: String, value: String) {
    val textModifier = Modifier
        .placeholderByLocalProvider(defaultWidth = 64.dp, defaultHeight = 12.dp)

    Column(
        modifier = Modifier.padding(horizontal = 12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            modifier = textModifier,
            text = name,
            color = LocalPallet.current.text30,
            style = LocalTypography.current.bodyR16
        )
        Text(
            modifier = textModifier,
            text = value,
            style = LocalTypography.current.bodyR16
        )
    }
}

@Composable
internal fun ComposableKeyContent(
    lines: ImmutableList<Pair<String, String?>>
) {
    if (lines.isNotEmpty()) {
        Divider(
            modifier = Modifier.fillMaxWidth(),
            color = LocalPallet.current.divider12
        )

        Column(
            modifier = Modifier.padding(vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            lines.mapNotNull { (name, value) ->
                if (value == null) {
                    null
                } else {
                    name to value
                }
            }.forEach { (name, value) ->
                ComposableKeyItem(name, value)
            }
        }
    }
}

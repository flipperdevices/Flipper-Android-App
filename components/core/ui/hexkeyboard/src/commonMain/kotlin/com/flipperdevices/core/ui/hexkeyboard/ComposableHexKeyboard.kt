@file:Suppress("CompositionLocalAllowlist")

package com.flipperdevices.core.ui.hexkeyboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentColor
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstrainedLayoutReference
import androidx.constraintlayout.compose.ConstraintLayout
import com.flipperdevices.core.data.PredefinedEnumMap
import com.flipperdevices.core.ui.ktx.clickableRipple
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Fields for composition local
 * This hack avoids passing the button color and keyboard action to every function.
 * We get the value from the compose context
 */
private val LocalButtonColor = compositionLocalOf<Color> { error("No button background") }
private val LocalKeyAction = compositionLocalOf<(HexKey) -> Unit> { error("No key action") }

@Composable
fun ComposableHexKeyboard(
    modifier: Modifier = Modifier,
    backgroundKey: Color = MaterialTheme.colors.surface,
    contentColor: Color = MaterialTheme.colors.onSurface,
    keyboardHeight: Dp = 256.dp,
    textStyle: TextStyle = TextStyle(),
    onClick: (HexKey) -> Unit = {}
) {
    CompositionLocalProvider(
        LocalTextStyle provides textStyle,
        LocalContentColor provides contentColor,
        LocalButtonColor provides backgroundKey,
        LocalKeyAction provides onClick
    ) {
        ConstraintLayout(
            modifier = modifier
                .fillMaxWidth()
                .height(keyboardHeight)
        ) {
            val refs: PredefinedEnumMap<HexKey, ConstrainedLayoutReference> =
                PredefinedEnumMap(HexKey::class.java) { createRef() }
            ComposableKeys123(refs)
            ComposableKeysABC(refs)
            ComposableKeys456(refs)
            ComposableKeysDEF(refs)
            ComposableKeys7890(refs)
            ComposableKeysClearOk(refs)
        }
    }
}

@Composable
internal fun ComposableKey(
    key: HexKey,
    modifier: Modifier = Modifier
) {
    val onClick = LocalKeyAction.current
    Box(
        modifier = modifier
            .clickableRipple { onClick(key) }
            .padding(4.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(LocalButtonColor.current),
        contentAlignment = Alignment.Center
    ) {
        val text = key.title.toString()
        when (key) {
            HexKey.Clear -> Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = text
            )
            HexKey.Ok -> Text(text = "OK")
            else -> Text(text = text)
        }
    }
}

@Preview()
@Composable
private fun ComposableHexKeyboardPreview() {
    ComposableHexKeyboard(
        modifier = Modifier.background(Color.Cyan),
        backgroundKey = Color.LightGray,
        contentColor = Color.Black
    )
}

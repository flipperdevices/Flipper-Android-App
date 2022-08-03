package com.flipperdevices.core.ui.hexkeyboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentColor
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstrainedLayoutReference
import androidx.constraintlayout.compose.ConstraintLayout

/**
 * Fields for composition local
 * This hack avoids passing the button color and keyboard action to every function.
 * We get the value from the compose context
 */
private val LocalButtonColor = compositionLocalOf<Color> { error("No button background") }
private val LocalKeyAction = compositionLocalOf<(HexKey) -> Unit> { error("No key action") }

@Suppress("MagicNumber", "LongMethod")
@Composable
fun ComposableHexKeyboard(
    modifier: Modifier,
    backgroundKey: Color = MaterialTheme.colors.surface,
    contentColor: Color = MaterialTheme.colors.onSurface,
    textStyle: TextStyle = TextStyle(),
    onClick: (HexKey) -> Unit = {}
) {
    CompositionLocalProvider(
        LocalTextStyle provides textStyle,
        LocalContentColor provides contentColor,
        LocalButtonColor provides backgroundKey,
        LocalKeyAction provides onClick
    ) {
        ConstraintLayout(modifier = modifier) {
            val refs: ImmutableEnumMap<HexKey, ConstrainedLayoutReference> =
                ImmutableEnumMap(HexKey::class.java, HexKey.values()) { createRef() }
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
    modifier: Modifier
) {
    val onClick = LocalKeyAction.current
    Button(
        modifier = modifier.padding(4.dp),
        onClick = { onClick.invoke(key) },
        colors = ButtonDefaults.buttonColors(
            backgroundColor = LocalButtonColor.current,
            contentColor = LocalContentColor.current
        ),
        elevation = null
    ) {
        val text = key.title.toString()
        when (key) {
            HexKey.Clear -> Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = text
            )
            HexKey.Ok -> Text(text = "Ok")
            else -> Text(text = text)
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun ComposableHexKeyboardPreview() {
    ComposableHexKeyboard(
        modifier = Modifier.fillMaxWidth().height(200.dp).background(Color.Cyan),
        backgroundKey = Color.LightGray,
        contentColor = Color.Black
    )
}

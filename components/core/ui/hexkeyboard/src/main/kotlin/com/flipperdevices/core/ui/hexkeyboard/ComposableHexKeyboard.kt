package com.flipperdevices.core.ui.hexkeyboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
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

private val LocalButtonColor = compositionLocalOf<Color> { error("No button background") }
private val LocalKeyAction = compositionLocalOf<(HexKey) -> Unit> { error("No key action") }

@Composable
fun ComposableHexKeyboard(
    backgroundKeyBoard: Color = MaterialTheme.colors.background,
    backgroundKey: Color = MaterialTheme.colors.surface,
    contentColor: Color = MaterialTheme.colors.onSurface,
    textStyle: TextStyle = TextStyle(),
    modifier: Modifier = Modifier.height(200.dp),
    onClick: (HexKey) -> Unit = {}
) {
    CompositionLocalProvider(
        LocalTextStyle provides textStyle,
        LocalContentColor provides contentColor,
        LocalButtonColor provides backgroundKey,
        LocalKeyAction provides onClick
    ) {
        Row(modifier.background(backgroundKeyBoard)) {
            Column(Modifier.weight(1f)) {
                ComposableKeys(keys = Keys789)
                ComposableKeys(keys = Keys456)
                ComposableKeys(keys = Keys123)
                ComposableKeys(keys = Keys0)
            }
            Column(Modifier.weight(1f)) {
                ComposableKeys(keys = KeysABC)
                ComposableKeys(keys = KeysDEF)
                Row(Modifier.weight(2f)) {
                    ComposableKey(key = HexKey.Clear)
                    ComposableKey(key = HexKey.Ok, weight = 2f)
                }
            }
        }
    }
}

@Composable
private fun ColumnScope.ComposableKeys(keys: List<HexKey>) {
    Row(Modifier.weight(1f)) {
        keys.forEach {
            ComposableKey(key = it)
        }
    }
}

@Composable
private fun RowScope.ComposableKey(
    key: HexKey,
    weight: Float = 1f
) {
    val onClick = LocalKeyAction.current
    Button(
        modifier = Modifier.weight(weight).fillMaxSize().padding(4.dp),
        onClick = { onClick.invoke(key) },
        colors = ButtonDefaults.buttonColors(
            backgroundColor = LocalButtonColor.current,
            contentColor = LocalContentColor.current
        )
    ) {
        if (key == HexKey.Clear) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = key.title
            )
        } else Text(text = key.title)
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun ComposableHexKeyboardPreview() {
    ComposableHexKeyboard(
        backgroundKeyBoard = Color.Transparent,
        backgroundKey = Color.LightGray,
        contentColor = Color.Black
    )
}

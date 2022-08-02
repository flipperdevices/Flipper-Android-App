package com.flipperdevices.core.ui.hexkeyboard

import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.constraintlayout.compose.ConstraintLayout

/**
 * Fields for composition local
 * This hack avoids passing the button color and keyboard action to every function.
 * We get the value from the compose context
 */
private val LocalButtonColor = compositionLocalOf<Color> { error("No button background") }
private val LocalKeyAction = compositionLocalOf<(HexKey) -> Unit> { error("No key action") }

private const val PART_WIDTH = 1f / 6f
private const val PART_HEIGHT = 1f / 4f

@Composable
fun ComposableHexKeyboard(
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .height(200.dp),
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
            val keys = HexKey.values()
            val refs = keys.map { createRef() }
            // 789ABC
            ComposableKey(
                key = HexKey.Seven,
                modifier = Modifier.constrainAs(refs[0]) {
                    top.linkTo(parent.top)
                    bottom.linkTo(refs[6].top)
                    start.linkTo(parent.start)
                    end.linkTo(refs[1].start)
                }
            )
            ComposableKey(
                key = HexKey.Eight,
                modifier = Modifier.constrainAs(refs[1]) {
                    top.linkTo(parent.top)
                    bottom.linkTo(refs[7].top)
                    start.linkTo(refs[0].end)
                    end.linkTo(refs[2].start)
                }
            )
            ComposableKey(
                key = HexKey.Nine,
                modifier = Modifier.constrainAs(refs[2]) {
                    top.linkTo(parent.top)
                    bottom.linkTo(refs[8].top)
                    start.linkTo(refs[1].end)
                    end.linkTo(refs[3].start)
                }
            )
            ComposableKey(
                key = HexKey.A,
                modifier = Modifier.constrainAs(refs[3]) {
                    top.linkTo(parent.top)
                    bottom.linkTo(refs[9].top)
                    start.linkTo(refs[2].end)
                    end.linkTo(refs[4].start)
                }
            )
            ComposableKey(
                key = HexKey.B,
                modifier = Modifier.constrainAs(refs[4]) {
                    top.linkTo(parent.top)
                    bottom.linkTo(refs[10].top)
                    start.linkTo(refs[3].end)
                    end.linkTo(refs[5].start)
                }
            )
            ComposableKey(
                key = HexKey.C,
                modifier = Modifier.constrainAs(refs[5]) {
                    top.linkTo(parent.top)
                    bottom.linkTo(refs[11].top)
                    start.linkTo(refs[4].end)
                    end.linkTo(parent.end)
                }
            )
            // 456DEF
            ComposableKey(
                key = HexKey.Four,
                modifier = Modifier.constrainAs(refs[6]) {
                    top.linkTo(refs[0].bottom)
                    bottom.linkTo(refs[12].top)
                    start.linkTo(parent.start)
                    end.linkTo(refs[7].start)
                }
            )
            ComposableKey(
                key = HexKey.Five,
                modifier = Modifier.constrainAs(refs[7]) {
                    top.linkTo(refs[1].bottom)
                    bottom.linkTo(refs[13].top)
                    start.linkTo(refs[6].end)
                    end.linkTo(refs[8].start)
                }
            )
            ComposableKey(
                key = HexKey.Six,
                modifier = Modifier.constrainAs(refs[8]) {
                    top.linkTo(refs[2].bottom)
                    bottom.linkTo(refs[14].top)
                    start.linkTo(refs[7].end)
                    end.linkTo(refs[9].start)
                }
            )
            ComposableKey(
                key = HexKey.D,
                modifier = Modifier.constrainAs(refs[9]) {
                    top.linkTo(refs[3].bottom)
                    bottom.linkTo(refs[16].top)
                    start.linkTo(refs[8].end)
                    end.linkTo(refs[10].start)
                }
            )
            ComposableKey(
                key = HexKey.E,
                modifier = Modifier.constrainAs(refs[10]) {
                    top.linkTo(refs[4].bottom)
                    bottom.linkTo(refs[17].top)
                    start.linkTo(refs[9].end)
                    end.linkTo(refs[11].start)
                }
            )
            ComposableKey(
                key = HexKey.F,
                modifier = Modifier.constrainAs(refs[11]) {
                    top.linkTo(refs[5].bottom)
                    bottom.linkTo(refs[17].top)
                    start.linkTo(refs[10].end)
                    end.linkTo(parent.end)
                }
            )
            // 321
            ComposableKey(
                key = HexKey.Three,
                modifier = Modifier.constrainAs(refs[12]) {
                    top.linkTo(refs[6].bottom)
                    bottom.linkTo(refs[15].top)
                    start.linkTo(parent.start)
                    end.linkTo(refs[13].start)
                }
            )
            ComposableKey(
                key = HexKey.Two,
                modifier = Modifier.constrainAs(refs[13]) {
                    top.linkTo(refs[7].bottom)
                    bottom.linkTo(refs[15].top)
                    start.linkTo(refs[12].end)
                    end.linkTo(refs[14].start)
                }
            )
            ComposableKey(
                key = HexKey.One,
                modifier = Modifier.constrainAs(refs[14]) {
                    top.linkTo(refs[8].bottom)
                    bottom.linkTo(refs[15].top)
                    start.linkTo(refs[13].end)
                    end.linkTo(refs[16].start)
                }
            )

            // 0
            ComposableKey(
                key = HexKey.Zero,
                modifier = Modifier.constrainAs(refs[15]) {
                    top.linkTo(refs[12].bottom)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                }.fillMaxWidth(PART_WIDTH * 3)
            )

            // Clear, Ok
            ComposableKey(
                key = HexKey.Clear,
                modifier = Modifier.constrainAs(refs[16]) {
                    top.linkTo(refs[10].bottom)
                    start.linkTo(refs[14].end)
                    end.linkTo(refs[17].start)
                }.fillMaxHeight(PART_HEIGHT * 2)
            )
            ComposableKey(
                key = HexKey.Ok,
                modifier = Modifier.constrainAs(refs[17]) {
                    top.linkTo(refs[11].bottom)
                    start.linkTo(refs[16].end)
                    end.linkTo(parent.end)
                }.fillMaxWidth(PART_WIDTH * 2).fillMaxHeight(PART_HEIGHT * 2)
            )
        }
    }
}

@Composable
private fun ComposableKey(
    key: HexKey,
    modifier: Modifier
) {
    val onClick = LocalKeyAction.current
    Button(
        modifier = modifier.fillMaxWidth(PART_WIDTH).fillMaxHeight(PART_HEIGHT).padding(4.dp),
        onClick = { onClick.invoke(key) },
        colors = ButtonDefaults.buttonColors(
            backgroundColor = LocalButtonColor.current,
            contentColor = LocalContentColor.current
        ),
        elevation = null
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
        backgroundKey = Color.LightGray,
        contentColor = Color.Black
    )
}

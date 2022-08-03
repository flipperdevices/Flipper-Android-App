package com.flipperdevices.core.ui.hexkeyboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.constraintlayout.compose.ConstraintLayoutScope
import androidx.constraintlayout.compose.Dimension

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
            ComposableKeys123ABC(refs)
            ComposableKeys456DEF(refs)
            ComposableKeys7890(refs)
            ComposableKeysClearOk(refs)
        }
    }
}

@Composable
fun ConstraintLayoutScope.ComposableKeys123ABC(
    refs: ImmutableEnumMap<HexKey, ConstrainedLayoutReference>
) {
    ComposableKey(
        key = HexKey.One,
        modifier = Modifier.constrainAs(refs[HexKey.One]) {
            top.linkTo(parent.top)
            bottom.linkTo(refs[HexKey.Four].top)
            start.linkTo(parent.start)
            end.linkTo(refs[HexKey.Two].start)
        }
    )
    ComposableKey(
        key = HexKey.Two,
        modifier = Modifier.constrainAs(refs[HexKey.Two]) {
            top.linkTo(refs[HexKey.One].top)
            bottom.linkTo(refs[HexKey.One].bottom)
            start.linkTo(refs[HexKey.One].end)
            end.linkTo(refs[HexKey.Three].start)
        }
    )
    ComposableKey(
        key = HexKey.Three,
        modifier = Modifier.constrainAs(refs[HexKey.Three]) {
            top.linkTo(refs[HexKey.Two].top)
            bottom.linkTo(refs[HexKey.Two].bottom)
            start.linkTo(refs[HexKey.Two].end)
            end.linkTo(refs[HexKey.A].start)
        }
    )
    ComposableKey(
        key = HexKey.A,
        modifier = Modifier.constrainAs(refs[HexKey.A]) {
            top.linkTo(refs[HexKey.Three].top)
            bottom.linkTo(refs[HexKey.Three].bottom)
            start.linkTo(refs[HexKey.Three].end)
            end.linkTo(refs[HexKey.B].start)
        }
    )
    ComposableKey(
        key = HexKey.B,
        modifier = Modifier.constrainAs(refs[HexKey.B]) {
            top.linkTo(refs[HexKey.A].top)
            bottom.linkTo(refs[HexKey.A].bottom)
            start.linkTo(refs[HexKey.A].end)
            end.linkTo(refs[HexKey.C].start)
        }
    )
    ComposableKey(
        key = HexKey.C,
        modifier = Modifier.constrainAs(refs[HexKey.C]) {
            top.linkTo(refs[HexKey.B].top)
            bottom.linkTo(refs[HexKey.B].bottom)
            start.linkTo(refs[HexKey.B].end)
            end.linkTo(parent.end)
        }
    )
}

@Composable
private fun ConstraintLayoutScope.ComposableKeys456DEF(
    refs: ImmutableEnumMap<HexKey, ConstrainedLayoutReference>
) {
    ComposableKey(
        key = HexKey.Four,
        modifier = Modifier.constrainAs(refs[HexKey.Four]) {
            top.linkTo(refs[HexKey.One].bottom)
            bottom.linkTo(refs[HexKey.Seven].top)
            start.linkTo(parent.start)
            end.linkTo(refs[HexKey.Five].start)
        }
    )
    ComposableKey(
        key = HexKey.Five,
        modifier = Modifier.constrainAs(refs[HexKey.Five]) {
            top.linkTo(refs[HexKey.Four].top)
            bottom.linkTo(refs[HexKey.Four].bottom)
            start.linkTo(refs[HexKey.Four].end)
            end.linkTo(refs[HexKey.Six].start)
        }
    )
    ComposableKey(
        key = HexKey.Six,
        modifier = Modifier.constrainAs(refs[HexKey.Six]) {
            top.linkTo(refs[HexKey.Five].top)
            bottom.linkTo(refs[HexKey.Five].bottom)
            start.linkTo(refs[HexKey.Five].end)
            end.linkTo(refs[HexKey.D].start)
        }
    )
    ComposableKey(
        key = HexKey.D,
        modifier = Modifier.constrainAs(refs[HexKey.D]) {
            top.linkTo(refs[HexKey.Six].top)
            bottom.linkTo(refs[HexKey.Six].bottom)
            start.linkTo(refs[HexKey.Six].end)
            end.linkTo(refs[HexKey.E].start)
        }
    )
    ComposableKey(
        key = HexKey.E,
        modifier = Modifier.constrainAs(refs[HexKey.E]) {
            top.linkTo(refs[HexKey.D].top)
            bottom.linkTo(refs[HexKey.D].bottom)
            start.linkTo(refs[HexKey.D].end)
            end.linkTo(refs[HexKey.F].start)
        }
    )
    ComposableKey(
        key = HexKey.F,
        modifier = Modifier.constrainAs(refs[HexKey.F]) {
            top.linkTo(refs[HexKey.E].top)
            bottom.linkTo(refs[HexKey.E].bottom)
            start.linkTo(refs[HexKey.E].end)
            end.linkTo(parent.end)
        }
    )
}

@Composable
private fun ConstraintLayoutScope.ComposableKeys7890(
    refs: ImmutableEnumMap<HexKey, ConstrainedLayoutReference>
) {
    ComposableKey(
        key = HexKey.Seven,
        modifier = Modifier.constrainAs(refs[HexKey.Seven]) {
            top.linkTo(refs[HexKey.Four].bottom)
            bottom.linkTo(refs[HexKey.Zero].top)
            start.linkTo(parent.start)
            end.linkTo(refs[HexKey.Eight].start)
        }
    )
    ComposableKey(
        key = HexKey.Eight,
        modifier = Modifier.constrainAs(refs[HexKey.Eight]) {
            top.linkTo(refs[HexKey.Seven].top)
            bottom.linkTo(refs[HexKey.Seven].bottom)
            start.linkTo(refs[HexKey.Seven].end)
            end.linkTo(refs[HexKey.Nine].start)
        }
    )
    ComposableKey(
        key = HexKey.Nine,
        modifier = Modifier.constrainAs(refs[HexKey.Nine]) {
            top.linkTo(refs[HexKey.Eight].top)
            bottom.linkTo(refs[HexKey.Eight].bottom)
            start.linkTo(refs[HexKey.Eight].end)
            end.linkTo(refs[HexKey.Six].end)
        }
    )
    ComposableKey(
        key = HexKey.Zero,
        modifier = Modifier.constrainAs(refs[HexKey.Zero]) {
            top.linkTo(refs[HexKey.Seven].bottom)
            bottom.linkTo(parent.bottom)
            start.linkTo(refs[HexKey.Seven].start)
            end.linkTo(refs[HexKey.Nine].end)

            width = Dimension.fillToConstraints
        }
    )
}

@Composable
private fun ConstraintLayoutScope.ComposableKeysClearOk(
    refs: ImmutableEnumMap<HexKey, ConstrainedLayoutReference>
) {
    ComposableKey(
        key = HexKey.Clear,
        modifier = Modifier.constrainAs(refs[HexKey.Clear]) {
            top.linkTo(refs[HexKey.Nine].top)
            bottom.linkTo(refs[HexKey.Zero].bottom)
            start.linkTo(refs[HexKey.D].start)
            end.linkTo(refs[HexKey.D].end)

            height = Dimension.fillToConstraints
        }
    )
    ComposableKey(
        key = HexKey.Ok,
        modifier = Modifier.constrainAs(refs[HexKey.Ok]) {
            top.linkTo(refs[HexKey.E].bottom)
            bottom.linkTo(refs[HexKey.Zero].bottom)
            start.linkTo(refs[HexKey.E].start)
            end.linkTo(refs[HexKey.F].end)

            height = Dimension.fillToConstraints
            width = Dimension.fillToConstraints
        }
    )
}

@Composable
private fun ComposableKey(
    key: HexKey,
    modifier: Modifier
) {
    val onClick = LocalKeyAction.current
    Button(
        modifier = modifier,
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

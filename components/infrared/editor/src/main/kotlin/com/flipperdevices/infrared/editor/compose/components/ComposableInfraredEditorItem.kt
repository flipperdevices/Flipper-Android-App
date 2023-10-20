package com.flipperdevices.infrared.editor.compose.components

import android.content.res.Configuration
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.ktx.clickableRipple
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.infrared.editor.R
import kotlin.math.roundToInt
import com.flipperdevices.core.ui.res.R as DesignSystem

internal const val BUTTON_HEIGHT = 55

@Composable
internal fun ComposableInfraredEditorItem(
    remoteName: String,
    onChangeName: (String) -> Unit,
    onChangeIndexEditor: () -> Unit,
    onDelete: () -> Unit,
    isActive: Boolean,
    isError: Boolean,
    modifier: Modifier = Modifier,
    dragModifier: Modifier = Modifier,
) {
    val shake = remember { Animatable(0f) }
    var trigger by remember { mutableStateOf(0L) }
    LaunchedEffect(trigger) {
        if (trigger != 0L) {
            for (i in 0..10) {
                when (i % 2) {
                    0 -> shake.animateTo(5f, spring(stiffness = 100_000f))
                    else -> shake.animateTo(-5f, spring(stiffness = 100_000f))
                }
            }
            shake.animateTo(0f)
        }
    }
    LaunchedEffect(key1 = isError) {
        if (isError) { trigger = System.currentTimeMillis() }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .offset { IntOffset(shake.value.roundToInt(), y = 0) },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        ComposableInfraredEditorButton(
            modifier = Modifier.weight(1f),
            dragModifier = dragModifier,
            remoteName = remoteName,
            onChangeName = onChangeName,
            isActiveEditor = isActive,
            onChangeIndexEditor = onChangeIndexEditor
        )
        Icon(
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .size(size = 24.dp)
                .clickableRipple(onClick = onDelete),
            painter = painterResource(DesignSystem.drawable.ic_trash_icon),
            contentDescription = remoteName,
            tint = LocalPallet.current.keyDelete
        )
    }
}

@Composable
private fun ComposableInfraredEditorButton(
    remoteName: String,
    onChangeName: (String) -> Unit,
    onChangeIndexEditor: () -> Unit,
    isActiveEditor: Boolean,
    modifier: Modifier = Modifier,
    dragModifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .heightIn(min = BUTTON_HEIGHT.dp)
            .clip(shape = RoundedCornerShape(12.dp))
            .background(LocalPallet.current.accent)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            modifier = dragModifier
                .padding(vertical = 12.dp)
                .size(size = 24.dp),
            painter = painterResource(R.drawable.pic_infrared_drag),
            contentDescription = null,
            tint = LocalPallet.current.infraredEditorDrag
        )

        if (isActiveEditor) {
            ComposableInfraredEditorField(remoteName, onChangeName)
        } else {
            ComposableInfraredEditorText(remoteName, onChangeIndexEditor)
        }
    }
}

@Composable
private fun RowScope.ComposableInfraredEditorField(
    remoteName: String,
    onChangeName: (String) -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    val customTextSelectionColors = TextSelectionColors(
        handleColor = LocalPallet.current.accentSecond,
        backgroundColor = LocalPallet.current.accent.copy(alpha = ContentAlpha.high)
    )
    val textState by remember(remoteName) {
        mutableStateOf(
            TextFieldValue(
                text = remoteName,
                selection = TextRange(remoteName.length)
            )
        )
    }

    CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {
        TextField(
            value = textState,
            onValueChange = {
                onChangeName(it.text)
            },
            singleLine = true,
            modifier = Modifier
                .focusRequester(focusRequester)
                .weight(1f),
            textStyle = LocalTypography.current.infraredEditButton.copy(
                textAlign = TextAlign.Center
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                },
            ),
            colors = TextFieldDefaults.textFieldColors(
                textColor = LocalPallet.current.infraredEditorKeyName,
                cursorColor = LocalPallet.current.infraredEditorKeyName,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                backgroundColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
            )
        )
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

@Composable
private fun RowScope.ComposableInfraredEditorText(remoteName: String, onClick: () -> Unit) {
    Text(
        text = remoteName,
        modifier = Modifier
            .clickable(onClick = onClick)
            .weight(1f),
        style = LocalTypography.current.infraredEditButton,
        color = LocalPallet.current.infraredEditorKeyName,
        textAlign = TextAlign.Center,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
fun Shaker() {
    val shake = remember { Animatable(0f) }
    var trigger by remember { mutableStateOf(0L) }
    LaunchedEffect(trigger) {
        if (trigger != 0L) {
            for (i in 0..10) {
                when (i % 2) {
                    0 -> shake.animateTo(5f, spring(stiffness = 100_000f))
                    else -> shake.animateTo(-5f, spring(stiffness = 100_000f))
                }
            }
            shake.animateTo(0f)
        }
    }

    Box(
        modifier = Modifier
            .clickable { trigger = System.currentTimeMillis() }
            .offset { IntOffset(shake.value.roundToInt(), y = 0) }
            .padding(horizontal = 24.dp, vertical = 8.dp)
    ) {
        Text(text = "Shake me")
    }
}

@Preview
@Composable
private fun PreviewComposableInfraredEditorItem() {
    FlipperThemeInternal {
        ComposableInfraredEditorItem(
            remoteName = "01234567890123456789124242424",
            onDelete = {},
            onChangeName = {},
            onChangeIndexEditor = {},
            isActive = true,
            isError = false
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewComposableInfraredEditorItemDark() {
    FlipperThemeInternal {
        ComposableInfraredEditorItem(
            remoteName = "01234567890123456789124242424",
            onDelete = {},
            onChangeName = {},
            onChangeIndexEditor = {},
            isActive = true,
            isError = false
        )
    }
}

@Preview
@Composable
private fun PreviewComposableInfraredEditorItemNotActive() {
    FlipperThemeInternal {
        ComposableInfraredEditorItem(
            remoteName = "0123456789012345678912424242433333",
            onDelete = {},
            onChangeName = {},
            onChangeIndexEditor = {},
            isActive = false,
            isError = false
        )
    }
}

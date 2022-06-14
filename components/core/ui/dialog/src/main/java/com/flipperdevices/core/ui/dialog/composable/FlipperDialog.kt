package com.flipperdevices.core.ui.dialog.composable

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.flipperdevices.core.ui.ktx.ComposableFlipperButton
import com.flipperdevices.core.ui.ktx.letCompose
import com.flipperdevices.core.ui.res.R as DesignSystem

@Composable
fun FlipperDialog(
    modifier: Modifier = Modifier,
    image: (@Composable () -> Unit)? = null,
    title: (@Composable () -> Unit)? = null,
    text: (@Composable () -> Unit)? = null,
    buttons: @Composable () -> Unit,
    onDismissRequest: (() -> Unit)? = null,
    closeOnClickOutside: Boolean = true
) {
    Dialog(onDismissRequest = {
        if (closeOnClickOutside) {
            onDismissRequest?.invoke()
        }
    }) {
        Box(
            modifier = modifier
                .clip(RoundedCornerShape(18.dp))
                .background(colorResource(DesignSystem.color.background))
        ) {
            FlipperDialogContent(
                image,
                title,
                text,
                buttons,
                onDismissRequest
            )
        }
    }
}

@Composable
fun FlipperDialog(
    modifier: Modifier = Modifier,
    @DrawableRes imageId: Int? = null,
    @StringRes titleId: Int? = null,
    @StringRes textId: Int? = null,
    @StringRes buttonTextId: Int,
    onClickButton: () -> Unit,
    onDismissRequest: (() -> Unit)? = null,
    closeOnClickOutside: Boolean = true
) {
    FlipperDialog(
        modifier,
        image = imageId?.letCompose { imageIdNotNullable ->
            Image(
                painter = painterResource(imageIdNotNullable),
                contentDescription = titleId?.let { stringResource(it) }
            )
        },
        title = titleId?.letCompose { titleIdNotNullable ->
            Text(
                text = stringResource(titleIdNotNullable),
                fontWeight = FontWeight.W500,
                fontSize = 14.sp,
                color = colorResource(DesignSystem.color.black_100),
                textAlign = TextAlign.Center
            )
        },
        text = textId?.letCompose { textIdNotNullable ->
            Text(
                text = stringResource(textIdNotNullable),
                fontWeight = FontWeight.W400,
                fontSize = 14.sp,
                color = colorResource(DesignSystem.color.black_40),
                textAlign = TextAlign.Center
            )
        },
        buttons = {
            ComposableFlipperButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(buttonTextId),
                onClick = onClickButton,
                textPadding = PaddingValues(12.dp)
            )
        },
        onDismissRequest,
        closeOnClickOutside
    )
}

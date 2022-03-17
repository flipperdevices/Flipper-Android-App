package com.flipperdevices.archive.shared.composable

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flipperdevices.core.ui.R as DesignSystem
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun ComposableAppBar(
    title: String,
    onBack: (() -> Unit)? = null
) {
    ComposableAppBarInternal(title = title, onBack = onBack, endContent = null)
}

@Composable
fun ComposableAppBar(
    title: String,
    onBack: (() -> Unit)? = null,
    @DrawableRes iconId: Int,
    onIconClick: () -> Unit
) {
    ComposableAppBarInternal(title, onBack) {
        Icon(
            modifier = Modifier
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(bounded = false),
                    onClick = onIconClick
                )
                .size(size = 24.dp)
                .then(it),
            painter = painterResource(iconId),
            contentDescription = null
        )
    }
}

@Composable
private fun ComposableAppBarInternal(
    title: String,
    onBack: (() -> Unit)? = null,
    endContent: (@Composable (Modifier) -> Unit)? = null
) {
    val systemUiController = rememberSystemUiController()
    val appBarColor = colorResource(DesignSystem.color.accent)

    SideEffect {
        systemUiController.setStatusBarColor(appBarColor)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(appBarColor),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (onBack != null) {
            AppBarBackArrow(onBack)
        }
        Text(
            modifier = Modifier
                .padding(vertical = 14.dp, horizontal = 8.dp)
                .weight(weight = 1f),
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.W700,
            color = colorResource(DesignSystem.color.black_100)
        )
        if (endContent != null) {
            endContent(
                Modifier.padding(end = 14.dp)
            )
        }
    }
}

@Composable
private fun AppBarBackArrow(onBack: () -> Unit) {
    Icon(
        modifier = Modifier
            .padding(start = 14.dp, top = 8.dp, bottom = 8.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(bounded = false),
                onClick = onBack
            )
            .size(size = 24.dp),
        painter = painterResource(DesignSystem.drawable.ic_back),
        tint = colorResource(DesignSystem.color.black_100),
        contentDescription = null
    )
}

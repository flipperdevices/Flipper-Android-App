package com.flipperdevices.settings.impl.composable.elements

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flipperdevices.core.ui.R as DesignSystem

@Composable
@Suppress("LongParameterList")
fun SimpleElement(
    modifier: Modifier = Modifier,
    @DrawableRes iconId: Int? = null,
    @StringRes titleId: Int? = null,
    @StringRes descriptionId: Int? = null,
    onClick: (() -> Unit)? = null,
    isCategory: Boolean = false
) {
    SimpleElement(
        modifier,
        iconId,
        titleId?.let { stringResource(it) },
        descriptionId?.let { stringResource(it) },
        onClick,
        isCategory
    )
}

@Composable
@Suppress("LongParameterList")
fun SimpleElement(
    modifier: Modifier = Modifier,
    @DrawableRes iconId: Int? = null,
    title: String? = null,
    description: String? = null,
    onClick: (() -> Unit)? = null,
    isCategory: Boolean
) {
    var rowModifier = modifier
        .heightIn(min = 48.dp)
        .padding(all = 12.dp)
        .fillMaxWidth()
    if (onClick != null) {
        rowModifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(),
                onClick = onClick
            )
            .then(rowModifier)
    }
    Row(
        rowModifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ElementIcon(iconId)
        Column(
            modifier = Modifier,
            verticalArrangement = Arrangement.Center
        ) {
            if (title != null) {
                Text(
                    text = title,
                    fontWeight = if (isCategory) FontWeight.W700 else FontWeight.W400,
                    fontSize = if (isCategory) 16.sp else 14.sp
                )
            }
            Spacer(modifier = Modifier.height(1.dp))
            if (description != null) {
                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = colorResource(DesignSystem.color.black_40)
                )
            }
        }
    }
}

@Composable
private fun ElementIcon(@DrawableRes iconId: Int?) {
    if (iconId == null) {
        return
    }
    Icon(
        modifier = Modifier.size(size = 42.dp),
        painter = painterResource(iconId),
        contentDescription = null
    )
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
@Suppress("UnusedPrivateMember")
private fun SimpleElementPreview() {
    SimpleElement(Modifier, null, "Title", "Description", {}, false)
}

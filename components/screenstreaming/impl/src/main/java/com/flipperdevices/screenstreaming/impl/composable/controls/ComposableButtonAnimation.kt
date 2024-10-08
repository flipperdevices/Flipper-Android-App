package com.flipperdevices.screenstreaming.impl.composable.controls

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.screenstreaming.impl.model.ButtonAnimEnum
import com.flipperdevices.screenstreaming.impl.model.FlipperButtonStackElement
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@Composable
internal fun ComposableFlipperButtonAnimation(
    buttons: ImmutableList<FlipperButtonStackElement>
) {
    LazyRow(
        modifier = Modifier
            .padding(bottom = 4.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        userScrollEnabled = false
    ) {
        items(
            count = buttons.size,
            key = { buttons[it].uuid }
        ) {
            val button = buttons[it]
            Image(
                modifier = Modifier.animateItem(fadeInSpec = null, fadeOutSpec = null)
                    .size(24.dp),
                painter = painterResource(
                    if (MaterialTheme.colors.isLight) {
                        button.enum.lightId
                    } else {
                        button.enum.darkId
                    }
                ),
                contentDescription = null
            )
        }
    }
}

@Preview
@Composable
private fun ComposableFlipperButtonPreview() {
    val buttons = remember {
        mutableListOf(
            FlipperButtonStackElement(ButtonAnimEnum.BACK),
        ).toMutableStateList()
    }
    FlipperThemeInternal {
        Column(Modifier.fillMaxSize()) {
            ComposableFlipperButtonAnimation(buttons.toImmutableList())
            Button(onClick = {
                buttons.add(FlipperButtonStackElement(ButtonAnimEnum.BACK))
            }) {
                Text(text = "New button")
            }
            Button(onClick = {
                buttons.removeAt(0)
            }) {
                Text(text = "Delete")
            }
        }
    }
}

package com.flipperdevices.faphub.maincard.impl.api

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.faphub.maincard.api.MainCardApi
import com.flipperdevices.faphub.maincard.impl.composable.ComposableMainCardInternal
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class, MainCardApi::class)
class MainCardApiImpl @Inject constructor() : MainCardApi {
    @Composable
    override fun ComposableMainCard(
        modifier: Modifier,
        onClick: () -> Unit
    ) {
        ComposableMainCardInternal(
            modifier.clickable(
                interactionSource = MutableInteractionSource(),
                indication = rememberRipple(),
                onClick = onClick
            )
        )
    }
}

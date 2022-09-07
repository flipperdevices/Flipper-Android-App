package com.flipperdevices.keyscreen.emulate.model

import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import com.flipperdevices.core.ui.ktx.ComposeLottiePic
import com.flipperdevices.core.ui.ktx.painterResourceByKey

@Stable
sealed class Picture {
    @Stable
    data class StaticRes(@DrawableRes val picId: Int) : Picture() {
        @Composable
        override fun Draw(modifier: Modifier) {
            Image(
                modifier = modifier,
                painter = painterResourceByKey(picId),
                contentDescription = null
            )
        }
    }

    @Stable
    data class LottieRes(
        @RawRes val picResId: Int,
        @DrawableRes val fallBackPicResId: Int
    ) : Picture() {
        @Composable
        override fun Draw(modifier: Modifier) {
            ComposeLottiePic(
                modifier = modifier,
                picResId = picResId,
                rollBackPicResId = fallBackPicResId
            )
        }
    }

    @Composable
    abstract fun Draw(modifier: Modifier)
}

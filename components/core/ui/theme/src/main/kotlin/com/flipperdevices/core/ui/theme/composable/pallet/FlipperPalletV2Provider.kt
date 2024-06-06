package com.flipperdevices.core.ui.theme.composable.pallet

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.flipperdevices.core.ui.theme.composable.pallet.generated.FlipperPalletV2
import com.flipperdevices.core.ui.theme.composable.pallet.generated.getDarkPallet
import com.flipperdevices.core.ui.theme.composable.pallet.generated.getLightPallet
import com.flipperdevices.core.ui.theme.composable.pallet.generated.toAnimatePallet

/**
 * Please, use instead LocalPalletV2
 *
 * @return the necessary Pallet depending on the theme
 */
@Composable
fun getThemedFlipperPalletV2(isLight: Boolean): FlipperPalletV2 {
    return remember(isLight) {
        if (isLight) {
            getLightPallet()
        } else {
            getDarkPallet()
        }
    }.toAnimatePallet()
}

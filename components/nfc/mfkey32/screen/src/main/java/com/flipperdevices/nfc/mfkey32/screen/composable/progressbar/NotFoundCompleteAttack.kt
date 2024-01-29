package com.flipperdevices.nfc.mfkey32.screen.composable.progressbar

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.ktx.elements.ComposableFlipperButton
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.nfc.mfkey32.screen.R

@Composable
fun NotFoundCompleteAttack(
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
) = Column(
    modifier.fillMaxWidth(),
    horizontalAlignment = Alignment.CenterHorizontally
) {
    Text(
        modifier = Modifier.padding(18.dp),
        text = stringResource(R.string.mfkey32_complete_not_found_title),
        style = LocalTypography.current.titleB18,
        color = LocalPallet.current.text100
    )
    Image(
        modifier = Modifier.padding(horizontal = 18.dp),
        painter = painterResource(
            if (MaterialTheme.colors.isLight) {
                R.drawable.pic_shrug_white
            } else {
                R.drawable.pic_shrug_black
            }
        ),
        contentDescription = stringResource(R.string.mfkey32_complete_not_found_title)
    )
    ComposableFlipperButton(
        modifier = Modifier
            .padding(horizontal = 24.dp, vertical = 24.dp)
            .fillMaxWidth(),
        text = stringResource(R.string.mfkey32_complete_btn),
        textPadding = PaddingValues(vertical = 14.dp),
        onClick = onDone
    )
}

@Preview(
    showSystemUi = true,
    showBackground = true
)
@Composable
private fun ComposableNotFoundCompleteAttackPreview() {
    FlipperThemeInternal {
        Box {
            NotFoundCompleteAttack({})
        }
    }
}

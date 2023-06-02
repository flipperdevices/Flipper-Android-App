package com.flipperdevices.firstpair.impl.composable.tos

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.markdown.ClickableUrlText
import com.flipperdevices.core.ui.ktx.SetUpNavigationBarColor
import com.flipperdevices.core.ui.ktx.SetUpStatusBarColor
import com.flipperdevices.core.ui.ktx.elements.ComposableFlipperButton
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.firstpair.impl.R
import com.flipperdevices.core.ui.res.R as DesignSystem

@Composable
fun ComposableTOS(onApplyPress: () -> Unit, modifier: Modifier = Modifier) {
    SetUpStatusBarColor(LocalPallet.current.background)
    Column(
        modifier = modifier
            .background(LocalPallet.current.background)
    ) {
        ComposableTutorial(modifier = Modifier.weight(weight = 1f))
        ComposableFooter(onApplyPress)
    }
    SetUpNavigationBarColor(LocalPallet.current.background)
}

@Composable
private fun ComposableTutorial(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.padding(
                start = 60.dp,
                end = 60.dp,
                top = 48.dp
            ),
            text = stringResource(R.string.firstpair_tos_title),
            style = LocalTypography.current.titleB24,
            textAlign = TextAlign.Center
        )

        Column(
            modifier = Modifier.weight(weight = 1f),
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                modifier = Modifier.fillMaxWidth(),
                painter = painterResource(DesignSystem.drawable.pic_connecting),
                contentDescription = stringResource(R.string.firstpair_tos_title)
            )

            Image(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 40.dp,
                        vertical = 8.dp
                    ),
                painter = painterResource(DesignSystem.drawable.pic_flipper_instruction),
                contentDescription = stringResource(R.string.firstpair_tos_description)
            )
        }
    }
}

@Composable
fun ComposableFooter(onApplyPress: () -> Unit, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        ComposableFlipperButton(
            modifier = Modifier
                .padding(
                    horizontal = 24.dp,
                    vertical = 18.dp
                )
                .fillMaxWidth(),
            text = stringResource(R.string.firstpair_tos_button),
            onClick = onApplyPress
        )

        ClickableUrlText(
            modifier = Modifier.padding(
                start = 24.dp,
                end = 24.dp,
                bottom = 12.dp
            ),
            markdownResId = R.string.firstpair_tos_footer,
            style = LocalTypography.current.bodyR16.copy(
                color = LocalPallet.current.text30,
                textAlign = TextAlign.Center
            )
        )
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun ComposableTOSPreview() {
    ComposableTOS(onApplyPress = {})
}

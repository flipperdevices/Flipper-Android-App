package com.flipperdevices.nfc.mfkey32.screen.composable.progressbar

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
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
import com.flipperdevices.core.markdown.annotatedStringFromMarkdown
import com.flipperdevices.core.preference.pb.HardwareColor
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.nfc.mfkey32.screen.R

@Composable
fun ComposableMfKey32NotFound(color: HardwareColor) = Column(
    modifier = Modifier.fillMaxHeight(),
    horizontalAlignment = Alignment.CenterHorizontally
) {
    Text(
        modifier = Modifier.padding(top = 32.dp, bottom = 18.dp),
        text = stringResource(R.string.mfkey32_not_found_title),
        style = LocalTypography.current.titleSB18,
        color = LocalPallet.current.text100,
        textAlign = TextAlign.Center
    )
    Image(
        painter = painterResource(
            if (color == HardwareColor.BLACK) {
                R.drawable.pic_flipper_nfc_detect_reader_black
            } else R.drawable.pic_flipper_nfc_detect_reader_white
        ),
        contentDescription = stringResource(R.string.mfkey32_not_found_title)
    )
    Text(
        modifier = Modifier.padding(top = 32.dp, bottom = 14.dp),
        text = stringResource(R.string.mfkey32_not_found_desc),
        style = LocalTypography.current.bodyR16,
        color = LocalPallet.current.text80,
        textAlign = TextAlign.Center
    )

    listOf(
        R.string.mfkey32_not_found_md_1,
        R.string.mfkey32_not_found_md_2,
        R.string.mfkey32_not_found_md_3,
        R.string.mfkey32_not_found_md_4,
        R.string.mfkey32_not_found_md_5
    ).forEach { resId ->
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            text = annotatedStringFromMarkdown(resId),
            textAlign = TextAlign.Center,
            style = LocalTypography.current.bodyR14,
            color = LocalPallet.current.text40
        )
    }
}

@Preview(
    showSystemUi = true,
    showBackground = true
)
@Composable
private fun ComposableMfKey32NotFoundPreview() {
    FlipperThemeInternal {
        ComposableMfKey32NotFound(HardwareColor.BLACK)
    }
}

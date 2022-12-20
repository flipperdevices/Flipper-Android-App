package com.flipperdevices.nfc.mfkey32.screen.composable.progressbar.error

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.markdown.annotatedStringFromMarkdown
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.nfc.mfkey32.screen.R
import com.flipperdevices.core.ui.res.R as DesignSystem

@Composable
fun ComposableMfKey32ConnectFailed() = ComposableMfKey32ErrorContent(
    titleId = R.string.mfkey32_error_connect_title,
    picId = DesignSystem.drawable.pic_flipper_update_failed,
    picIdBlack = DesignSystem.drawable.pic_black_flipper_update_failed
) {
    listOf(
        R.string.mfkey32_error_connect_desc_1,
        R.string.mfkey32_error_connect_desc_2,
        R.string.mfkey32_error_connect_desc_3,
        R.string.mfkey32_error_connect_desc_4
    ).forEach { resId ->
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            text = annotatedStringFromMarkdown(resId),
            textAlign = TextAlign.Start,
            style = LocalTypography.current.bodyR16,
            color = LocalPallet.current.text40
        )
    }
}

@Preview(
    showSystemUi = true,
    showBackground = true
)
@Composable
private fun ComposableMfKey32ReadWritePreview() {
    FlipperThemeInternal {
        ComposableMfKey32ConnectFailed()
    }
}

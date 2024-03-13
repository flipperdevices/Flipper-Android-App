package com.flipperdevices.faphub.installedtab.impl.composable.button

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.faphub.installedtab.impl.R
import com.flipperdevices.faphub.installedtab.impl.model.InstalledNetworkErrorEnum

@Composable
fun ComposableErrorButton(
    @Suppress("UnusedParameter")
    installedNetworkErrorEnum: InstalledNetworkErrorEnum,
    modifier: Modifier = Modifier
) = Column(
    modifier = modifier,
    horizontalAlignment = Alignment.CenterHorizontally
) {
    Row(Modifier.padding(8.dp)) {
        Icon(
            modifier = Modifier.size(14.dp),
            painter = painterResource(R.drawable.ic_pull_to_refresh),
            contentDescription = stringResource(R.string.faphub_installed_error_pull_to_refresh),
            tint = LocalPallet.current.text40
        )
        Text(
            modifier = Modifier.padding(horizontal = 2.dp),
            text = stringResource(R.string.faphub_installed_error_pull_to_refresh),
            style = LocalTypography.current.subtitleM12,
            color = LocalPallet.current.text40
        )
    }
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(LocalPallet.current.onErrorBorder)
            .padding(1.dp)
            .clip(RoundedCornerShape(7.dp))
            .background(LocalPallet.current.onErrorBackground)
            .padding(8.dp),
        text = stringResource(R.string.faphub_installed_error_desc),
        style = LocalTypography.current.subtitleM12,
        color = LocalPallet.current.onError,
        textAlign = TextAlign.Center
    )
}

@Preview(
    showBackground = true,
    showSystemUi = true,
)
@Composable
private fun ComposableErrorButtonPreview() {
    FlipperThemeInternal {
        ComposableErrorButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            installedNetworkErrorEnum = InstalledNetworkErrorEnum.GENERAL
        )
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun ComposableErrorButtonNightPreview() {
    FlipperThemeInternal {
        ComposableErrorButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            installedNetworkErrorEnum = InstalledNetworkErrorEnum.GENERAL
        )
    }
}

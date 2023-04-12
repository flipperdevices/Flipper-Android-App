package com.flipperdevices.settings.impl.composable.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import com.flipperdevices.core.ui.ktx.OrangeAppBar
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.settings.impl.R

@Composable
fun ComposableMigrateRemoteControlScreen(
    onBack: () -> Unit = {}
) {
    Column {
        OrangeAppBar(
            titleId = R.string.options_remote_control_title,
            onBack = onBack
        )
        ComposableMigrateDescription(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        )
    }
}

@Composable
private fun ComposableMigrateDescription(
    modifier: Modifier = Modifier
) = Column(
    modifier = modifier,
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
) {
    val picId = if (MaterialTheme.colors.isLight) {
        R.drawable.pic_migrate_remote_control_light
    } else {
        R.drawable.pic_migrate_remote_control_dark
    }
    Image(
        painter = painterResource(picId),
        contentDescription = stringResource(R.string.options_remote_control_subtitle)
    )
    Text(
        modifier = Modifier.padding(top = 24.dp),
        text = stringResource(R.string.options_remote_control_subtitle),
        style = LocalTypography.current.bodyM14,
        color = LocalPallet.current.text100
    )
    Text(
        modifier = Modifier.padding(top = 6.dp),
        text = stringResource(R.string.options_remote_control_desc),
        style = LocalTypography.current.bodyR14,
        color = LocalPallet.current.text40
    )
}

@Preview
@Composable
private fun ComposableMigrateRemoteControlScreenPreview() {
    FlipperThemeInternal {
        ComposableMigrateRemoteControlScreen({})
    }
}
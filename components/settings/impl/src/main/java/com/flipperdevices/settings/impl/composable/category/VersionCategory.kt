package com.flipperdevices.settings.impl.composable.category

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.ktx.clickableNullIndication
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.settings.impl.R

private const val EXPERT_MODE_CLICK_COUNT = 10

@Composable
fun VersionCategory(version: String, onActivateExpertMode: () -> Unit) {
    var howMuchClick by remember { mutableStateOf(0) }
    val versionText = "${stringResource(id = R.string.version)}: $version"
    Column(
        modifier = Modifier.fillMaxWidth().clickableNullIndication {
            howMuchClick++
            if (howMuchClick > EXPERT_MODE_CLICK_COUNT) {
                onActivateExpertMode()
            }
        },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.version_name_app),
            color = LocalPallet.current.text20,
            style = LocalTypography.current.subtitleR12
        )
        Text(
            text = versionText,
            color = LocalPallet.current.text40,
            style = LocalTypography.current.subtitleR12
        )
        Spacer(modifier = Modifier.height(14.dp))
    }
}

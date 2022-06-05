package com.flipperdevices.settings.impl.composable.category

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flipperdevices.core.ui.R as DesignSystem
import com.flipperdevices.settings.impl.R

@Composable
fun VersionCategory(version: String) {
    val versionText = "${stringResource(id = R.string.version)}: $version"
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.version_name_app),
            fontSize = 12.sp,
            fontWeight = FontWeight.W400,
            color = colorResource(id = DesignSystem.color.black_20)
        )
        Text(
            text = versionText,
            fontSize = 12.sp,
            fontWeight = FontWeight.W400,
            color = colorResource(id = DesignSystem.color.black_40)
        )
        Spacer(modifier = Modifier.height(14.dp))
    }
}

package com.flipperdevices.faphub.report.impl.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.ktx.elements.ComposableFlipperButton
import com.flipperdevices.core.ui.ktx.elements.ComposableReportTextField
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.faphub.report.impl.R

@Composable
internal fun ComposableReadyToReport(
    submit: (String) -> Unit
) = Column(
    Modifier
        .fillMaxSize()
        .padding(horizontal = 14.dp, vertical = 14.dp)
) {
    var text by remember { mutableStateOf("") }

    Box(Modifier.weight(1f)) {
        ComposableReportTextField(
            value = text,
            title = {
                Text(
                    text = stringResource(R.string.fap_report_field_title),
                    style = LocalTypography.current.buttonB16.copy(
                        color = LocalPallet.current.text100
                    ),
                )
            },
            placeholder = {
                Text(
                    text = stringResource(R.string.fap_report_field_desc),
                    style = LocalTypography.current.bodyR14.copy(
                        color = LocalPallet.current.reportBorder
                    )
                )
            },
            onValueChange = { text = it },
            modifier = Modifier.height(250.dp)
        )
    }

    ComposableFlipperButton(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        text = stringResource(R.string.fap_report_field_btn),
        onClick = { submit(text) },
        enabled = text.isNotEmpty()
    )
}

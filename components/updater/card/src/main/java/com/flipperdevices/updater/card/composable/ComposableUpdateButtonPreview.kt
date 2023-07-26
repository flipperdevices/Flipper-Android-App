package com.flipperdevices.updater.card.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.updater.model.DistributionFile
import com.flipperdevices.updater.model.FirmwareChannel
import com.flipperdevices.updater.model.FirmwareVersion
import com.flipperdevices.updater.model.OfficialFirmware
import com.flipperdevices.updater.model.UpdateCardState
import com.flipperdevices.updater.model.UpdateRequest

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun ComposableUpdateButtonPreview() {
    FlipperThemeInternal {
        val version = FirmwareVersion(
            channel = FirmwareChannel.RELEASE,
            version = "1.1.1"
        )
        val updateCardState = setOf(
            UpdateCardState.NoUpdate(flipperVersion = version),
            UpdateCardState.UpdateAvailable(
                update = UpdateRequest(
                    updateFrom = version,
                    updateTo = version,
                    content = OfficialFirmware(DistributionFile(url = "", sha256 = "")),
                    changelog = null
                ),
                isOtherChannel = false
            )
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
                .background(LocalPallet.current.background)
        ) {
            updateCardState.forEach {
                ComposableUpdateButton(it, false, onStartUpdateRequest = {})
                ComposableUpdateButton(it, true, onStartUpdateRequest = {})
            }
        }
    }
}

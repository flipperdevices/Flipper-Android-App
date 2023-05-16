package com.flipperdevices.updater.card.composable.dialogs

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.withStyle
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.info.shared.getColorByChannel
import com.flipperdevices.info.shared.getTextByVersion
import com.flipperdevices.updater.card.R
import com.flipperdevices.updater.model.FirmwareChannel
import com.flipperdevices.updater.model.FirmwareVersion

@Composable
fun annotatedStringWithVersion(
    version: FirmwareVersion?,
    channelColor: Color? = null
): AnnotatedString {
    val versionText = version?.let { getTextByVersion(it) }
        ?: stringResource(R.string.update_card_dialog_unknown_version)
    val color = channelColor ?: if (version == null) {
        LocalPallet.current.channelFirmwareUnknown
    } else {
        when (version.channel) {
            FirmwareChannel.RELEASE,
            FirmwareChannel.RELEASE_CANDIDATE,
            FirmwareChannel.DEV -> getColorByChannel(version.channel)

            else -> LocalPallet.current.text100
        }
    }
    return buildAnnotatedString {
        withStyle(style = SpanStyle(color = color)) {
            append(versionText.capitalize(Locale.current))
        }
    }
}

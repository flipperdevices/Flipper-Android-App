package com.flipperdevices.faphub.fapscreen.impl.composable.header

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ktx.jre.toFormattedSize
import com.flipperdevices.core.ui.ktx.placeholderConnecting
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.faphub.dao.api.model.FapMetaInformation
import com.flipperdevices.faphub.fapscreen.impl.R

private val DEFAULT_INFORMATION_VALUE
    get() = String((Array(size = 10) { 'L' }).toCharArray())

@Composable
internal fun ComposableFapMetaInformation(
    metaInformation: FapMetaInformation?,
    modifier: Modifier = Modifier
) = Row(
    modifier = modifier
        .fillMaxWidth()
        .height(IntrinsicSize.Min),
    verticalAlignment = Alignment.CenterVertically
) {
    val informationColumnModifier = Modifier.weight(1f)

    ComposableInformationColumn(
        modifier = informationColumnModifier,
        name = stringResource(R.string.fapscreen_information_version),
        value = metaInformation?.version?.toString()
    )
    ComposableInformationDivider()
    ComposableInformationColumn(
        modifier = informationColumnModifier,
        name = stringResource(R.string.fapscreen_information_size),
        value = metaInformation?.sizeBytes?.toFormattedSize() ?: stringResource(
            R.string.fapscreen_information_size_unknown
        )
    )
}

@Composable
private fun ComposableInformationColumn(
    name: String,
    value: String?,
    modifier: Modifier = Modifier
) = Column(
    modifier = modifier,
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
) {
    Text(
        modifier = Modifier.padding(bottom = 4.dp),
        text = name,
        style = LocalTypography.current.subtitleR10,
        color = LocalPallet.current.text40
    )
    Text(
        modifier = if (value == null) Modifier.placeholderConnecting() else Modifier,
        text = value ?: DEFAULT_INFORMATION_VALUE,
        style = LocalTypography.current.subtitleR12,
        color = LocalPallet.current.text100
    )
}

@Composable
private fun ComposableInformationDivider() = Divider(
    modifier = Modifier
        .fillMaxHeight()
        .width(1.dp),
    color = LocalPallet.current.fapHubDividerColor
)

@Preview(
    showBackground = true
)
@Composable
private fun ComposableFapMetaInformationPreview() {
    FlipperThemeInternal {
        ComposableFapMetaInformation(
            modifier = Modifier,
            metaInformation = null
        )
    }
}

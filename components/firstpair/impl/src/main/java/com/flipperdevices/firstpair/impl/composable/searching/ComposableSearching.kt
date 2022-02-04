package com.flipperdevices.firstpair.impl.composable.searching

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.flipperdevices.bridge.api.scanner.DiscoveredBluetoothDevice
import com.flipperdevices.core.ui.R as DesignSystem
import com.flipperdevices.core.ui.composable.ComposeLottiePic
import com.flipperdevices.firstpair.impl.R
import com.flipperdevices.firstpair.impl.viewmodels.searching.BLEDeviceViewModel

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun ComposableSearchingScreen(
    devicesViewModel: BLEDeviceViewModel = viewModel()
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        ComposableSearchingAppBar {}
        ComposableTitle()
        ComposableSearchingTitle {}
        ComposableSearchingItems(
            modifier = Modifier.weight(weight = 1f),
            devicesViewModel
        )
        ComposableSearchingFooter {}
    }
}

@Composable
private fun ComposableSearchingAppBar(
    onBack: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.CenterStart
    ) {
        Icon(
            modifier = Modifier
                .padding(vertical = 12.dp, horizontal = 14.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(bounded = false),
                    onClick = onBack
                ),
            painter = painterResource(DesignSystem.drawable.ic_back),
            contentDescription = stringResource(R.string.firstpair_search_back)
        )
    }
}

@Composable
private fun ComposableTitle() {
    Text(
        modifier = Modifier.padding(top = 6.dp, bottom = 48.dp),
        text = stringResource(R.string.firstpair_search_title),
        fontWeight = FontWeight.W700,
        color = colorResource(DesignSystem.color.black_100),
        fontSize = 22.sp
    )
}

@Composable
private fun ComposableSearchingTitle(
    onHelpClicking: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(weight = 1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.padding(
                    end = 8.dp, top = 8.dp, bottom = 8.dp, start = 18.dp
                ),
                text = stringResource(R.string.firstpair_search_title_status_text),
                color = colorResource(DesignSystem.color.black_100),
                fontSize = 18.sp,
                fontWeight = FontWeight.W500
            )
            CircularProgressIndicator(
                modifier = Modifier.size(size = 20.dp),
                strokeWidth = 2.dp,
                color = colorResource(DesignSystem.color.black_30)
            )
        }
        Row(
            modifier = Modifier
                .padding(end = 10.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(),
                    onClick = onHelpClicking
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.padding(all = 8.dp),
                text = stringResource(R.string.firstpair_search_title_help),
                color = colorResource(DesignSystem.color.black_30),
                fontSize = 16.sp,
                fontWeight = FontWeight.W500
            )
            Icon(
                modifier = Modifier
                    .size(height = 24.dp, width = 32.dp) // 24 (width) + 8 (padding) = 32
                    .padding(end = 8.dp),
                painter = painterResource(R.drawable.ic_help),
                contentDescription = stringResource(R.string.firstpair_search_title_help),
                tint = colorResource(DesignSystem.color.black_30)
            )
        }
    }
}

@Composable
private fun ComposableSearchingItems(
    modifier: Modifier = Modifier,
    devicesViewModel: BLEDeviceViewModel
) {
    val devices by devicesViewModel.getState().collectAsState()

    if (devices.isEmpty()) {
        Box(
            modifier,
            contentAlignment = Alignment.Center
        ) {
            ComposableSearchingProgress()
        }
        return
    }

    val filteredDevices = devices.filterNot { it.name.isNullOrEmpty() }

    ComposableSearchingFoundedItems(modifier, filteredDevices)
}

@Composable
private fun ComposableSearchingProgress() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.padding(bottom = 36.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier.size(size = 82.dp),
                painter = painterResource(R.drawable.pic_phone),
                contentDescription = null
            )
            ComposeLottiePic(
                modifier = Modifier.size(size = 32.dp),
                picResId = R.raw.dots_loader,
                rollBackPicResId = R.drawable.pic_loader
            )
            Icon(
                modifier = Modifier
                    .size(size = 82.dp)
                    .padding(start = 13.dp),
                painter = painterResource(R.drawable.pic_flipper_heavy),
                contentDescription = stringResource(R.string.firstpair_search_flipper_status),
                tint = colorResource(DesignSystem.color.accent_secondary)
            )
        }

        Text(
            text = stringResource(R.string.firstpair_search_loader_text),
            color = colorResource(DesignSystem.color.black_30),
            fontWeight = FontWeight.W400,
            fontSize = 18.sp
        )
    }
}

@Composable
private fun ComposableSearchingFoundedItems(
    modifier: Modifier = Modifier,
    devices: List<DiscoveredBluetoothDevice>
) {
    LazyColumn(modifier = modifier) {
        items(devices) {
            val name = it.name!!.replaceFirst("Flipper", "")
            ComposableSearchItem(text = name)
        }
    }
}

@Composable
private fun ComposableSearchingFooter(
    onClickSkipConnection: () -> Unit
) {
    Text(
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(),
                onClick = onClickSkipConnection
            )
            .padding(all = 8.dp)
            .fillMaxWidth(),
        text = stringResource(R.string.firstpair_search_skip_connection),
        color = colorResource(DesignSystem.color.accent_secondary),
        fontWeight = FontWeight.W500,
        fontSize = 16.sp,
        textAlign = TextAlign.Center
    )
}

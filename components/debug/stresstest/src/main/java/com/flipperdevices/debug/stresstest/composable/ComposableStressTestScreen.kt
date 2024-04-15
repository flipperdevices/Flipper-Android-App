package com.flipperdevices.debug.stresstest.composable

import android.text.format.Formatter
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.flipperdevices.bridge.api.model.FlipperSerialSpeed
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.debug.stresstest.model.LogLine
import com.flipperdevices.debug.stresstest.model.StressTestState
import com.flipperdevices.debug.stresstest.viewmodel.StressTestViewModel
import kotlinx.collections.immutable.ImmutableList

@Composable
fun ComposableStressTestScreen(
    viewModel: StressTestViewModel,
    modifier: Modifier = Modifier,
) {
    val debugLog by viewModel.getDebugLog().collectAsState()
    val stressTestState by viewModel.getStressTestState().collectAsState()
    val speedState by viewModel.getSpeed().collectAsState()

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ComposableStatus(stressTestState)
        ComposableLog(
            debugLog,
            Modifier.weight(weight = 1f)
        )
        ComposableButton(
            onStartBruteforce = viewModel::startBruteforce,
            onStopBruteforce = viewModel::stopBruteforce,
        )
        ComposableSpeed(speedState)
    }
}

@Composable
private fun ComposableStatus(
    stressTestState: StressTestState
) {
    Text(
        modifier = Modifier.padding(all = 16.dp),
        text = "Success: ${stressTestState.successfulCount} " +
            "Error: ${stressTestState.errorCount}",
        style = LocalTypography.current.titleB24
    )
}

@Composable
private fun ComposableLog(
    debugLog: ImmutableList<LogLine>,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            itemsIndexed(debugLog.asReversed()) { index, line ->
                ComposableLogLine(line)
                if (index < debugLog.lastIndex) {
                    Divider(color = LocalPallet.current.divider12, thickness = 1.dp)
                }
            }
        }
    }
}

@Composable
private fun ComposableLogLine(line: LogLine) {
    var style = LocalTypography.current.bodyR14
    if (line.color != null) style = style.copy(color = line.color)
    Text(
        modifier = Modifier.padding(all = 8.dp),
        text = line.text,
        style = style
    )
}

@Composable
private fun ComposableButton(
    onStartBruteforce: () -> Unit,
    onStopBruteforce: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TextButton(
            onClick = onStartBruteforce
        ) {
            Text(
                modifier = Modifier.padding(all = 16.dp),
                text = "Start",
                fontSize = 24.sp,
                color = LocalPallet.current.text100
            )
        }
        TextButton(onClick = onStopBruteforce) {
            Text(
                modifier = Modifier.padding(all = 16.dp),
                text = "Stop",
                fontSize = 24.sp,
                color = LocalPallet.current.text100
            )
        }
    }
}

@Composable
private fun ColumnScope.ComposableSpeed(speedState: FlipperSerialSpeed) {
    var maxReceiveSpeed by remember { mutableLongStateOf(0L) }
    var maxTransmitSpeed by remember { mutableLongStateOf(0L) }

    if (speedState.receiveBytesInSec > maxReceiveSpeed) {
        maxReceiveSpeed = speedState.receiveBytesInSec
    }

    if (speedState.transmitBytesInSec > maxTransmitSpeed) {
        maxTransmitSpeed = speedState.transmitBytesInSec
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        val rx = Formatter.formatFileSize(LocalContext.current, speedState.receiveBytesInSec)
        val tx = Formatter.formatFileSize(LocalContext.current, speedState.transmitBytesInSec)
        Text("Receive speed: $rx/s")
        Text("Send speed: $tx/s")
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        val rx = Formatter.formatFileSize(LocalContext.current, maxReceiveSpeed)
        val tx = Formatter.formatFileSize(LocalContext.current, maxTransmitSpeed)
        Text("Max: $rx/s")
        Text("Max: $tx/s")
    }
}

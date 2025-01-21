package com.flipperdevices.bridge.impl.scanner

import android.annotation.SuppressLint
import com.flipperdevices.core.log.TaggedLogger
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.flow.callbackFlow
import no.nordicsemi.android.support.v18.scanner.BluetoothLeScannerCompat
import no.nordicsemi.android.support.v18.scanner.ScanCallback
import no.nordicsemi.android.support.v18.scanner.ScanFilter
import no.nordicsemi.android.support.v18.scanner.ScanResult
import no.nordicsemi.android.support.v18.scanner.ScanSettings

private val timber = TaggedLogger("FlowScanCallback")

internal fun BluetoothLeScannerCompat.scanFlow(
    settings: ScanSettings,
    filters: List<ScanFilter> = emptyList()
) = callbackFlow {
    val callback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            trySend(result)
                .onFailure {
                    timber.error(it) { "On send scan result" }
                }
        }

        @SuppressLint("MissingPermission")
        override fun onBatchScanResults(results: MutableList<ScanResult>) {
            results.forEach { result ->
                trySend(result).onFailure { sendError ->
                    timber.error(sendError) { "On send batch scan results" }
                }
            }
        }

        override fun onScanFailed(errorCode: Int) {
            timber.error { "Scan failed $errorCode" }
        }
    }
    startScan(filters, settings, callback)
    info { "Start scan with filter $filters and settings $settings" }

    awaitClose {
        stopScan(callback)
    }
}

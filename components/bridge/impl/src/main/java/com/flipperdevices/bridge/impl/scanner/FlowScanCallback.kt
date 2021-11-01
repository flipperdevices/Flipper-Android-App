package com.flipperdevices.bridge.impl.scanner

import com.flipperdevices.core.log.TaggedTimber
import com.flipperdevices.core.log.debug
import com.flipperdevices.core.log.error
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.flow.callbackFlow
import no.nordicsemi.android.support.v18.scanner.BluetoothLeScannerCompat
import no.nordicsemi.android.support.v18.scanner.ScanCallback
import no.nordicsemi.android.support.v18.scanner.ScanFilter
import no.nordicsemi.android.support.v18.scanner.ScanResult
import no.nordicsemi.android.support.v18.scanner.ScanSettings

private val timber = TaggedTimber("FlowScanCallback")

@ExperimentalCoroutinesApi
fun BluetoothLeScannerCompat.scanFlow(
    settings: ScanSettings,
    filters: List<ScanFilter> = emptyList()
) = callbackFlow {
    val callback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            timber.debug { "New device (callback=$callbackType): $result" }
            trySend(result)
                .onFailure {
                    timber.error(it) { "On send scan result" }
                }
        }

        override fun onBatchScanResults(results: MutableList<ScanResult>) {
            results.forEach { result ->
                timber.debug { "New device in batch(size=${results.size}): ${result.device.name}" }
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

    awaitClose {
        stopScan(callback)
    }
}

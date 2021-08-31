package com.flipper.bridge.impl.scanner

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.flow.callbackFlow
import no.nordicsemi.android.support.v18.scanner.BluetoothLeScannerCompat
import no.nordicsemi.android.support.v18.scanner.ScanCallback
import no.nordicsemi.android.support.v18.scanner.ScanFilter
import no.nordicsemi.android.support.v18.scanner.ScanResult
import no.nordicsemi.android.support.v18.scanner.ScanSettings
import timber.log.Timber

private val timber = Timber.tag("FlowScanCallback")

@ExperimentalCoroutinesApi
fun BluetoothLeScannerCompat.scanFlow(
    settings: ScanSettings,
    filters: List<ScanFilter> = emptyList()
) = callbackFlow {
    val callback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            timber.d("New device (callback=$callbackType): $result")
            trySend(result)
                .onFailure {
                    timber.e(it)
                }
        }

        override fun onBatchScanResults(results: MutableList<ScanResult>) {
            results.forEach { result ->
                timber.d("New device in batch(size=${results.size}): ${result?.device?.name}")
                trySend(result).onFailure { sendError ->
                    timber.e(sendError)
                }
            }
        }

        override fun onScanFailed(errorCode: Int) {
            timber.e("Scan failed $errorCode")
        }
    }
    startScan(filters, settings, callback)

    awaitClose {
        stopScan(callback)
    }
}
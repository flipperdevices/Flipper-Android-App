package com.flipperdevices.nfceditor.impl.api

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.keyparser.api.model.FlipperKeyParsed
import com.flipperdevices.metric.api.MetricApi
import com.flipperdevices.metric.api.events.complex.DebugInfoEnum
import com.flipperdevices.metric.api.events.complex.DebugInfoEvent
import com.flipperdevices.nfceditor.api.NfcEditorApi
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@Suppress("MagicNumber")
private val SUPPORTED_NFC_FORMATS = intArrayOf(2, 3, 4)
private const val SUPPORTED_NFC_TYPE = "Mifare Classic"

@ContributesBinding(AppGraph::class)
class NfcEditorApiImpl @Inject constructor(
    private val metricApi: MetricApi
) : NfcEditorApi {

    override fun isSupportedByNfcEditor(parsedKey: FlipperKeyParsed): Boolean {
        return parsedKey is FlipperKeyParsed.NFC &&
            SUPPORTED_NFC_FORMATS.contains(parsedKey.version) &&
            parsedKey.deviceType == SUPPORTED_NFC_TYPE
    }

    override fun reportUnsupportedFormat(parsedKey: FlipperKeyParsed) {
        if (parsedKey !is FlipperKeyParsed.NFC) {
            // Report only NFC
            return
        }
        if (parsedKey.deviceType != SUPPORTED_NFC_TYPE) {
            // Report only Mifare Classic formats
            return
        }
        if (SUPPORTED_NFC_FORMATS.contains(parsedKey.version)) {
            // Report only unsupported format
            return
        }
        metricApi.reportComplexEvent(
            DebugInfoEvent(
                DebugInfoEnum.NFC_UNSUPPORTED_EDIT,
                parsedKey.version.toString()
            )
        )
    }
}

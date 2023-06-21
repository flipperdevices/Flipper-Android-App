package com.flipperdevices.nfceditor.impl.api

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.keyparser.api.model.FlipperKeyParsed
import com.flipperdevices.nfceditor.api.NfcEditorApi
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@Suppress("MagicNumber")
private val SUPPORTED_NFC_FORMATS = arrayOf(2, 3)
private const val SUPPORTED_NFC_TYPE = "Mifare Classic"

@ContributesBinding(AppGraph::class)
class NfcEditorApiImpl @Inject constructor() : NfcEditorApi {

    override fun isSupportedByNfcEditor(parsedKey: FlipperKeyParsed): Boolean {
        return parsedKey is FlipperKeyParsed.NFC &&
            SUPPORTED_NFC_FORMATS.contains(parsedKey.version) &&
            parsedKey.deviceType == SUPPORTED_NFC_TYPE
    }
}

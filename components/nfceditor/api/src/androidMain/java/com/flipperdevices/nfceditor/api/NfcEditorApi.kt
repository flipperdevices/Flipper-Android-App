package com.flipperdevices.nfceditor.api

import com.flipperdevices.keyparser.api.model.FlipperKeyParsed

interface NfcEditorApi {
    fun isSupportedByNfcEditor(parsedKey: FlipperKeyParsed): Boolean

    fun reportUnsupportedFormat(parsedKey: FlipperKeyParsed)
}

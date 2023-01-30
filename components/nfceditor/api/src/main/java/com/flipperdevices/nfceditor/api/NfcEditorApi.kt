package com.flipperdevices.nfceditor.api

import com.flipperdevices.bridge.dao.api.model.parsed.FlipperKeyParsed

interface NfcEditorApi {
    fun isSupportedByNfcEditor(parsedKey: FlipperKeyParsed): Boolean
}

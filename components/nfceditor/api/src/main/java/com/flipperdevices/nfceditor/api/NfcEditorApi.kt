package com.flipperdevices.nfceditor.api

import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.parsed.FlipperKeyParsed
import com.github.terrakok.cicerone.Screen

interface NfcEditorApi {
    fun getNfcEditorScreen(flipperKey: FlipperKey): Screen

    fun isSupportedByNfcEditor(parsedKey: FlipperKeyParsed): Boolean
}

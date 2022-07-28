package com.flipperdevices.nfceditor.impl.api

import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.parsed.FlipperKeyParsed
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.nfceditor.api.NfcEditorApi
import com.flipperdevices.nfceditor.impl.fragments.NfcEditorFragment
import com.github.terrakok.cicerone.Screen
import com.github.terrakok.cicerone.androidx.FragmentScreen
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

private const val SUPPORTED_NFC_FORMAT = 2
private const val SUPPORTED_NFC_TYPE = "Mifare Classic"

@ContributesBinding(AppGraph::class)
class NfcEditorApiImpl @Inject constructor() : NfcEditorApi {
    override fun getNfcEditorScreen(flipperKey: FlipperKey): Screen {
        return FragmentScreen { NfcEditorFragment.getInstance(flipperKey) }
    }

    override fun isSupportedByNfcEditor(parsedKey: FlipperKeyParsed): Boolean {
        return parsedKey is FlipperKeyParsed.NFC &&
            parsedKey.version == SUPPORTED_NFC_FORMAT &&
            parsedKey.deviceType == SUPPORTED_NFC_TYPE
    }
}

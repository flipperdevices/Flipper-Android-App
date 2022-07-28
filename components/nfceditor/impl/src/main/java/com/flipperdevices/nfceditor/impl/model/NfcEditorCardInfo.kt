package com.flipperdevices.nfceditor.impl.model

import androidx.compose.runtime.Stable

@Stable
data class NfcEditorCardInfo(
    val cardType: NfcEditorCardType,
    val uid: String?,
    val atqa: String?,
    val sak: String?
)

enum class NfcEditorCardType {
    MF_1K,
    MF_4K
}

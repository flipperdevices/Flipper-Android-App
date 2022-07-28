package com.flipperdevices.nfceditor.impl.model

enum class NfcTypeCard(
    val nameType: String,
    val UID: String,
    val ATQA: String,
    val SAK: String
) {
    Classic4k(
        nameType = "MIFARE Classic 4k",
        UID = "B6 69 03 36 8A 98 02",
        ATQA = "02 02",
        SAK = "98"
    )
}

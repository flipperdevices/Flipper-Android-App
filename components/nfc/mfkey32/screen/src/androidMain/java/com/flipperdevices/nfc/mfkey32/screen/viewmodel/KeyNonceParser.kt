package com.flipperdevices.nfc.mfkey32.screen.viewmodel

import com.flipperdevices.nfc.tools.api.MfKey32Nonce

private const val KEY_SEC = "sec"
private const val KEY_KEY = "key"
private const val KEY_UID = "cuid"
private const val KEY_NT0 = "nt0"
private const val KEY_AR0 = "ar0"
private const val KEY_NR0 = "nr0"
private const val KEY_NT1 = "nt1"
private const val KEY_NR1 = "nr1"
private const val KEY_AR1 = "ar1"

object KeyNonceParser {
    fun parse(text: String): List<MfKey32Nonce> {
        return text.lines().mapNotNull { parseLine(it) }
    }

    // Sample: Sec 2 key A cuid 2a234f80 nt0 55721809 nr0 ce9985f6 ar0 772f55be nt1 a27173f2 nr1 e386b505 ar1 5fa65203
    private fun parseLine(line: String): MfKey32Nonce? {
        if (line.isBlank()) return null
        val blocks = line.split(" ").map { it.lowercase() }
        val params = mutableMapOf<String, String?>()
        for (i in 0..blocks.lastIndex step 2) {
            val key = blocks.getOrNull(i)
            val value = blocks.getOrNull(i + 1)
            if (key != null && value != null) {
                params[key.lowercase()] = value
            }
        }
        return MfKey32Nonce(
            sectorName = params[KEY_SEC] ?: return null,
            keyName = params[KEY_KEY] ?: return null,
            uid = params[KEY_UID]?.toUIntOrNull(radix = 16) ?: return null,
            nt0 = params[KEY_NT0]?.toUIntOrNull(radix = 16) ?: return null,
            nr0 = params[KEY_NR0]?.toUIntOrNull(radix = 16) ?: return null,
            ar0 = params[KEY_AR0]?.toUIntOrNull(radix = 16) ?: return null,
            nt1 = params[KEY_NT1]?.toUIntOrNull(radix = 16) ?: return null,
            nr1 = params[KEY_NR1]?.toUIntOrNull(radix = 16) ?: return null,
            ar1 = params[KEY_AR1]?.toUIntOrNull(radix = 16) ?: return null
        )
    }
}

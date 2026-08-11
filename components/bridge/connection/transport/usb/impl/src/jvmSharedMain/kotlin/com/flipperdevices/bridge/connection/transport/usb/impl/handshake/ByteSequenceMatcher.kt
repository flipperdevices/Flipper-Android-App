package com.flipperdevices.bridge.connection.transport.usb.impl.handshake

private fun buildPrefixFunction(pattern: ByteArray): IntArray {
    val prefixFunction = IntArray(pattern.size)
    var matchedLength = 0
    for (index in 1 until pattern.size) {
        while (matchedLength > 0 && pattern[index] != pattern[matchedLength]) {
            matchedLength = prefixFunction[matchedLength - 1]
        }
        if (pattern[index] == pattern[matchedLength]) {
            matchedLength++
        }
        prefixFunction[index] = matchedLength
    }
    return prefixFunction
}

/**
 * Incremental Knuth-Morris-Pratt matcher for finding a byte sequence in
 * a stream consumed chunk by chunk.
 *
 * Unlike naive matching it correctly handles patterns with repeated
 * prefixes (e.g. finding "\r\n\r\n>: " in a stream containing
 * "\r\n\r\n\r\n>: ") and matches that span chunk boundaries.
 */
class ByteSequenceMatcher(
    private val pattern: ByteArray
) {
    private val prefixFunction = buildPrefixFunction(pattern)
    private var matchedLength = 0

    init {
        require(pattern.isNotEmpty()) { "Pattern must not be empty" }
    }

    /**
     * Feeds [chunk] into the matcher. Returns the index right after the
     * end of the first match, or -1 if the pattern is not fully matched yet.
     */
    fun indexAfterMatch(chunk: ByteArray): Int {
        for (index in chunk.indices) {
            val currentByte = chunk[index]
            while (matchedLength > 0 && pattern[matchedLength] != currentByte) {
                matchedLength = prefixFunction[matchedLength - 1]
            }
            if (pattern[matchedLength] == currentByte) {
                matchedLength++
            }
            if (matchedLength == pattern.size) {
                matchedLength = 0
                return index + 1
            }
        }
        return -1
    }
}

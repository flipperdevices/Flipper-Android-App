package com.flipperdevices.nfceditor.impl.viewmodel

import com.flipperdevices.core.ktx.jre.then

const val DELETE_SYMBOL = "?"
private val ALLOWED_SYMBOL = "^[0-9A-F?]*\$".toRegex()

class TextUpdaterHelper {
    fun getProcessedText(
        originalText: String,
        newText: String,
        oldPosition: Int,
        newPosition: Int
    ): String {
        val newTextFormatted = newText.uppercase()
        if (!ALLOWED_SYMBOL.matches(newTextFormatted)) {
            return originalText
        }

        val (minPosition, maxPosition, isDelete) = if (oldPosition < newPosition) {
            oldPosition then newPosition then false
        } else if (newPosition < oldPosition) {
            newPosition then oldPosition then true
        } else return originalText

        val updatedText = if (isDelete) {
            originalText.replaceRange(
                minPosition,
                maxPosition,
                DELETE_SYMBOL.repeat(maxPosition - minPosition)
            )
        } else {
            val changedText = newTextFormatted.substring(minPosition, maxPosition)
            originalText.replaceRange(minPosition, maxPosition, changedText)
        }

        return updatedText
    }
}

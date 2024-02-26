package com.flipperdevices.core.ktx.jre

import java.nio.ByteBuffer
import kotlin.math.ceil
import kotlin.math.roundToInt

fun ByteArray.split(maxSize: Int): Array<ByteArray> {
    val chunkCount = ceil(size.toDouble() / maxSize).roundToInt()
    return Array(chunkCount) { index ->
        val startPosition = index * maxSize
        copyOfRange(startPosition, (startPosition + maxSize).coerceAtMost(size))
    }
}

fun List<ByteArray>.flatten(): ByteArray {
    val newSize = this.sumOf { it.size }
    val byteBuffer = ByteBuffer.allocate(newSize)
    forEach {
        byteBuffer.put(it)
    }

    return byteBuffer.array()
}

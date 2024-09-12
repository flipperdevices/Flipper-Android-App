package com.flipperdevices.core.atomicfile

import okio.Sink
import okio.sink
import java.io.FileOutputStream

actual class SinkWithOutputStream private constructor(
    private val sink: Sink,
    val outputStream: FileOutputStream
) : Sink by sink {
    constructor(outputStream: FileOutputStream) : this(outputStream.sink(), outputStream)
}

package com.flipperdevices.core

import okio.Buffer
import okio.FileSystem
import okio.Path
import okio.Source
import okio.Timeout

fun FileSystem.sourceOrEmpty(path: Path): Source {
    return if (exists(path)) {
        source(path)
    } else {
        EmptySource()
    }
}

private class EmptySource : Source {
    override fun close() = Unit

    override fun read(sink: Buffer, byteCount: Long): Long {
        return -1
    }

    override fun timeout() = Timeout.NONE
}

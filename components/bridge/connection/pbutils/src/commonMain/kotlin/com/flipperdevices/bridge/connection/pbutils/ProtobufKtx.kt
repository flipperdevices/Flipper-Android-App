package com.flipperdevices.bridge.connection.pbutils

import com.flipperdevices.core.ktx.jre.limit
import com.flipperdevices.protobuf.Main
import com.squareup.wire.ProtoAdapter
import com.squareup.wire.ProtoReader
import com.squareup.wire.ProtoWriter
import okio.Buffer
import okio.EOFException
import okio.buffer
import okio.sink
import okio.source
import java.io.InputStream
import java.io.OutputStream

fun Main.writeDelimitedTo(stream: OutputStream) {
    val size = Main.ADAPTER.encodedSize(this)

    val bufferedSink = stream.sink().buffer()
    val writer = ProtoWriter(bufferedSink)
    writer.writeVarint32(size)
    encode(bufferedSink)
    bufferedSink.emit()
}

fun Main.encodeWithDelimitedSize(): ByteArray {
    val size = Main.ADAPTER.encodedSize(this)
    val buffer = Buffer()
    val writer = ProtoWriter(buffer)
    writer.writeVarint32(size)
    encode(buffer)
    return buffer.readByteArray()
}

fun <T> ProtoAdapter<T>.decodeDelimitedPackage(stream: InputStream): T {
    val length = stream.readDelimitedLength()
    return stream.source()
        .limit(length, throwOnByteShortage = true)
        .buffer()
        .use { limitedSource ->
            val limitedReader = ProtoReader(limitedSource)
            decode(limitedReader)
        }
}

private fun InputStream.readDelimitedLength(): Long {
    val buffer = Buffer()
    val readBytes = mutableListOf<Byte>()
    var size = -1
    while (size == -1) {
        readBytes.add(read().toByte())
        buffer.clear()
        buffer.write(readBytes.toByteArray())
        val reader = ProtoReader(buffer)
        try {
            size = reader.readVarint32()
        } catch (@Suppress("SwallowedException") eofException: EOFException) {
            continue
        }
    }

    return size.toLong()
}

package com.flipperdevices.bridge.connection.pbutils

import com.flipperdevices.protobuf.Main
import com.squareup.wire.ProtoWriter
import okio.Buffer
import okio.buffer
import okio.sink
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
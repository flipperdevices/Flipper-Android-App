package com.flipperdevices.bridge.protobuf

import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.warn
import com.flipperdevices.protobuf.MainKt
import com.flipperdevices.protobuf.main
import com.google.protobuf.ByteString
import kotlinx.coroutines.flow.channelFlow
import java.io.InputStream

private const val EOF_CODE = -1

fun LogTagProvider.streamToCommandFlow(
    inputStream: InputStream,
    fileSize: Long? = null,
    requestWrapper: MainKt.Dsl.(data: ByteString) -> Unit
) = channelFlow {
    val bufferArray = ByteArray(ProtobufConstants.MAX_FILE_DATA)
    var alreadyRead = 0L
    var isAllRead = false
    var readSize = inputStream.readOrThrow(bufferArray)

    while (readSize != EOF_CODE && !isAllRead) {
        alreadyRead += readSize
        isAllRead = if (fileSize != null) alreadyRead >= fileSize else false
        send(
            main {
                hasNext = isAllRead.not()
                requestWrapper(ByteString.copyFrom(bufferArray.copyOf(readSize)))
            }
        )
        readSize = inputStream.readOrThrow(bufferArray)
    }
    if (!isAllRead) {
        warn { "Unexpected end of stream. Expect $fileSize bytes, actual $alreadyRead bytes" }
        send(
            main {
                hasNext = false
                requestWrapper(ByteString.EMPTY)
            }
        )
    }
    close()
}

private fun InputStream.readOrThrow(buffer: ByteArray): Int {
    return runCatching {
        read(buffer)
    }.getOrThrow()
}

package com.google.protobuf

import java.nio.ByteBuffer

class BinaryReaderWrapper private constructor(binaryReader: BinaryReader) : Reader by binaryReader {

    companion object {
        /**
         * Read more in {@link BinaryReader#newInstance}
         */
        fun newInstance(
            buffer: ByteBuffer,
            bufferIsImmutable: Boolean = true
        ): BinaryReaderWrapper {
            return BinaryReaderWrapper(BinaryReader.newInstance(buffer, bufferIsImmutable))
        }
    }
}

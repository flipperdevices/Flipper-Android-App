package com.flipperdevices.bridge.dao.impl.converters

import com.flipperdevices.bridge.dao.impl.md5.MD5Converter
import java.io.InputStream

class LambdaMD5Converter(
    private val block: suspend (istream: InputStream) -> String
) : MD5Converter {
    override suspend fun convert(istream: InputStream): String {
        return block.invoke(istream)
    }
}

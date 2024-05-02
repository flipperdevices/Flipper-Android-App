package com.flipperdevices.bridge.dao.impl.md5

import com.flipperdevices.core.ktx.jre.md5
import java.io.InputStream

internal class MD5ConverterImpl : MD5Converter {
    override suspend fun convert(istream: InputStream): String {
        return istream.md5()
    }
}

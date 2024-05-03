package com.flipperdevices.bridge.dao.impl.converters

import com.flipperdevices.bridge.dao.impl.md5.MD5Converter
import java.io.InputStream

internal object StubMD5Converter : MD5Converter {
    override suspend fun convert(istream: InputStream): String {
        return "STUB_MD5"
    }
}

package com.flipperdevices.bridge.dao.impl.converters

import com.flipperdevices.core.ktx.jre.md5
import java.io.InputStream

interface MD5Converter {
    suspend fun convert(istream: InputStream): String

    object Default : MD5Converter {
        override suspend fun convert(istream: InputStream): String {
            return istream.md5()
        }
    }
}

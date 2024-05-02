package com.flipperdevices.bridge.dao.impl.md5

import java.io.InputStream

interface MD5Converter {
    /**
     * Converts [istream] to md5 string
     */
    suspend fun convert(istream: InputStream): String
}

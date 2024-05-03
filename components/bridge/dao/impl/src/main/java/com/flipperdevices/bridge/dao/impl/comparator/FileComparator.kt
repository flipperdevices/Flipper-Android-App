package com.flipperdevices.bridge.dao.impl.comparator

import java.io.InputStream

interface FileComparator {

    /**
     * Check if two streams have identical content
     */
    suspend fun isSameContent(istream1: InputStream, istream2: InputStream): Boolean
}

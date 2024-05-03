package com.flipperdevices.bridge.dao.impl.md5

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ktx.jre.md5
import com.squareup.anvil.annotations.ContributesBinding
import java.io.InputStream
import javax.inject.Inject

@ContributesBinding(AppGraph::class, MD5Converter::class)
class MD5ConverterImpl @Inject constructor() : MD5Converter {
    override suspend fun convert(istream: InputStream): String {
        return istream.md5()
    }
}

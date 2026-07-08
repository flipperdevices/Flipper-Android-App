package com.flipperdevices.bridge.dao.impl.md5

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ktx.jre.md5
import dev.zacsweers.metro.ContributesBinding
import java.io.InputStream
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding

@ContributesBinding(AppGraph::class, binding<MD5Converter>())
class MD5ConverterImpl @Inject constructor() : MD5Converter {
    override suspend fun convert(istream: InputStream): String {
        return istream.md5()
    }
}

package com.flipperdevices.bridge.dao.impl.comparator

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ktx.jre.FlipperDispatchers
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.withContext
import java.io.InputStream
import javax.inject.Inject

@ContributesBinding(AppGraph::class, FileComparator::class)
class DefaultFileComparator @Inject constructor() : FileComparator {
    override suspend fun isSameContent(
        istream1: InputStream,
        istream2: InputStream
    ): Boolean = withContext(FlipperDispatchers.workStealingDispatcher) {
        if (istream1.available() == 0 && istream2.available() == 0) return@withContext true
        if (istream1.available() == 0 || istream2.available() == 0) return@withContext false
        if (istream1.available() != istream2.available()) {
            return@withContext false
        }
        istream1.bufferedReader().use { br1 ->
            istream2.bufferedReader().use { br2 ->
                do {
                    val ch1 = br1.read()
                    val ch2 = br2.read()
                    if (ch1 != ch2) return@withContext false
                } while (ch1 != -1)
            }
        }
        return@withContext true
    }
}

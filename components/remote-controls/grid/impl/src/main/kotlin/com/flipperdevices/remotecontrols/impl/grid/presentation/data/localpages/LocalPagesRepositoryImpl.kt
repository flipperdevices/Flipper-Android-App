package com.flipperdevices.remotecontrols.impl.grid.presentation.data.localpages

import com.flipperdevices.bridge.dao.api.delegates.key.SimpleKeyApi
import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.ifrmvp.model.PagesLayout
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class, LocalPagesRepository::class)
class LocalPagesRepositoryImpl @Inject constructor(
    private val simpleKeyApi: SimpleKeyApi,
) : LocalPagesRepository {

    override suspend fun getLocalFlipperKey(path: FlipperFilePath): FlipperKey? {
        return simpleKeyApi.getKey(
            FlipperKeyPath(
                path = path,
                deleted = false
            )
        )
    }

    override suspend fun getLocalPagesLayout(
        path: FlipperFilePath,
        toPagesLayout: (String) -> PagesLayout?
    ): PagesLayout? {
        val flipperKey = getLocalFlipperKey(path)
        return flipperKey
            .let { it?.additionalFiles.orEmpty() }
            .let { additionalFiles ->
                additionalFiles.firstNotNullOfOrNull { fFile ->
                    val text = fFile.content.openStream().reader().readText()
                    toPagesLayout.invoke(text)
                }
            }
    }
}

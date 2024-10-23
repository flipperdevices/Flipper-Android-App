package com.flipperdevices.keyedit

import com.flipperdevices.bridge.dao.api.model.FlipperFile
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.keyedit.api.NotSavedFlipperFile
import com.flipperdevices.keyedit.api.NotSavedFlipperKeyApi
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class, NotSavedFlipperKeyApi::class)
class NotSavedFlipperKeyNoop @Inject constructor() : NotSavedFlipperKeyApi {
    override suspend fun toNotSavedFlipperFile(flipperFile: FlipperFile): NotSavedFlipperFile {
        throw NotImplementedError()
    }
}

package com.flipperdevices.keyedit

import com.flipperdevices.bridge.dao.api.model.FlipperFile
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.keyedit.api.NotSavedFlipperFile
import com.flipperdevices.keyedit.api.NotSavedFlipperKeyApi
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding

@ContributesBinding(AppGraph::class, binding<NotSavedFlipperKeyApi>())
class NotSavedFlipperKeyNoop @Inject constructor() : NotSavedFlipperKeyApi {
    override suspend fun toNotSavedFlipperFile(flipperFile: FlipperFile): NotSavedFlipperFile {
        throw NotImplementedError()
    }
}

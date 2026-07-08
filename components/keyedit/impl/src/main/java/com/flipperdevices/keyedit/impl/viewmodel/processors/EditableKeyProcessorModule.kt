package com.flipperdevices.keyedit.impl.viewmodel.processors

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.keyedit.impl.model.EditableKey
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Binds

@ContributesTo(AppGraph::class)
interface EditableKeyProcessorModule {
    @Binds
    fun provideExistedKeyProcessor(
        existedKeyProcessor: ExistedKeyProcessor
    ): EditableKeyProcessor<EditableKey.Existed>

    @Binds
    fun provideLimbKeyProcessor(
        limboKeyProcessor: LimboKeyProcessor
    ): EditableKeyProcessor<EditableKey.Limb>
}

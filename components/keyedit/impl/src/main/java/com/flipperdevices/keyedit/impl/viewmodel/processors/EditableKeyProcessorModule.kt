package com.flipperdevices.keyedit.impl.viewmodel.processors

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.keyedit.impl.model.EditableKey
import com.squareup.anvil.annotations.ContributesTo
import dagger.Binds
import dagger.Module

@Module
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
package com.flipperdevices.remotecontrols.api

import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.ui.decompose.ScreenDecomposeComponent

abstract class SetupScreenDecomposeComponent(
    componentContext: ComponentContext
) : ScreenDecomposeComponent(componentContext) {

    interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            param: Param,
            onBack: () -> Unit,
            onIrFileReady: (id: Long, name: String) -> Unit
        ): SetupScreenDecomposeComponent
    }

    class Param(
        val brandId: Long,
        val categoryId: Long,
        val brandName: String,
        val categoryName: String
    ) {
        val remoteName: String
            get() = "${brandName}_$categoryName"
                .replace(" ", "_")
                .replace("\\", "_")
                .replace("/", "_")
                .replace(".", "_")
                .take(MAX_SIZE_REMOTE_LENGTH)
    }

    companion object {
        private const val MAX_SIZE_REMOTE_LENGTH = 21
    }
}

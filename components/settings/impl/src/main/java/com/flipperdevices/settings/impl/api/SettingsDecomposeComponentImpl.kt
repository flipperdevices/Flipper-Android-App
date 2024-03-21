package com.flipperdevices.settings.impl.api

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.value.Value
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.debug.api.StressTestDecomposeComponent
import com.flipperdevices.filemanager.api.navigation.FileManagerDecomposeComponent
import com.flipperdevices.settings.api.SettingsDecomposeComponent
import com.flipperdevices.settings.impl.model.SettingsNavigationConfig
import com.flipperdevices.shake2report.api.Shake2ReportDecomposeComponent
import com.flipperdevices.ui.decompose.DecomposeComponent
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import com.flipperdevices.ui.decompose.popOr
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import me.gulya.anvil.assisted.ContributesAssistedFactory

@ContributesAssistedFactory(AppGraph::class, SettingsDecomposeComponent.Factory::class)
class SettingsDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted private val onBack: DecomposeOnBackParameter,
    private val fileManagerComponentFactory: FileManagerDecomposeComponent.Factory,
    private val shake2ReportComponentFactory: Shake2ReportDecomposeComponent.Factory,
    private val mainComponentFactory: MainScreenDecomposeComponent.Factory,
    private val stressTestFactory: StressTestDecomposeComponent.Factory
) : SettingsDecomposeComponent<SettingsNavigationConfig>(), ComponentContext by componentContext {

    override val stack: Value<ChildStack<SettingsNavigationConfig, DecomposeComponent>> =
        childStack(
            source = navigation,
            serializer = SettingsNavigationConfig.serializer(),
            initialConfiguration = SettingsNavigationConfig.Main,
            handleBackButton = true,
            childFactory = ::child,
        )

    private fun child(
        config: SettingsNavigationConfig,
        componentContext: ComponentContext
    ): DecomposeComponent = when (config) {
        SettingsNavigationConfig.FileManager -> fileManagerComponentFactory(
            componentContext = componentContext,
            onBack = { navigation.popOr(onBack::invoke) }
        )

        SettingsNavigationConfig.Main -> mainComponentFactory(
            componentContext,
            navigation,
            onBack = { navigation.popOr(onBack::invoke) }
        )

        SettingsNavigationConfig.Shake2Report -> shake2ReportComponentFactory(
            componentContext,
            onBack = { navigation.popOr(onBack::invoke) }
        )

        SettingsNavigationConfig.StressTest -> stressTestFactory(componentContext)
    }
}

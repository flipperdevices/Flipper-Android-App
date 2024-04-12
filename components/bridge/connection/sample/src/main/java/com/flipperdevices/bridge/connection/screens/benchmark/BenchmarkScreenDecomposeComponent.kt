package com.flipperdevices.bridge.connection.screens.benchmark

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.core.ui.lifecycle.viewModelWithFactoryWithoutRemember
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.ui.decompose.ScreenDecomposeComponent
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class BenchmarkScreenDecomposeComponent @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted private val address: String,
    private val benchmarkViewModelFactory: BenchmarkViewModel.Factory
) : ScreenDecomposeComponent(componentContext) {
    private val benchmarkViewModel = viewModelWithFactoryWithoutRemember(address) {
        benchmarkViewModelFactory(address)
    }

    @Composable
    override fun Render() {
        Column {
            Text(
                modifier = Modifier.padding(16.dp),
                text = address,
                style = LocalTypography.current.titleB24
            )
            val state by benchmarkViewModel.getState().collectAsState()

            Text(text = "Current status is: $state")
        }
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            @Assisted componentContext: ComponentContext,
            @Assisted address: String
        ): BenchmarkScreenDecomposeComponent
    }
}

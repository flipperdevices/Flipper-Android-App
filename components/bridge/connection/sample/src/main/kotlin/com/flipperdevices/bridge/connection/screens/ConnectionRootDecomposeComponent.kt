package com.flipperdevices.bridge.connection.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.value.Value
import com.flipperdevices.bridge.connection.screens.benchmark.BenchmarkScreenDecomposeComponent
import com.flipperdevices.bridge.connection.screens.models.ConnectionRootConfig
import com.flipperdevices.bridge.connection.screens.nopermission.ConnectionNoPermissionDecomposeComponent
import com.flipperdevices.bridge.connection.screens.search.ConnectionSearchDecomposeComponent
import com.flipperdevices.ui.decompose.CompositeDecomposeComponent
import com.flipperdevices.ui.decompose.DecomposeComponent
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class ConnectionRootDecomposeComponent @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    private val context: Context,
    private val searchDecomposeFactory: ConnectionSearchDecomposeComponent.Factory,
    private val benchmarkScreenDecomposeComponentFactory: BenchmarkScreenDecomposeComponent.Factory
) : CompositeDecomposeComponent<ConnectionRootConfig>(), ComponentContext by componentContext {
    override val stack: Value<ChildStack<ConnectionRootConfig, DecomposeComponent>> = childStack(
        source = navigation,
        serializer = ConnectionRootConfig.serializer(),
        initialConfiguration = if (isPermissionGranted()) {
            ConnectionRootConfig.Main
        } else {
            ConnectionRootConfig.NoPermission
        },
        childFactory = ::child,
        handleBackButton = true
    )

    private fun child(
        config: ConnectionRootConfig,
        componentContext: ComponentContext
    ): DecomposeComponent = when (config) {
        is ConnectionRootConfig.Main -> searchDecomposeFactory(
            componentContext = componentContext,
            onItemSelect = { navigation.push(ConnectionRootConfig.Benchmark(it)) }
        )

        is ConnectionRootConfig.NoPermission ->
            ConnectionNoPermissionDecomposeComponent(componentContext)

        is ConnectionRootConfig.Benchmark ->
            benchmarkScreenDecomposeComponentFactory(
                componentContext = componentContext,
                address = config.address
            )
    }

    private fun isPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.BLUETOOTH_SCAN
        ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.BLUETOOTH_CONNECT
        ) == PackageManager.PERMISSION_GRANTED
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext
        ): ConnectionRootDecomposeComponent
    }
}

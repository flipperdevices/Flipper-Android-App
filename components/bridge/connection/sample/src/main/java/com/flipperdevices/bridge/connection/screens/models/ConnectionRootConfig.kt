package com.flipperdevices.bridge.connection.screens.models

import com.flipperdevices.bridge.connection.screens.benchmark.BenchmarkScreenDecomposeComponent
import kotlinx.serialization.Serializable

@Serializable
sealed class ConnectionRootConfig {
    @Serializable
    data object Main : ConnectionRootConfig()

    @Serializable
    data object NoPermission : ConnectionRootConfig()

    @Serializable
    data class Benchmark(val address: String) : ConnectionRootConfig()
}

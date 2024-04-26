package com.flipperdevices.app.baselineprofile.benchmark

import androidx.benchmark.macro.BaselineProfileMode
import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.flipperdevices.app.baselineprofile.scope.FirstLaunchIterationScope
import com.flipperdevices.app.baselineprofile.util.UiAutomatorExt
import com.flipperdevices.app.baselineprofile.util.UiAutomatorExt.clearCache
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * THIS WILL WIPE YOUR FLIPPER ANDROID INSTALL
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class StartupBenchmarks {

    @get:Rule
    val rule = MacrobenchmarkRule()

    @Test
    fun startupCompilationNone() {
        benchmark(CompilationMode.None())
    }

    @Test
    fun startupCompilationBaselineProfiles() {
        benchmark(CompilationMode.Partial(BaselineProfileMode.Require))
    }

    private fun benchmark(compilationMode: CompilationMode) {
        rule.measureRepeated(
            packageName = UiAutomatorExt.requirePackageName,
            metrics = listOf(StartupTimingMetric(), FrameTimingMetric()),
            compilationMode = compilationMode,
            startupMode = StartupMode.COLD,
            iterations = 6,
            setupBlock = {
                device.clearCache()
                pressHome()
            },
            measureBlock = {
                startActivityAndWait()
                with(FirstLaunchIterationScope) { tryRun() }
            }
        )
    }
}

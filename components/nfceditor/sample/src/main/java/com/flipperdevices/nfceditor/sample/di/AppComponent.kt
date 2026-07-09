package com.flipperdevices.nfceditor.sample.di

import android.app.Application
import android.content.Context
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.di.ApplicationParams
import com.flipperdevices.nfceditor.sample.NfcEditorActivity
import com.flipperdevices.shake2report.api.Shake2ReportApi
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.Provider
import dev.zacsweers.metro.SingleIn

@SingleIn(AppGraph::class)
@DependencyGraph(AppGraph::class)
interface AppComponent {
    val shake2report: Provider<Shake2ReportApi>
    fun inject(activity: NfcEditorActivity)

    @DependencyGraph.Factory
    fun interface Factory {
        fun create(
            @Provides context: Context,
            @Provides application: Application,
            @Provides applicationParams: ApplicationParams
        ): AppComponent
    }
}

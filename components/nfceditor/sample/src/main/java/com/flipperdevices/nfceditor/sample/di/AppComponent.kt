package com.flipperdevices.nfceditor.sample.di

import android.app.Application
import android.content.Context
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.di.ApplicationParams
import com.flipperdevices.nfceditor.sample.NfcEditorActivity
import com.flipperdevices.shake2report.api.Shake2ReportApi
import com.squareup.anvil.annotations.MergeComponent
import dagger.BindsInstance
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
@MergeComponent(AppGraph::class)
interface AppComponent {
    val shake2report: Provider<Shake2ReportApi>
    fun inject(activity: NfcEditorActivity)

    @MergeComponent.Factory
    interface Factory {
        fun create(
            @BindsInstance context: Context,
            @BindsInstance application: Application,
            @BindsInstance applicationParams: ApplicationParams
        ): AppComponent
    }
}

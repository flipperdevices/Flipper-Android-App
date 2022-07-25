package com.flipperdevices.nfceditor.sample.di

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.nfceditor.sample.NfcEditorActivity
import com.flipperdevices.shake2report.api.Shake2ReportApi
import com.squareup.anvil.annotations.ContributesTo
import javax.inject.Provider

@ContributesTo(AppGraph::class)
interface NfcEditorComponent {
    val shake2report: Provider<Shake2ReportApi>

    fun inject(activity: NfcEditorActivity)
}

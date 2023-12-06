package com.flipperdevices.faphub.report.api

import com.flipperdevices.core.ui.navigation.AggregateFeatureEntry
import com.flipperdevices.core.ui.navigation.FeatureScreenRootRoute

interface FapReportFeatureEntry : AggregateFeatureEntry {
    override val ROUTE: FeatureScreenRootRoute
        get() = FeatureScreenRootRoute.FAP_HUB_REPORT

    fun start(applicationUid: String, reportUrl: String): String
}

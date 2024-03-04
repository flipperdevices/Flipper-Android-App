package com.flipperdevices.faphub.report.impl.viewmodel

import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.flipperdevices.faphub.dao.api.FapReportApi
import com.flipperdevices.faphub.report.impl.model.FapReportState
import com.flipperdevices.inappnotification.api.InAppNotificationStorage
import com.flipperdevices.inappnotification.api.model.InAppNotification
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ReportViewModel @AssistedInject constructor(
    @Assisted private val applicationUid: String,
    private val reportApi: FapReportApi,
    private val inAppNotificationStorage: InAppNotificationStorage
) : DecomposeViewModel(), LogTagProvider {
    override val TAG = "ReportViewModel"

    private val state = MutableStateFlow<FapReportState>(FapReportState.ReadyToReport)

    fun getFapReportState() = state.asStateFlow()

    fun submit(onBack: () -> Unit, text: String) {
        if (!state.compareAndSet(FapReportState.ReadyToReport, FapReportState.Uploading)) {
            return
        }
        viewModelScope.launch {
            try {
                reportApi.report(applicationUid, text)
                inAppNotificationStorage.addNotification(InAppNotification.ReportApp)
            } catch (e: Exception) {
                error(e) { "Failed report bug" }
            }
            withContext(Dispatchers.Main) {
                onBack()
            }
        }
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            applicationUid: String
        ): ReportViewModel
    }
}

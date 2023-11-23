package com.flipperdevices.faphub.fapscreen.impl.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.flipperdevices.bridge.dao.api.FapHubHideItemApi
import com.flipperdevices.core.ktx.jre.launchWithLock
import com.flipperdevices.core.ktx.jre.runBlockingWithLog
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.warn
import com.flipperdevices.faphub.dao.api.FapNetworkApi
import com.flipperdevices.faphub.dao.api.model.FapItem
import com.flipperdevices.faphub.fapscreen.impl.api.FAP_ID_KEY
import com.flipperdevices.faphub.fapscreen.impl.model.FapDetailedControlState
import com.flipperdevices.faphub.fapscreen.impl.model.FapScreenLoadingState
import com.flipperdevices.faphub.installation.stateprovider.api.api.FapInstallationStateManager
import com.flipperdevices.faphub.installation.stateprovider.api.model.FapState
import com.flipperdevices.faphub.report.api.FapReportFeatureEntry
import com.flipperdevices.faphub.target.api.FlipperTargetProviderApi
import com.flipperdevices.inappnotification.api.InAppNotificationStorage
import com.flipperdevices.inappnotification.api.model.InAppNotification
import com.flipperdevices.metric.api.MetricApi
import com.flipperdevices.metric.api.events.SimpleEvent
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.withContext
import tangle.inject.TangleParam
import tangle.viewmodel.VMInject

@Suppress("LongParameterList")
class FapScreenViewModel @VMInject constructor(
    @TangleParam(FAP_ID_KEY)
    private val fapUniversalId: String,
    private val fapNetworkApi: FapNetworkApi,
    private val stateManager: FapInstallationStateManager,
    private val targetProviderApi: FlipperTargetProviderApi,
    private val fapReportFeatureEntry: FapReportFeatureEntry,
    private val fapHubHideApi: FapHubHideItemApi,
    private val inAppNotificationStorage: InAppNotificationStorage,
    private val metricApi: MetricApi
) : ViewModel(), LogTagProvider {
    override val TAG = "FapScreenViewModel"

    private val fapScreenLoadingStateFlow = MutableStateFlow<FapScreenLoadingState>(
        FapScreenLoadingState.Loading
    )

    private val controlStateFlow = MutableStateFlow<FapDetailedControlState>(
        FapDetailedControlState.Loading
    )

    private val mutex = Mutex()
    private var controlStateJob: Job? = null
    private var downloadFapJob: Job? = null

    init {
        onRefresh()
    }

    fun getLoadingState() = fapScreenLoadingStateFlow.asStateFlow()

    fun getControlState() = controlStateFlow.asStateFlow()

    fun onOpenReportApp(navController: NavController) {
        val loadingState = fapScreenLoadingStateFlow.value as? FapScreenLoadingState.Loaded
        if (loadingState == null) {
            warn { "#onOpenReportApp calls when fapScreenLoadingStateFlow is null or not loaded" }
            return
        }
        navController.navigate(fapReportFeatureEntry.start(loadingState.fapItem.id))
    }

    fun onPressHide(isHidden: Boolean, navController: NavController) {
        val loadingState = fapScreenLoadingStateFlow.value as? FapScreenLoadingState.Loaded
        if (loadingState == null) {
            warn { "#onPressHide calls when fapScreenLoadingStateFlow is null or not loaded" }
            return
        }
        metricApi.reportSimpleEvent(
            SimpleEvent.HIDE_FAPHUB_APP,
            loadingState.fapItem.applicationAlias
        )
        viewModelScope.launch(Dispatchers.Default) {
            if (isHidden) {
                fapHubHideApi.unHideItem(loadingState.fapItem.id)
            } else {
                fapHubHideApi.hideItem(loadingState.fapItem.id)
                inAppNotificationStorage.addNotification(
                    InAppNotification.HiddenApp(
                        action = { runBlockingWithLog { fapHubHideApi.unHideItem(loadingState.fapItem.id) } }
                    )
                )
                withContext(Dispatchers.Main) {
                    navController.popBackStack()
                }
            }
            fapScreenLoadingStateFlow.update {
                if (it is FapScreenLoadingState.Loaded) {
                    it.copy(
                        isHidden = isHidden.not()
                    )
                } else {
                    it
                }
            }
        }
    }

    fun onRefresh() = launchWithLock(mutex, viewModelScope, "refresh") {
        downloadFapJob?.cancelAndJoin()
        downloadFapJob = viewModelScope.launch(Dispatchers.Default) {
            fapScreenLoadingStateFlow.emit(FapScreenLoadingState.Loading)
            controlStateFlow.emit(FapDetailedControlState.Loading)
            targetProviderApi.getFlipperTarget().collectLatest { target ->
                if (target == null) {
                    fapScreenLoadingStateFlow.emit(FapScreenLoadingState.Loading)
                    controlStateFlow.emit(FapDetailedControlState.Loading)
                    controlStateJob?.cancelAndJoin()
                    return@collectLatest
                }
                fapNetworkApi.getFapItemById(target, fapUniversalId).onSuccess { fapItem ->
                    fapScreenLoadingStateFlow.emit(
                        FapScreenLoadingState.Loaded(
                            fapItem = fapItem,
                            shareUrl = generateUrl(fapItem.applicationAlias),
                            isHidden = fapHubHideApi.isHidden(fapItem.id)
                        )
                    )
                    controlStateJob?.cancelAndJoin()
                    controlStateJob = stateManager.getFapStateFlow(
                        applicationUid = fapItem.id,
                        currentVersion = fapItem.upToDateVersion
                    ).onEach { state ->
                        controlStateFlow.emit(state.toControlState(fapItem))
                    }.launchIn(viewModelScope + Dispatchers.Default)
                }.onFailure {
                    error(it) { "Failed fetch single application" }
                    if (it !is CancellationException) {
                        fapScreenLoadingStateFlow.emit(FapScreenLoadingState.Error(it))
                    }
                }
            }
        }
    }

    private fun FapState.toControlState(fapItem: FapItem): FapDetailedControlState = when (this) {
        FapState.Canceling,
        FapState.Deleting,
        FapState.RetrievingManifest,
        FapState.NotInitialized -> FapDetailedControlState.Loading

        is FapState.InstallationInProgress,
        is FapState.UpdatingInProgress,
        is FapState.NotAvailableForInstall,
        FapState.ReadyToInstall -> FapDetailedControlState.InProgressOrNotInstalled(
            fapItem
        )

        is FapState.Installed,
        is FapState.ReadyToUpdate -> FapDetailedControlState.Installed(
            fapItem
        )
    }

    private fun generateUrl(alias: String) = "https://lab.flipper.net/apps/$alias"
}

package com.flipperdevices.faphub.fapscreen.impl.viewmodel

import androidx.lifecycle.viewModelScope
import com.flipperdevices.bridge.dao.api.FapHubHideItemApi
import com.flipperdevices.core.ktx.jre.launchWithLock
import com.flipperdevices.core.ktx.jre.runBlockingWithLog
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.warn
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.flipperdevices.faphub.dao.api.FapNetworkApi
import com.flipperdevices.faphub.dao.api.model.FapItem
import com.flipperdevices.faphub.fapscreen.impl.model.FapDetailedControlState
import com.flipperdevices.faphub.fapscreen.impl.model.FapScreenLoadingState
import com.flipperdevices.faphub.fapscreen.impl.model.FapScreenNavigationConfig
import com.flipperdevices.faphub.installation.stateprovider.api.api.FapInstallationStateManager
import com.flipperdevices.faphub.installation.stateprovider.api.model.FapState
import com.flipperdevices.faphub.report.api.FapReportArgument
import com.flipperdevices.faphub.target.api.FlipperTargetProviderApi
import com.flipperdevices.inappnotification.api.InAppNotificationStorage
import com.flipperdevices.inappnotification.api.model.InAppNotification
import com.flipperdevices.metric.api.MetricApi
import com.flipperdevices.metric.api.events.SimpleEvent
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
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

@Suppress("LongParameterList")
class FapScreenViewModel @AssistedInject constructor(
    @Assisted private val fapUniversalId: String,
    private val fapNetworkApi: FapNetworkApi,
    private val stateManager: FapInstallationStateManager,
    private val targetProviderApi: FlipperTargetProviderApi,
    private val fapHubHideApi: FapHubHideItemApi,
    private val inAppNotificationStorage: InAppNotificationStorage,
    private val metricApi: MetricApi
) : DecomposeViewModel(), LogTagProvider {
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

    fun getReportAppNavigationConfig(loadedState: FapScreenLoadingState.Loaded): FapScreenNavigationConfig.FapReport {
        return FapScreenNavigationConfig.FapReport(
            FapReportArgument(
                applicationUid = loadedState.fapItem.id,
                reportUrl = loadedState.fapItem.fapDeveloperInformation.githubRepositoryLink
            )
        )
    }

    fun onPressHide(isHidden: Boolean, onBack: () -> Unit) {
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
                    onBack()
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

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            fapUniversalId: String
        ): FapScreenViewModel
    }
}

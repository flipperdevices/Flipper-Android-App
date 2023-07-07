package com.flipperdevices.bottombar.impl.viewmodel

import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import com.flipperdevices.bottombar.impl.model.FlipperBottomTab
import com.flipperdevices.core.ktx.jre.runBlockingWithLog
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.core.preference.pb.SelectedTab
import com.flipperdevices.core.preference.pb.Settings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import tangle.viewmodel.VMInject

class BottomNavigationViewModel @VMInject constructor(
    private val settingsDataStore: DataStore<Settings>
) : ViewModel(), LogTagProvider {
    override val TAG = "BottomNavigationViewModel"

    private val selectedTabInternal = MutableStateFlow(FlipperBottomTab.DEVICE)

    val selectedTab: StateFlow<FlipperBottomTab>
        get() = selectedTabInternal

    fun getStartDestination(): FlipperBottomTab {
        return runBlockingWithLog("selected_tab") {
            return@runBlockingWithLog when (settingsDataStore.data.first().selectedTab) {
                null,
                SelectedTab.UNRECOGNIZED,
                SelectedTab.DEVICE -> FlipperBottomTab.DEVICE
                SelectedTab.ARCHIVE -> FlipperBottomTab.ARCHIVE
                SelectedTab.HUB -> FlipperBottomTab.HUB
            }
        }
    }

    fun invalidateSelectedTab(currentDestination: NavDestination?) {
        val currentTab = FlipperBottomTab.values()
            .find { tab ->
                currentDestination.isTopLevelDestinationInHierarchy(tab)
            }
        if (currentTab == null) {
            info { "Can't find current tab for $currentDestination" }
            return
        }

        viewModelScope.launch(Dispatchers.Default) {
            selectedTabInternal.emit(currentTab)
            settingsDataStore.updateData {
                it.toBuilder()
                    .setSelectedTab(
                        when (currentTab) {
                            FlipperBottomTab.DEVICE -> SelectedTab.DEVICE
                            FlipperBottomTab.ARCHIVE -> SelectedTab.ARCHIVE
                            FlipperBottomTab.HUB -> SelectedTab.HUB
                        }
                    ).build()
            }
        }
    }
}

/**
 * Copy from https://github.com/android/nowinandroid/blob/e63394248b23f2a138f6ed333e5711b898d24a40/app/src/main/java/com/google/samples/apps/nowinandroid/ui/NiaApp.kt#L270
 */
private fun NavDestination?.isTopLevelDestinationInHierarchy(destination: FlipperBottomTab) =
    this?.hierarchy?.any {
        it.route?.contains(destination.name, true) ?: false
    } ?: false

package com.flipperdevices.settings.impl.viewmodels

import android.app.Application
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.flipperdevices.bridge.synchronization.api.SynchronizationApi
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.navigation.global.CiceroneGlobal
import com.flipperdevices.core.preference.pb.Settings
import com.flipperdevices.debug.api.StressTestApi
import com.flipperdevices.firstpair.api.FirstPairApi
import com.flipperdevices.settings.impl.R
import com.flipperdevices.settings.impl.di.SettingsComponent
import com.github.terrakok.cicerone.Router
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DebugViewModel(application: Application) : AndroidViewModel(application) {
    @Inject
    lateinit var stressTestApi: StressTestApi

    @Inject
    lateinit var cicerone: CiceroneGlobal

    @Inject
    lateinit var firstPairApi: FirstPairApi

    @Inject
    lateinit var synchronizationApi: SynchronizationApi

    @Inject
    lateinit var settingsDataStore: DataStore<Settings>

    init {
        ComponentHolder.component<SettingsComponent>().inject(this)
    }

    fun onOpenStressTest(router: Router) {
        router.navigateTo(stressTestApi.getStressTestScreen())
    }

    fun onStartSynchronization() {
        synchronizationApi.startSynchronization(force = true)
    }

    fun onOpenConnectionScreen() {
        cicerone.getRouter().navigateTo(firstPairApi.getFirstPairScreen())
    }

    fun getIgnoredSupportedVersionState() = settingsDataStore.data.map {
        it.ignoreUnsupportedVersion
    }.stateIn(viewModelScope, SharingStarted.Lazily, false)

    fun onSwitchIgnoreSupportedVersion(ignored: Boolean) {
        viewModelScope.launch {
            settingsDataStore.updateData {
                it.toBuilder()
                    .setIgnoreUnsupportedVersion(ignored)
                    .build()
            }

            withContext(Dispatchers.Main) {
                val context = getApplication<Application>()
                Toast.makeText(
                    context,
                    R.string.debug_ignored_unsupported_version_toast,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}

package com.flipperdevices.debug.impl.viewmodel

import androidx.lifecycle.viewModelScope
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.ui.LifecycleViewModel
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class BruteforceViewModel : LifecycleViewModel() {
    @Inject
    lateinit var serviceProvider: FlipperServiceProvider

    private val isBruteforceRunning = AtomicBoolean(false)
    private val debugLog = MutableStateFlow(mutableListOf<String>())

    fun startBruteforce() {
        serviceProvider.provideServiceApi(this) { serviceApi ->
            viewModelScope.launch {
                if (!isBruteforceRunning.compareAndSet(false, true)) {
                    return@launch
                }
                while (isBruteforceRunning.get()) {
                }
            }
        }
    }

    fun stopBruteforce() {
        isBruteforceRunning.set(false)
    }
}

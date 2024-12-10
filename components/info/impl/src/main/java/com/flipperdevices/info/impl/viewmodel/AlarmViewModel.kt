package com.flipperdevices.info.impl.viewmodel

import com.flipperdevices.bridge.connection.feature.alarm.api.FAlarmFeatureApi
import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureProvider
import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureStatus
import com.flipperdevices.bridge.connection.feature.provider.api.get
import com.flipperdevices.bridge.connection.feature.provider.api.getSync
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

class AlarmViewModel @Inject constructor(
    private val featureProvider: FFeatureProvider
) : DecomposeViewModel() {
    val hasAlarm = featureProvider.get<FAlarmFeatureApi>()
        .map { status -> status is FFeatureStatus.Supported<FAlarmFeatureApi> }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    fun alarmOnFlipper() {
        viewModelScope.launch {
            featureProvider.getSync<FAlarmFeatureApi>()?.makeSound()
        }
    }
}

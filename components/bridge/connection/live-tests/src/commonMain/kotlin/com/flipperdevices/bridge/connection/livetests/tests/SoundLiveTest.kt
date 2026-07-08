package com.flipperdevices.bridge.connection.livetests.tests

import com.flipperdevices.bridge.connection.feature.alarm.api.FAlarmFeatureApi
import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureProvider
import com.flipperdevices.bridge.connection.feature.provider.api.getSync
import com.flipperdevices.bridge.connection.livetests.api.LiveTest
import com.flipperdevices.bridge.connection.livetests.model.FeatureNotSupportedError
import javax.inject.Inject

class SoundLiveTest @Inject constructor(
    private val featureProvider: FFeatureProvider
) : LiveTest {
    override val name = "Sound"
    override val description = "Ask the Flipper to play an audiovisual alert"

    override suspend fun execute(): Result<String> {
        val alarmApi = featureProvider.getSync<FAlarmFeatureApi>()
            ?: return Result.failure(FeatureNotSupportedError("ALARM"))
        return runCatching {
            alarmApi.makeSound()
            "Alert command sent, the Flipper should have made a sound"
        }
    }
}

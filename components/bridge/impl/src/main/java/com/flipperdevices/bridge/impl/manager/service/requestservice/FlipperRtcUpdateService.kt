package com.flipperdevices.bridge.impl.manager.service.requestservice

import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.api.model.FlipperRequestPriority
import com.flipperdevices.bridge.api.model.wrapToRequest
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.protobuf.main
import com.flipperdevices.protobuf.system.System
import com.flipperdevices.protobuf.system.dateTime
import com.flipperdevices.protobuf.system.setDateTimeRequest
import java.util.Calendar

class FlipperRtcUpdateService : LogTagProvider {
    override val TAG = "FlipperRtcUpdateService"

    suspend fun initialize(requestApi: FlipperRequestApi) {
        requestApi.requestWithoutAnswer(
            main {
                systemSetDatetimeRequest = setDateTimeRequest {
                    datetime = getCurrentDateTime()
                }
            }.wrapToRequest(FlipperRequestPriority.RIGHT_NOW)
        )
    }

    @Suppress("MagicNumber")
    private fun getCurrentDateTime(): System.DateTime {
        val rightNow = Calendar.getInstance()
        return dateTime {
            hour = rightNow.get(Calendar.HOUR_OF_DAY)
            minute = rightNow.get(Calendar.MINUTE)
            second = rightNow.get(Calendar.SECOND)
            day = rightNow.get(Calendar.DAY_OF_MONTH)
            month = rightNow.get(Calendar.MONTH) + 1
            weekday = when (rightNow.get(Calendar.DAY_OF_WEEK)) {
                Calendar.MONDAY -> 1
                Calendar.TUESDAY -> 2
                Calendar.WEDNESDAY -> 3
                Calendar.THURSDAY -> 4
                Calendar.FRIDAY -> 5
                Calendar.SATURDAY -> 6
                Calendar.SUNDAY -> 7
                else -> 0
            }
        }
    }
}

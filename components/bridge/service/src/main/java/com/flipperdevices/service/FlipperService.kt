package com.flipperdevices.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import com.flipperdevices.bridge.api.manager.FlipperRequestApi

class FlipperService : Service(), FlipperServiceApi {
    private val binder = FlipperServiceBinder(this)

    override fun onBind(intent: Intent?) = binder

    override fun getRequestApi(): FlipperRequestApi {
        TODO("Not yet implemented")
    }
}

class FlipperServiceBinder(
    val serviceApi: FlipperServiceApi
) : Binder() {
    fun closeService() {}
}

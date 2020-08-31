package com.flipper.app

import android.app.Application
import timber.log.Timber

class FlipperApplication : Application() {
  override fun onCreate() {
    super.onCreate()
    if (BuildConfig.DEBUG) {
      Timber.plant(Timber.DebugTree())
    }

    Timber.v("Started!")
  }
}

package com.flipperdevices.core.di

import android.app.Activity
import kotlin.reflect.KClass

data class ApplicationParams(
    val startApplicationClass: KClass<out Activity>,
    val version: String
)

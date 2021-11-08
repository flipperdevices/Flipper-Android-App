package com.flipperdevices.screenstreaming.api

import com.github.terrakok.cicerone.Screen

interface ScreenStreamingApi {
    fun provideScreen(): Screen
}

package com.flipperdevices.filemanager.api.navigation

import com.github.terrakok.cicerone.Screen

interface FileManagerScreenProvider {
    fun fileManager(deviceId: String, path: String = "/ext/"): Screen
}
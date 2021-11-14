package com.flipperdevices.filemanager.api.navigation

import com.flipperdevices.deeplink.model.DeeplinkContent
import com.github.terrakok.cicerone.Screen

const val ROOT_DIR = "/"

interface FileManagerScreenProvider {
    fun fileManager(path: String = ROOT_DIR): Screen
    fun saveWithFileManager(deeplinkContent: DeeplinkContent, path: String = ROOT_DIR): Screen
}

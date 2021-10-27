package com.flipperdevices.filemanager.api.navigation

import com.github.terrakok.cicerone.Screen

interface FileManagerScreenProvider {
    fun fileManager(path: String): Screen
}

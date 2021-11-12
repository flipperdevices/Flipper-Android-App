package com.flipperdevices.filemanager.api.navigation

import android.net.Uri
import com.github.terrakok.cicerone.Screen

const val ROOT_DIR = "/"

interface FileManagerScreenProvider {
    fun fileManager(path: String = ROOT_DIR): Screen
    fun saveWithFileManager(uri: Uri, path: String = ROOT_DIR): Screen
}

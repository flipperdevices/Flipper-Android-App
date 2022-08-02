package com.flipperdevices.filemanager.impl.model

import androidx.compose.runtime.Stable

@Stable
data class FileItem(
    val fileName: String,
    val isDirectory: Boolean,
    val path: String,
    val size: Long
) {
    companion object {
        val DUMMY_FOLDER = FileItem("Test Directory", true, "/any/Test Directory", 0)
        val DUMMY_FILE = FileItem("testfile.ibtn", false, "/any/Test Directory/testfile.ibtn", 1000)
    }
}

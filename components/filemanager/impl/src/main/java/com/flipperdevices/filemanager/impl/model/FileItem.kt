package com.flipperdevices.filemanager.impl.model

data class FileItem(
    val fileName: String,
    val isDirectory: Boolean,
    val size: Long
) {
    companion object {
        val DUMMY = FileItem("Test Directory", true, 1000)
    }
}

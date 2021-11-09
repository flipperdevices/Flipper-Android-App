package com.flipperdevices.filemanager.impl.model

data class FileItem(
    val fileName: String,
    val isDirectory: Boolean,
    val path: String,
    val size: Long
) {
    companion object {
        val DUMMY = FileItem("Test Directory", true, "/any/Test Directory", 1000)
    }
}

package com.flipperdevices.newfilemanager.impl.model

import com.flipperdevices.bridge.connection.feature.storage.api.model.FileType
import com.flipperdevices.bridge.connection.feature.storage.api.model.ListingItem
import okio.Path.Companion.toPath

data class FileItem(
    val fileName: String,
    val isDirectory: Boolean,
    val path: String,
    val size: Long
) {
    constructor(
        directory: String,
        listingItem: ListingItem
    ) : this(
        fileName = listingItem.fileName,
        isDirectory = listingItem.fileType == FileType.DIR,
        path = directory.toPath().resolve(listingItem.fileName).toString(),
        size = listingItem.size
    )

    companion object {
        val DUMMY_FOLDER = FileItem("Test Directory", true, "/any/Test Directory", 0)
        val DUMMY_FILE = FileItem("testfile.ibtn", false, "/any/Test Directory/testfile.ibtn", 1000)
    }
}

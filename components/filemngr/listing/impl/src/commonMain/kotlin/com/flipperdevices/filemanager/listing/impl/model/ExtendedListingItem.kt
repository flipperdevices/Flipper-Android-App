package com.flipperdevices.filemanager.listing.impl.model

import com.flipperdevices.bridge.connection.feature.storage.api.model.FileType
import com.flipperdevices.bridge.connection.feature.storage.api.model.ListingItem
import okio.Path

sealed interface ExtendedListingItem {
    /**
     * Local file-only path
     * example: file.txt, item.svg
     */
    val path: Path

    val itemType: FileType

    val itemName: String
        get() = path.name

    fun asListingItem() = ListingItem(
        fileName = itemName,
        fileType = itemType,
        size = (this as? File)?.size ?: 0
    )

    /**
     * @param path file name path. Not full path
     * @param size file size in bytes
     */
    data class File(
        override val path: Path,
        val size: Long
    ) : ExtendedListingItem {
        override val itemType: FileType = FileType.FILE
    }

    /**
     * @param path file name path. Not full path
     * @param itemsCount amount of items inside
     */
    data class Folder(
        override val path: Path,
        val itemsCount: Int? = null,
    ) : ExtendedListingItem {
        override val itemType: FileType = FileType.DIR
    }
}

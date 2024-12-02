package com.flipperdevices.core.share

interface PlatformShareHelper {
    /**
     * Provide file which can be shared later via [shareFile]
     *
     * If file with same name [fileName] exists, it will be deleted
     */
    fun provideSharableFile(fileName: String): PlatformSharableFile

    /**
     * @param file file to share
     * @param title text displayed as hint for user
     */
    fun shareFile(file: PlatformSharableFile, title: String)
}

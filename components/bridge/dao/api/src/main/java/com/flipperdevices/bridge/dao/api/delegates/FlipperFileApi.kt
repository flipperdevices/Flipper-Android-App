package com.flipperdevices.bridge.dao.api.delegates

import com.flipperdevices.bridge.dao.api.model.FlipperFile
import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.dao.api.model.FlipperKeyContent

interface FlipperFileApi {
    suspend fun getFile(filePath: FlipperFilePath): FlipperFile
    suspend fun deleteFile(filePath: FlipperFilePath)
    suspend fun insert(file: FlipperFile)
    suspend fun updateFileContent(filePath: FlipperFilePath, newContent: FlipperKeyContent)
}

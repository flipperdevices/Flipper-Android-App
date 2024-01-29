package com.flipperdevices.bridge.synchronization.impl.executor

import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.dao.api.model.FlipperKeyContent
import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class StorageType(
    val value: Platform
)

enum class Platform {
    ANDROID,
    FLIPPER
}

interface AbstractKeyStorage {
    suspend fun loadFile(filePath: FlipperFilePath): FlipperKeyContent
    suspend fun modify(filePath: FlipperFilePath, newContent: FlipperKeyContent)
    suspend fun saveFile(filePath: FlipperFilePath, keyContent: FlipperKeyContent)
    suspend fun deleteFile(filePath: FlipperFilePath)
}

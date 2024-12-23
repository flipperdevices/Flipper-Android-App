package com.flipperdevices.nfc.mfkey32.screen.viewmodel

import com.flipperdevices.bridge.connection.feature.storage.api.exception.FStorageFileNotFoundException
import com.flipperdevices.bridge.connection.feature.storage.api.fm.FFileDownloadApi
import com.flipperdevices.bridge.connection.feature.storage.api.fm.FFileUploadApi
import com.flipperdevices.core.FlipperStorageProvider
import com.flipperdevices.core.ktx.jre.withLock
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.core.progress.copyWithProgress
import com.flipperdevices.nfc.mfkey32.screen.model.DuplicatedSource
import com.flipperdevices.nfc.mfkey32.screen.model.FoundedInformation
import com.flipperdevices.nfc.mfkey32.screen.model.FoundedKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import okio.buffer
import okio.source
import java.io.ByteArrayInputStream

private const val FLIPPER_DICT_USER_PATH = "/ext/nfc/assets/mf_classic_dict_user.nfc"
private const val FLIPPER_DICT_PATH = "/ext/nfc/assets/mf_classic_dict.nfc"

class ExistedKeysStorage(
    private val storageProvider: FlipperStorageProvider
) : LogTagProvider {
    override val TAG = "ExistedKeysStorage"
    private val foundedInformationStateFlow = MutableStateFlow(FoundedInformation())

    private val mutex = Mutex()
    private val flipperKeys = HashSet<String>()
    private val userDict = HashSet<String>()
    private val userKeys = ArrayList<String>()

    fun getFoundedInformation(): StateFlow<FoundedInformation> = foundedInformationStateFlow

    suspend fun load(fFileDownloadApi: FFileDownloadApi) {
        val foundedUserDict = loadDict(fFileDownloadApi, FLIPPER_DICT_USER_PATH)
        userDict.addAll(foundedUserDict)
        userKeys.addAll(foundedUserDict)
        val foundedDict = loadDict(fFileDownloadApi, FLIPPER_DICT_PATH)
        flipperKeys.addAll(foundedDict)
    }

    suspend fun upload(fFileUploadApi: FFileUploadApi): List<String> {
        val bytesToWrite = userKeys.joinToString(separator = "\n", postfix = "\n").toByteArray()
        try {
            ByteArrayInputStream(bytesToWrite).source().buffer().use { source ->
                fFileUploadApi.sink(FLIPPER_DICT_USER_PATH).use { sink ->
                    source.copyWithProgress(
                        sink = sink,
                        sourceLength = { bytesToWrite.size.toLong() }
                    )
                }
            }
        } catch (e: FStorageFileNotFoundException) {
            throw e
        } catch (e: Exception) {
            error(e) { "#upload Unhandled exception" }
        }
        return userKeys.minus(userDict).distinct()
    }

    suspend fun onNewKey(
        foundedKey: FoundedKey
    ) = withLock(mutex, "on_new_key") {
        val existed = if (flipperKeys.contains(foundedKey.key)) {
            DuplicatedSource.FLIPPER
        } else if (userDict.contains(foundedKey.key)) {
            DuplicatedSource.USER
        } else {
            null
        }
        foundedInformationStateFlow.update { foundedInformation ->
            foundedInformation.copy(
                keys = foundedInformation.keys.plus(foundedKey),
                uniqueKeys = if (existed == null && foundedKey.key != null) {
                    foundedInformation.uniqueKeys.plus(foundedKey.key)
                } else {
                    foundedInformation.uniqueKeys
                },
                duplicated = if (existed != null && foundedKey.key != null) {
                    foundedInformation.duplicated.plus(foundedKey.key to existed)
                } else {
                    foundedInformation.duplicated
                }
            )
        }
        if (existed == null && foundedKey.key != null) {
            userKeys.add(foundedKey.key)
        }
    }

    private suspend fun loadDict(fFileDownloadApi: FFileDownloadApi, path: String): List<String> {
        return try {
            storageProvider.useTemporaryFile { tmpFile ->
                fFileDownloadApi.download(
                    pathOnFlipper = path,
                    fileOnAndroid = tmpFile,
                    progressListener = { current, max ->
                        info { "Download dict with progress $current/$max" }
                    }
                )
                tmpFile.toFile().readLines()
                    .filterNot { it.startsWith("/") || it.isEmpty() }
            }
        } catch (e: Throwable) {
            error(e) { "Failed load dict $path" }
            emptyList()
        }
    }
}

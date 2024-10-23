package com.flipperdevices.nfc.mfkey32.screen.viewmodel

import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.api.model.wrapToRequest
import com.flipperdevices.bridge.protobuf.streamToCommandFlow
import com.flipperdevices.bridge.rpc.api.FlipperStorageApi
import com.flipperdevices.core.FlipperStorageProvider
import com.flipperdevices.core.ktx.jre.withLock
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.nfc.mfkey32.screen.model.DuplicatedSource
import com.flipperdevices.nfc.mfkey32.screen.model.FoundedInformation
import com.flipperdevices.nfc.mfkey32.screen.model.FoundedKey
import com.flipperdevices.protobuf.Flipper
import com.flipperdevices.protobuf.storage.file
import com.flipperdevices.protobuf.storage.writeRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import java.io.ByteArrayInputStream
import java.io.FileNotFoundException

private const val FLIPPER_DICT_USER_PATH = "/ext/nfc/assets/mf_classic_dict_user.nfc"
private const val FLIPPER_DICT_PATH = "/ext/nfc/assets/mf_classic_dict.nfc"

class ExistedKeysStorage(
    private val flipperStorageApi: FlipperStorageApi,
    private val storageProvider: FlipperStorageProvider
) : LogTagProvider {
    override val TAG = "ExistedKeysStorage"
    private val foundedInformationStateFlow = MutableStateFlow(FoundedInformation())

    private val mutex = Mutex()
    private val flipperKeys = HashSet<String>()
    private val userDict = HashSet<String>()
    private val userKeys = ArrayList<String>()

    fun getFoundedInformation(): StateFlow<FoundedInformation> = foundedInformationStateFlow

    suspend fun load() {
        val foundedUserDict = loadDict(FLIPPER_DICT_USER_PATH)
        userDict.addAll(foundedUserDict)
        userKeys.addAll(foundedUserDict)
        val foundedDict = loadDict(FLIPPER_DICT_PATH)
        flipperKeys.addAll(foundedDict)
    }

    suspend fun upload(requestApi: FlipperRequestApi): List<String> {
        val bytesToWrite = userKeys.joinToString(separator = "\n", postfix = "\n").toByteArray()
        val response = ByteArrayInputStream(bytesToWrite).use { stream ->
            val commandFlow = streamToCommandFlow(stream, bytesToWrite.size.toLong()) { chunkData ->
                storageWriteRequest = writeRequest {
                    path = FLIPPER_DICT_USER_PATH
                    file = file { data = chunkData }
                }
            }
            requestApi.request(commandFlow.map { it.wrapToRequest() })
        }
        if (response.commandStatus != Flipper.CommandStatus.OK) {
            throw FileNotFoundException()
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

    private suspend fun loadDict(path: String): List<String> {
        return try {
            storageProvider.useTemporaryFile { tmpFile ->
                flipperStorageApi.download(
                    pathOnFlipper = path,
                    fileOnAndroid = tmpFile.toFile(),
                    progressListener = { progress ->
                        info { "Download dict with progress $progress" }
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

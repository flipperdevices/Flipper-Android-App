package com.flipperdevices.faphub.installation.manifest.impl.utils

import com.flipperdevices.bridge.dao.api.model.FlipperFileFormat
import com.flipperdevices.faphub.installation.manifest.model.FapManifestItem
import java.io.File
import javax.inject.Inject

private const val FAP_MANIFEST_FILETYPE_KEY = "Filetype"
private const val FAP_MANIFEST_FILETYPE_VALUE = "Flipper Application Installation Manifest"
private const val FAP_MANIFEST_VERSION_KEY = "Version"
private const val FAP_MANIFEST_VERSION_VALUE = "1"
private const val FAP_MANIFEST_UID_KEY = "UID"
private const val FAP_MANIFEST_VERSION_UID_KEY = "Version UID"
private const val FAP_MANIFEST_PATH_KEY = "Path"

class FapManifestParser @Inject constructor() {
    fun parse(fff: FlipperFileFormat, name: String): FapManifestItem? {
        val applicationId = File(name).nameWithoutExtension

        val dict = fff.orderedDict.toMap()
        return FapManifestItem(
            applicationId = applicationId,
            uid = dict[FAP_MANIFEST_UID_KEY] ?: return null,
            versionUid = dict[FAP_MANIFEST_VERSION_UID_KEY] ?: return null,
            path = dict[FAP_MANIFEST_PATH_KEY] ?: return null
        )
    }

    fun encode(fapItem: FapManifestItem): FlipperFileFormat {
        val orderedDict = mutableListOf<Pair<String, String>>()
        orderedDict.add(FAP_MANIFEST_FILETYPE_KEY to FAP_MANIFEST_FILETYPE_VALUE)
        orderedDict.add(FAP_MANIFEST_VERSION_KEY to FAP_MANIFEST_VERSION_VALUE)
        orderedDict.add(FAP_MANIFEST_UID_KEY to fapItem.uid)
        orderedDict.add(FAP_MANIFEST_VERSION_UID_KEY to fapItem.versionUid)
        orderedDict.add(FAP_MANIFEST_PATH_KEY to fapItem.path)

        return FlipperFileFormat(orderedDict)
    }

}
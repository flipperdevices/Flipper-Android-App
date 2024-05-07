package com.flipperdevices.faphub.installedtab.impl.viewmodel

import com.flipperdevices.core.data.SemVer
import com.flipperdevices.faphub.dao.api.model.FapBuildState
import com.flipperdevices.faphub.dao.api.model.FapCategory
import com.flipperdevices.faphub.dao.api.model.FapItemShort
import com.flipperdevices.faphub.dao.api.model.FapItemVersion
import com.flipperdevices.faphub.installation.manifest.model.FapManifestItem
import com.flipperdevices.faphub.target.model.FlipperTarget
import kotlinx.collections.immutable.persistentListOf

internal fun getTestManifest(uid: String) = FapManifestItem(
    applicationAlias = "",
    uid = uid,
    versionUid = "",
    path = "",
    fullName = "",
    iconBase64 = null,
    sdkApi = null,
    sourceFileHash = null,
    isDevCatalog = false
)

internal fun getTestFapItemShort(uid: String, name: String = "") = FapItemShort(
    id = uid,
    picUrl = "",
    shortDescription = "",
    name = name,
    category = FapCategory(
        id = "TESTCATEGORYID",
        name = "test category",
        picUrl = "",
        applicationCount = 1,
        color = null
    ),
    screenshots = persistentListOf(),
    applicationAlias = "",
    upToDateVersion = FapItemVersion(
        id = "TESTVERSIONID",
        version = SemVer(0, 1),
        target = FlipperTarget.Received(
            target = "f7",
            sdk = SemVer(0, 1)
        ),
        buildState = FapBuildState.READY,
        sdkApi = null
    )
)

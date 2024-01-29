package com.flipperdevices.faphub.installedtab.impl.model

import androidx.compose.runtime.Stable
import com.flipperdevices.faphub.installation.manifest.model.FapManifestItem

@Stable
data class OfflineFapApp(
    val name: String,
    val iconBase64: String?,
    val category: String,
    val applicationUid: String,
    val applicationAlias: String
) {
    constructor(fapManifestItem: FapManifestItem) : this(
        name = fapManifestItem.fullName,
        iconBase64 = fapManifestItem.iconBase64,
        category = extractCategoryFromPath(fapManifestItem.path),
        applicationUid = fapManifestItem.uid,
        applicationAlias = fapManifestItem.applicationAlias
    )
}

private const val UNKNOWN_CATEGORY = "Unknown"

private fun extractCategoryFromPath(path: String): String {
    val paths = path.split("/")
    // Example path: /ext/apps/category/app_name.fap
    return paths.getOrElse(paths.size - 2) { UNKNOWN_CATEGORY }
}

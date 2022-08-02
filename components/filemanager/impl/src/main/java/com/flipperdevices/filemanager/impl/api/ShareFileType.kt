package com.flipperdevices.filemanager.impl.api

import android.os.Bundle
import androidx.navigation.NavType
import com.flipperdevices.filemanager.impl.model.ShareFile
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class ShareFileType : NavType<ShareFile>(isNullableAllowed = false) {
    override fun get(bundle: Bundle, key: String): ShareFile? {
        return bundle.getParcelable(key)
    }

    override fun parseValue(value: String): ShareFile {
        return Json.decodeFromString(value)
    }

    override fun put(bundle: Bundle, key: String, value: ShareFile) {
        bundle.putParcelable(key, value)
    }
}

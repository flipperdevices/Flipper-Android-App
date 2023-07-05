package com.flipperdevices.faphub.dao.network.ktorfit.utils

import androidx.datastore.core.DataStore
import com.flipperdevices.core.preference.pb.Settings
import kotlinx.coroutines.flow.first
import javax.inject.Inject

private const val FAP_URL_DEV = "https://catalog.flipp.dev"
private const val FAP_URL = "https://catalog.flipperzero.one"

class HostUrlBuilder @Inject constructor(
    private val settings: DataStore<Settings>
) {
    suspend fun getHostUrl(): String {
        val useDevCatalog = settings.data.first().useDevCatalog
        return if (useDevCatalog) {
            FAP_URL_DEV
        } else {
            FAP_URL
        }
    }
}

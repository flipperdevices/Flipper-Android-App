package com.flipperdevices.updater.card.helpers.delegates

import androidx.datastore.core.DataStore
import com.flipperdevices.bridge.connection.feature.storage.api.FStorageFeatureApi
import com.flipperdevices.bridge.connection.feature.storage.api.fm.FListingStorageApi
import com.flipperdevices.core.preference.pb.Settings
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class UpdateOfferDebugFlagAlwaysTest {
    private val fListingStorageApi: FListingStorageApi = mockk()
    private val fStorageFeatureApi: FStorageFeatureApi = mockk()
    private val dataStoreSettings: DataStore<Settings> = mockk()
    private val delegate: UpdateOfferDelegate = UpdateOfferDebugFlagAlways(
        dataStoreSettings = dataStoreSettings
    )

    @Before
    fun setup() {
        coEvery { fStorageFeatureApi.listingApi() } returns fListingStorageApi
    }

    @Test
    fun `Flag in settings true`() = runTest {
        every { dataStoreSettings.data } returns flowOf(
            Settings(
                always_update = true
            )
        )
        delegate.isRequire(fStorageFeatureApi).collect {
            Assert.assertTrue(it)
        }
    }

    @Test
    fun `Flag in settings false`() = runTest {
        every { dataStoreSettings.data } returns flowOf(
            Settings(
                always_update = false
            )
        )
        delegate.isRequire(fStorageFeatureApi).collect {
            Assert.assertFalse(it)
        }
    }
}

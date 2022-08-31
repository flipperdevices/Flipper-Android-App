package com.flipperdevices.updater.card.helpers.delegates

import androidx.datastore.core.DataStore
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.core.preference.pb.Settings
import com.flipperdevices.core.preference.pb.settings
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class UpdateOfferDelegateFlagAlwaysTest {
    private val serviceApi: FlipperServiceApi = mockk()
    private val dataStoreSettings: DataStore<Settings> = mockk()
    private val delegate: UpdateOfferDelegate = UpdateOfferDelegateFlagAlways(
        dataStoreSettings = dataStoreSettings
    )

    @Test
    fun `Flag in settings true`() = runTest {
        every { dataStoreSettings.data } returns flowOf(
            settings {
                alwaysUpdate = true
            }
        )
        delegate.isRequire(serviceApi).collect {
            Assert.assertTrue(it)
        }
    }

    @Test
    fun `Flag in settings false`() = runTest {
        every { dataStoreSettings.data } returns flowOf(
            settings {
                alwaysUpdate = false
            }
        )
        delegate.isRequire(serviceApi).collect {
            Assert.assertFalse(it)
        }
    }
}

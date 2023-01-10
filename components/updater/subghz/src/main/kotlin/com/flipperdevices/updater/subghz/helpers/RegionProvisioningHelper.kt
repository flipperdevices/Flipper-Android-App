package com.flipperdevices.updater.subghz.helpers

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.telephony.TelephonyManager
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.updater.subghz.model.RegionProvisioning
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface RegionProvisioningHelper {
    suspend fun provideRegion(regionFromGeoIp: String?): RegionProvisioning
}

@ContributesBinding(AppGraph::class, RegionProvisioningHelper::class)
class RegionProvisioningHelperImpl @Inject constructor(
    private val context: Context
) : RegionProvisioningHelper, LogTagProvider {
    override val TAG = "CountryProvisioningHelper"

    override suspend fun provideRegion(regionFromGeoIp: String?): RegionProvisioning {
        val (simCountry, networkCountry, isRoaming) = getCountryFromSimCards()
        val systemRegion = getRegionFromSystem()

        return RegionProvisioning(
            regionFromNetwork = networkCountry,
            regionFromSim = simCountry,
            isRoaming = isRoaming,
            regionFromIp = regionFromGeoIp,
            regionSystem = systemRegion
        )
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun getRegionFromSystem(): String {
        val locale: String = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.resources.configuration.locales[0].country
        } else {
            context.resources.configuration.locale.country
        }
        return locale.uppercase()
    }

    private suspend fun getCountryFromSimCards(): Triple<String?, String?, Boolean> =
        withContext(Dispatchers.Main) {
            val telephonyManager = context.getSystemService(TelephonyManager::class.java)

            val simCountry = telephonyManager.simCountryIso
            val networkCountryIso = telephonyManager.networkCountryIso
            val isRoaming = telephonyManager.isNetworkRoaming

            info { "Sim card region: $simCountry, network $networkCountryIso, roaming $isRoaming" }

            return@withContext Triple(simCountry, networkCountryIso, isRoaming)
        }
}

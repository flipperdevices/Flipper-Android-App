package com.flipperdevices.updater.impl.tasks

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.telephony.TelephonyManager
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.updater.impl.model.RegionProvisioning
import com.flipperdevices.updater.impl.model.RegionProvisioningSource
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val COUNTRY_CODE_LENGTH = 2

interface RegionProvisioningHelper {
    suspend fun provideRegion(regionFromGeoIp: String?): RegionProvisioning?
}

@ContributesBinding(AppGraph::class, RegionProvisioningHelper::class)
class RegionProvisioningHelperImpl @Inject constructor(
    private val context: Context
) : RegionProvisioningHelper, LogTagProvider {
    override val TAG = "CountryProvisioningHelper"

    override suspend fun provideRegion(regionFromGeoIp: String?): RegionProvisioning? {
        val simCards = getCountryFromSimCards()
        if (simCards != null) {
            return simCards
        }
        if (regionFromGeoIp != null &&
            regionFromGeoIp.isNotBlank() &&
            regionFromGeoIp.length == COUNTRY_CODE_LENGTH
        ) {
            return RegionProvisioning(
                regionFromGeoIp,
                RegionProvisioningSource.GEO_IP
            )
        }
        val systemRegion = getRegionFromSystem()
        if (systemRegion != null) {
            return systemRegion
        }
        return null
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun getRegionFromSystem(): RegionProvisioning? {
        val locale: String = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.resources.configuration.locales[0].country
        } else {
            context.resources.configuration.locale.country
        }
        if (locale.isNotBlank() && locale.length == COUNTRY_CODE_LENGTH) {
            return RegionProvisioning(
                locale,
                RegionProvisioningSource.SYSTEM
            )
        }
        return null
    }

    private suspend fun getCountryFromSimCards(): RegionProvisioning? =
        withContext(Dispatchers.Main) {
            val telephonyManager = context.getSystemService(TelephonyManager::class.java)

            val simCountry = telephonyManager.simCountryIso
            val networkCountryIso = telephonyManager.networkCountryIso

            info { "Sim card region: $simCountry and network $networkCountryIso" }

            if (networkCountryIso.isNotBlank() && networkCountryIso.length == COUNTRY_CODE_LENGTH) {
                return@withContext RegionProvisioning(
                    networkCountryIso,
                    RegionProvisioningSource.SIM_NETWORK
                )
            }
            if (simCountry.isNotBlank() && simCountry.length == COUNTRY_CODE_LENGTH) {
                return@withContext RegionProvisioning(
                    simCountry,
                    RegionProvisioningSource.SIM_COUNTRY
                )
            }
            return@withContext null
        }
}

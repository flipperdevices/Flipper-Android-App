package com.flipperdevices.selfupdater.source.github.parser

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.di.ApplicationParams
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

interface CompareVersionParser {
    fun isThatNewVersion(newVersion: String): Boolean
}

@ContributesBinding(AppGraph::class, CompareVersionParser::class)
class CompareVersionParserImpl @Inject constructor(
    private val applicationParams: ApplicationParams
) : CompareVersionParser, LogTagProvider {
    override fun isThatNewVersion(newVersion: String): Boolean {
        info { "Compare version ${applicationParams.version} with $newVersion" }

        val currentVersionParts = applicationParams.version.split(".")
        val newVersionParts = newVersion.split(".")

        if (currentVersionParts.size > newVersionParts.size) return false

        for (i in currentVersionParts.indices) {
            val currentVersionPart = currentVersionParts[i].toInt()
            val newVersionPart = newVersionParts[i].toInt()

            if (currentVersionPart < newVersionPart) return true
            if (currentVersionPart > newVersionPart) return false
        }

        return false
    }

    override val TAG: String get() = "CompareVersionParserImpl"
}

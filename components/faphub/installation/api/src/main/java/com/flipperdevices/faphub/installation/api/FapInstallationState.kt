package com.flipperdevices.faphub.installation.api

import androidx.annotation.FloatRange

private const val MIN_PROCESS = 0.0
private const val MAX_PROCESS = 1.0

sealed class FapInstallationState {
    object Install : FapInstallationState()
    data class Installing(
        @FloatRange(MIN_PROCESS, MAX_PROCESS) val process: Float,
    ) : FapInstallationState()
    object Installed : FapInstallationState()
    object Update : FapInstallationState()
    data class Updating(
        @FloatRange(MIN_PROCESS, MAX_PROCESS) val process: Float,
    ) : FapInstallationState()

    companion object {
        // For mock
        @SuppressWarnings("MagicNumber")
        fun getRandomState(): FapInstallationState {
            return when ((0..4).random()) {
                0 -> Installing((0..100).random().toFloat() / 100)
                1 -> Installed
                2 -> Update
                3 -> Updating((0..100).random().toFloat() / 100)
                else -> Install
            }
        }
    }
}

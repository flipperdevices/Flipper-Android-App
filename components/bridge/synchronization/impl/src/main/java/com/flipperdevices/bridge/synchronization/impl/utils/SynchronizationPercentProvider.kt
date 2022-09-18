package com.flipperdevices.bridge.synchronization.impl.utils

import androidx.datastore.core.DataStore
import com.flipperdevices.bridge.synchronization.impl.di.TaskGraph
import com.flipperdevices.core.di.SingleIn
import com.flipperdevices.core.preference.pb.Settings
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject
import kotlin.math.max
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

private const val FIRST_LISTING_PERCENT = 0.1f
private const val FIRST_HASH_PERCENT = 0.2f
private const val FIRST_DOWNLOAD_PERCENT = 1f
private const val LISTING_PERCENT = 0.45f
private const val HASH_PERCENT = 0.9f
private const val DOWNLOAD_PERCENT = 1f

interface SynchronizationPercentProvider {
    fun getListingProgress(
        processed: Int,
        total: Int
    ): Float

    fun getHashProgress(
        processed: Int,
        total: Int
    ): Float

    fun getDownloadProgress(
        processed: Int,
        total: Int
    ): Float

    suspend fun markedAsFinish()
}

@SingleIn(TaskGraph::class)
@ContributesBinding(TaskGraph::class, SynchronizationPercentProvider::class)
class SynchronizationPercentProviderImpl @Inject constructor(
    private val preference: DataStore<Settings>
) : SynchronizationPercentProvider {
    private val isFirstTime by lazy {
        runBlocking { !preference.data.first().firstSynchronizationPassed }
    }

    override fun getListingProgress(
        processed: Int,
        total: Int
    ): Float {
        return getProgressUpdate(
            processed, total,
            previousStagePercent = 0f,
            stagePercent = if (isFirstTime) {
                FIRST_LISTING_PERCENT
            } else LISTING_PERCENT
        )
    }

    override fun getHashProgress(
        processed: Int,
        total: Int
    ): Float {
        return getProgressUpdate(
            processed, total,
            previousStagePercent = if (isFirstTime) {
                FIRST_LISTING_PERCENT
            } else LISTING_PERCENT,
            stagePercent = if (isFirstTime) {
                FIRST_HASH_PERCENT
            } else HASH_PERCENT
        )
    }

    override fun getDownloadProgress(
        processed: Int,
        total: Int
    ): Float {
        return getProgressUpdate(
            processed, total,
            previousStagePercent = if (isFirstTime) {
                FIRST_HASH_PERCENT
            } else HASH_PERCENT,
            stagePercent = if (isFirstTime) {
                FIRST_DOWNLOAD_PERCENT
            } else DOWNLOAD_PERCENT
        )
    }

    override suspend fun markedAsFinish() {
        preference.updateData {
            it.toBuilder()
                .setFirstSynchronizationPassed(true)
                .build()
        }
    }

    private fun getProgressUpdate(
        processed: Int,
        total: Int,
        previousStagePercent: Float,
        stagePercent: Float
    ): Float {
        val whichPercentAllowed = max(0f, stagePercent - previousStagePercent)
        val taskPercent = processed.toFloat() / total.toFloat()
        val resultPercent = taskPercent * whichPercentAllowed
        return previousStagePercent + resultPercent
    }
}

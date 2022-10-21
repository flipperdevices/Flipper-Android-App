package com.flipperdevices.wearable.emulate.impl.viewmodel

import com.flipperdevices.core.di.SingleIn
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.wearable.emulate.impl.di.WearGraph
import com.google.android.gms.wearable.CapabilityClient
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await

interface NodeFindingHelper {
    suspend fun findNode(): String?
}

private const val CAPABILITY_PHONE_APP = "emulate_proto_flipper_phone_app"

@SingleIn(WearGraph::class)
@ContributesBinding(WearGraph::class, NodeFindingHelper::class)
class NodeFindingHelperImpl @Inject constructor(
    private val capabilityClient: CapabilityClient
) : NodeFindingHelper, LogTagProvider {
    override val TAG = "NodeFindingHelper"

    override suspend fun findNode(): String? {
        info { "#checkIfPhoneHasApp" }

        try {
            val capabilityInfo = capabilityClient
                .getCapability(CAPABILITY_PHONE_APP, CapabilityClient.FILTER_ALL)
                .await()

            info { "Capability request succeeded" }

            val foundedNode = capabilityInfo.nodes.firstOrNull { it.isNearby }
                ?: capabilityInfo.nodes.firstOrNull()
            info { "Found node $foundedNode" }
            return foundedNode?.id
        } catch (ignored: CancellationException) {
            // Request was cancelled normally
        } catch (throwable: Throwable) {
            error(throwable) { "Capability request failed to return any results." }
        }
        return null
    }
}

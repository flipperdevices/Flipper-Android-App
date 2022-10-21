package com.flipperdevices.wearable.emulate.impl.viewmodel

import com.flipperdevices.wearable.emulate.common.WearableCommandOutputStream
import com.flipperdevices.wearable.emulate.common.ipcemulate.Main
import com.flipperdevices.wearable.emulate.common.ipcemulate.mainRequest
import com.flipperdevices.wearable.emulate.common.ipcemulate.requests.sendRequest
import com.flipperdevices.wearable.emulate.common.ipcemulate.requests.startEmulateRequest
import com.flipperdevices.wearable.emulate.common.ipcemulate.requests.stopEmulateRequest
import com.flipperdevices.wearable.emulate.impl.di.WearGraph
import com.flipperdevices.wearable.emulate.impl.model.KeyToEmulate
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

interface EmulateHelper {
    fun onStartEmulate()
    fun onSend()
    fun onStopEmulate()
}

@ContributesBinding(WearGraph::class, EmulateHelper::class)
class EmulateHelperImpl @Inject constructor(
    private val commandOutputStream: WearableCommandOutputStream<Main.MainRequest>,
    private val keyToEmulate: KeyToEmulate
) : EmulateHelper {
    override fun onStartEmulate() = commandOutputStream.send(
        mainRequest {
            startEmulate = startEmulateRequest {
                path = keyToEmulate.keyPath
            }
        }
    )


    override fun onSend() = commandOutputStream.send(
        mainRequest {
            sendRequest = sendRequest {
                path = keyToEmulate.keyPath
            }
        }
    )

    override fun onStopEmulate() = commandOutputStream.send(
        mainRequest {
            stopEmulate = stopEmulateRequest { }
        }
    )

}
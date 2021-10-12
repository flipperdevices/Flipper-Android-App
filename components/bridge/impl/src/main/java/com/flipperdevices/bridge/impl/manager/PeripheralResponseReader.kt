package com.flipperdevices.bridge.impl.manager

import com.flipperdevices.bridge.utils.ByteEndlessInputStream
import com.flipperdevices.protobuf.Flipper
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class PeripheralResponseReader {
    private val byteInputStream = ByteEndlessInputStream()
    private val responses = MutableStateFlow<Flipper.Main?>(null)

    init {
        Thread { // TODO attach to lifecycle
            while (true) {
                val main = Flipper.Main.parseDelimitedFrom(byteInputStream)
                GlobalScope.launch {
                    responses.emit(main)
                }
            }
        }.start()
    }

    fun onReceiveBytes(byteArray: ByteArray) {
        Timber.i("Receive proto array with size: ${byteArray.size}")
        byteInputStream.write(byteArray)
    }

    fun getResponses(): StateFlow<Flipper.Main?> = responses
}
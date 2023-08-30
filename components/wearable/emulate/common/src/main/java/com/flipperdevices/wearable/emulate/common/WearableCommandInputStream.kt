package com.flipperdevices.wearable.emulate.common

import com.flipperdevices.core.ktx.jre.launchWithLock
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.google.android.gms.wearable.ChannelClient
import com.google.android.gms.wearable.ChannelClient.Channel
import com.google.protobuf.GeneratedMessageLite
import com.google.protobuf.InvalidProtocolBufferException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.tasks.await
import java.io.InputStream

private const val READ_TIMEOUT_MS = 100L

class WearableCommandInputStream<T : GeneratedMessageLite<*, *>>(
    private val channelClient: ChannelClient,
    private val parser: (InputStream) -> T?
) : LogTagProvider {
    override val TAG = "WearableCommandInputStream"

    private val mutex = Mutex()
    private val requests = MutableSharedFlow<T>()
    private var parserJob: Job? = null

    fun getRequestsFlow(): Flow<T> = requests

    fun onOpenChannel(scope: CoroutineScope, channel: Channel) {
        launchWithLock(mutex, scope, "open_channel") {
            parserJob?.cancelAndJoin()
            parserJob = scope.launch(Dispatchers.Default) {
                channelClient.getInputStream(channel).await().use {
                    parseLoopJob(this, it)
                }
            }
        }
    }

    fun onCloseChannel(scope: CoroutineScope) {
        launchWithLock(mutex, scope, "close_channel") {
            parserJob?.cancelAndJoin()
        }
    }

    private suspend fun parseLoopJob(scope: CoroutineScope, inputStream: InputStream) {
        while (scope.isActive) {
            try {
                val main = parser(inputStream)
                if (main == null) {
                    delay(READ_TIMEOUT_MS)
                    continue
                }
                info { "Receive $main response" }
                scope.launch(Dispatchers.Default) {
                    requests.emit(main)
                }
            } catch (ignored: CancellationException) {
                // ignore
            } catch (ignored: java.util.concurrent.CancellationException) {
                // ignore
            } catch (invalidProtocol: InvalidProtocolBufferException) {
                error(invalidProtocol) { "Broke protocol" }
            } catch (e: Exception) {
                error(e) { "Failed parse stream" }
            }
            delay(READ_TIMEOUT_MS)
        }
    }
}

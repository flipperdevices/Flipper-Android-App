package com.flipperdevices.wearable.emulate.common

import com.flipperdevices.core.ktx.jre.launchWithLock
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.google.android.gms.wearable.ChannelClient
import com.google.android.gms.wearable.ChannelClient.Channel
import com.squareup.wire.ProtoAdapter
import com.squareup.wire.ProtoWriter
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import okio.buffer
import okio.sink
import java.io.IOException
import java.io.OutputStream
import java.util.concurrent.Executors
import java.util.concurrent.LinkedTransferQueue
import java.util.concurrent.TimeUnit

private const val TIMEOUT_MS = 100L
private const val POOL_THREADS_AMOUNT = 2

class WearableCommandOutputStream<T>(
    private val channelClient: ChannelClient,
    private val adapter: ProtoAdapter<T>
) : LogTagProvider {
    override val TAG = "WearableCommandOutputStream-${hashCode()}"

    private val dispatcher = Executors.newFixedThreadPool(POOL_THREADS_AMOUNT).asCoroutineDispatcher()

    private val queue = LinkedTransferQueue<T>()
    private val mutex = Mutex()
    private var sendJob: Job? = null

    fun onOpenChannel(scope: CoroutineScope, channel: Channel) {
        launchWithLock(mutex, scope, "open_channel") {
            sendJob?.cancelAndJoin()
            sendJob = scope.launch(dispatcher) {
                channelClient.getOutputStream(channel).await().use {
                    sendLoopJob(this, it)
                }
            }
        }
    }

    fun onCloseChannel(scope: CoroutineScope) {
        queue.clear()
        launchWithLock(mutex, scope, "close_channel") {
            sendJob?.cancelAndJoin()
        }
    }

    fun send(command: T) {
        queue.add(command)
    }

    private suspend fun sendLoopJob(scope: CoroutineScope, outputStream: OutputStream) {
        while (scope.isActive) {
            try {
                val request = runCatching {
                    queue.poll(TIMEOUT_MS, TimeUnit.MILLISECONDS)
                }.getOrNull() ?: continue
                info { "Receive $request" }

                withContext(dispatcher) {
                    adapter.writeDelimitedTo(outputStream, request)
                }
            } catch (ignored: CancellationException) {
                // ignore
            } catch (ioException: IOException) {
                error(ioException) { "Broke protocol" }
            } catch (e: Exception) {
                error(e) { "Failed parse stream" }
            }
        }
    }

    private fun ProtoAdapter<T>.writeDelimitedTo(stream: OutputStream, value: T) {
        val bufferedSink = stream.sink().buffer()
        val writer = ProtoWriter(bufferedSink)
        writer.writeVarint32(encodedSize(value))
        encode(bufferedSink, value)
        bufferedSink.emit()
    }
}

package com.flipperdevices.core.atomicfile

import okio.Sink

actual class SinkWithOutputStream(sink: Sink) : Sink by sink

fun Sink.wrap(): SinkWithOutputStream = SinkWithOutputStream(this)

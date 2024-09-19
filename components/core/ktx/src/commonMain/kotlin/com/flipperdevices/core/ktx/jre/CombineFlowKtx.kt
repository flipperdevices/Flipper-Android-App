package com.flipperdevices.core.ktx.jre

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

public fun <T1, T2> Flow<T1>.combine(flow: Flow<T2>): Flow<Pair<T1, T2>> =
    combine(flow) { t1, t2 -> t1 to t2 }

fun <T1> Flow<T1>.withFirstElement(firstElement: T1): Flow<T1> {
    return flow {
        emit(firstElement)
        emitAll(this@withFirstElement)
    }
}

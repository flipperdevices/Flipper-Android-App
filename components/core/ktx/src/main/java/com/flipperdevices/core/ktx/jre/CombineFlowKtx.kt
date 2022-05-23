package com.flipperdevices.core.ktx.jre

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

public fun <T1, T2> Flow<T1>.combine(flow: Flow<T2>): Flow<Pair<T1, T2>> =
    combine(flow) { t1, t2 -> t1 to t2 }

package com.flipperdevices.bridge.service.impl.provider.lifecycle

interface FlipperServiceLifecycleListener {
    /**
     * @return should caller remove it from list after execution
     */
    fun onInternalStop(): Boolean
}

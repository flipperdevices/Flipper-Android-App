package com.flipperdevices.bridge.impl.manager.overflow

import com.flipperdevices.bridge.api.model.FlipperRequest

class FlipperRequestComparator : Comparator<FlipperRequest> {
    override fun compare(first: FlipperRequest, second: FlipperRequest): Int {
        return first.priority.ordinal - second.priority.ordinal
    }
}

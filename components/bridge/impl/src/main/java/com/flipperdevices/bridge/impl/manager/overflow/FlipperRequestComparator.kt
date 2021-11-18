package com.flipperdevices.bridge.impl.manager.overflow

import com.flipperdevices.bridge.api.model.FlipperRequest

private const val GREATER = 1
private const val EQUALS = 0
private const val LESS = -1

class FlipperRequestComparator : Comparator<FlipperRequest> {
    override fun compare(first: FlipperRequest, second: FlipperRequest): Int {
        var solution = EQUALS

        // Request with high priority have large timestamp
        if (first.priority.ordinal > second.priority.ordinal) {
            solution = GREATER
        } else if (first.priority.ordinal < second.priority.ordinal) {
            solution = LESS
        }

        if (solution != EQUALS) {
            return solution
        }

        // Request with lower timestamp have larger priority
        if (first.createTimestampNanos > second.createTimestampNanos) {
            solution = GREATER
        } else if (first.createTimestampNanos < second.createTimestampNanos) {
            solution = LESS
        }

        return solution
    }
}

package com.flipperdevices.core.ktx.jre

fun <T> MutableList<T>.safeRemoveFirst() {
    if (this.isNotEmpty()) {
        this.removeAt(0)
    }
}

fun <T> MutableList<T>.safeRemoveLast() {
    if (this.isNotEmpty()) {
        this.removeAt(this.size - 1)
    }
}

package com.flipperdevices.core.ktx.jre

fun isNotNull(vararg items: Any?): Boolean {
    return items.none { it == null }
}

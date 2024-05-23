@file:Suppress("FunctionNaming")

package com.flipperdevices.core.di

import org.junit.Assert
import org.junit.Test

/**
 * This is a sample test class to demonstrate how to use jvmSharedTest source set
 */
class ComponentHolderJvmSharedSampleTest {
    @Test
    fun GIVEN_empty_holder_WHEN_taking_int_class_THEN_throws() {
        Assert.assertThrows(Throwable::class.java) {
            ComponentHolder.component<Int>()
        }
    }
}

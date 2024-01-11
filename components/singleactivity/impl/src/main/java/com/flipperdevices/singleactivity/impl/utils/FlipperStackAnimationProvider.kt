package com.flipperdevices.singleactivity.impl.utils

import com.arkivanov.decompose.extensions.compose.stack.animation.StackAnimation
import com.arkivanov.decompose.extensions.compose.stack.animation.StackAnimationProvider
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.stack.animation.plus
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation

object FlipperStackAnimationProvider : StackAnimationProvider {
    override fun <C : Any, T : Any> provide(): StackAnimation<C, T> {
        return stackAnimation(fade())
    }
}

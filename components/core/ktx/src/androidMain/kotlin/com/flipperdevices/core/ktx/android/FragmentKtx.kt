package com.flipperdevices.core.ktx.android

import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

fun <T : Fragment> T.withArgs(argsBuilder: Bundle.() -> Unit): T {
    val bundle = arguments ?: Bundle()
    argsBuilder.invoke(bundle)
    arguments = bundle
    return this
}

fun Fragment.setStatusBarColor(@ColorRes statusBarColor: Int?) {
    if (statusBarColor == null) {
        return
    }
    val contextNotNull = context ?: return
    val window: Window = activity?.window ?: return
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    window.statusBarColor = ContextCompat.getColor(contextNotNull, statusBarColor)
}

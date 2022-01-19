package com.flipperdevices.core.ktx.android

import android.os.Bundle
import androidx.fragment.app.Fragment

fun <T : Fragment> T.withArgs(argsBuilder: Bundle.() -> Unit): T {
    val bundle = arguments ?: Bundle()
    argsBuilder.invoke(bundle)
    arguments = bundle
    return this
}

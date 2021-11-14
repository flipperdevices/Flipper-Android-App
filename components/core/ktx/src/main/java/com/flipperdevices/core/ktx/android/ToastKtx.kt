package com.flipperdevices.core.ktx.android

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment

@Deprecated(
    "Please, use it only in debug purpose",
    ReplaceWith(
        "toast(R.string.my_text)"
    )
)
fun Context.toast(text: CharSequence, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, text, duration).show()
}

fun Context.toast(@StringRes resId: Int, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, resId, duration).show()
}

@Deprecated(
    "Please, use it only in debug purpose",
    ReplaceWith(
        "toast(R.string.my_text)"
    )
)
fun Fragment.toast(text: CharSequence, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(requireContext(), text, duration).show()
}

fun Fragment.toast(@StringRes resId: Int, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(requireContext(), resId, duration).show()
}

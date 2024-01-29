package com.flipperdevices.nfc.tools.impl.bindings

object MfKey32Binding {
    init {
        System.loadLibrary("mfkey32")
    }

    /**
     * @return less zero if failed recover key
     */
    @JvmStatic
    @Suppress("LongParameterList")
    external fun tryRecoverKey(
        uid: Long, // serial number
        nt0: Long, // tag challenge first
        nr0: Long, // first encrypted reader challenge
        ar0: Long, // first encrypted reader response
        nt1: Long, // tag challenge second
        nr1: Long, // second encrypted reader challenge
        ar1: Long // second encrypted reader response
    ): String?
}

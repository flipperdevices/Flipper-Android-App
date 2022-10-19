package com.flipperdevices.nfc.tools.impl.bindings

object MfKey32Binding {
    /**
     * @return less zero if failed recover key
     */
    @JvmStatic
    external fun tryRecoverKey(
        uid: Int, // serial number
        nt0: Int, // tag challenge first
        nr0: Int, // first encrypted reader challenge
        ar0: Int, // first encrypted reader response
        nt1: Int, // tag challenge second
        nr1: Int, // second encrypted reader challenge
        ar1: Int // second encrypted reader response
    ): String?
}
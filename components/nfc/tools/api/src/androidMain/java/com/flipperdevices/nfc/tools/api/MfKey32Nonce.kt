package com.flipperdevices.nfc.tools.api

data class MfKey32Nonce(
    val sectorName: String,
    val keyName: String,
    val uid: UInt, // serial number
    val nt0: UInt, // tag challenge first
    val nr0: UInt, // first encrypted reader challenge
    val ar0: UInt, // first encrypted reader response
    val nt1: UInt, // tag challenge second
    val nr1: UInt, // second encrypted reader challenge
    val ar1: UInt // second encrypted reader response
)

package com.flipperdevices.core.ui.hexkeyboard

enum class HexKey(val title: String) {
    Zero("0"), One("1"), Two("2"),
    Three("3"), Four("4"), Five("5"),
    Six("6"), Seven("7"), Eight("8"),
    Nine("9"), Clear("<="), Ok("OK"),
    A("A"), B("B"), C("C"), D("D"), E("E"), F("F")
}

val Keys789 = listOf(
    HexKey.Seven,
    HexKey.Eight,
    HexKey.Nine
)

val Keys456 = listOf(
    HexKey.Four,
    HexKey.Five,
    HexKey.Six
)

val Keys123 = listOf(
    HexKey.One,
    HexKey.Two,
    HexKey.Three
)

val Keys0 = listOf(
    HexKey.Zero
)

val KeysABC = listOf(
    HexKey.A,
    HexKey.B,
    HexKey.C
)

val KeysDEF = listOf(
    HexKey.D,
    HexKey.E,
    HexKey.F
)

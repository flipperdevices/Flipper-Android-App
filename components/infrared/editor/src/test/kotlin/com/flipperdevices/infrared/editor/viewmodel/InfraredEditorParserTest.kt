package com.flipperdevices.infrared.editor.viewmodel

import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.flipperdevices.bridge.dao.api.model.FlipperFileFormat
import com.flipperdevices.core.test.readTestAssetString
import com.flipperdevices.infrared.editor.model.InfraredRemote
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.TIRAMISU])
class InfraredEditorParserTest {

    @Test
    @Suppress("MaximumLineLength", "MaxLineLength")
    fun mixedInfraredRemote() {
        val fff = FlipperFileFormat.fromFileContent(readTestAssetString("infrared.ir"))
        val actualRemote = InfraredEditorParser.mapParsedKeyToInfraredRemotes(fff)

        val expectedRemote = listOf(
            InfraredRemote.Raw(
                nameInternal = "Heat_lo",
                typeInternal = "raw",
                frequency = "38000",
                dutyCycle = "0.330000",
                data = "3045 1677 493 1023 524 1024 522 354 469 354 469 354 469 1026 576 353 470 350 487 1001 492 1054 492 354 469 1054 492 355 468 354 469 1054 492 1070 491 354 468 1056 438 1109 438 386 437 385 438 1107 439 385 438 400 464 1081 465 359 464 360 463 361 462 362 461 388 435 388 435 404 434 389 434 389 434 389 434 389 434 389 434 389 434 389 434 405 434 389 434 389 434 1112 434 389 434 389 434 1113 434 389 434 405 434 1112 435 389 434 366 458 365 458 365 458 365 458 365 458 381 458 365 458 366 457 365 458 1089 457 366 457 366 457 366 457 382 456 367 433 390 433 391 432 391 432 391 432 391 432 391 432 406 433 391 432 391 432 390 433 391 432 391 432 391 432 391 432 406 433 391 432 391 433 391 432 391 432 391 432 391 432 391 432 407 432 391 432 391 432 391 432 392 431 391 433 391 432 391 432 430 409 393 430 392 431 392 431 392 432 391 457 367 432 392 431 408 431 392 431 1138 433 391 408 392 431 392 456 367 456 1113 408 421 433",
            ),
            InfraredRemote.Raw(
                nameInternal = "Dh",
                typeInternal = "raw",
                frequency = "38000",
                dutyCycle = "0.330000",
                data = "3092 3057 3092 4438 579 1675 545 534 576 1650 571 535 575 531 569 1656 575 1652 579 527 573 533 577 1648 572 1655 576 1651 580 526 573 532 578 1647 573 532 578 1647 573 1654 577 529 571 535 575 530 570 535 575 529 571 534 576 529 571 534 576 528 571 534 576 528 572 533 577 528 572 533 577 527 573 1651 580 526 573 532 578 527 572 532 568 537 573 531 568 536 574 1650 571 535 575 531 569 536 574 530 569 535 575 529 571 534 576 529 571 533 577 528 572 533 577 527 572 532 568 536 574 531 569 1655 576 530 570 535 575 530 570 535 575 529 571 534 576 528 572 533 577 527 573 532 578 527 572 531 569 536 574 531 569 535 575 530 570 534 576 529 571 534 576 528 571 533 577 527 573 532 567 537 573 531 569 536 574 530 570 535 575 529 571 534 576 528 571 533 577 527 572 532 578 526 573 531 569 536 574 530 570 535 575 529 571 534 576 528 572 533 577 1646 574 532 578 1646 574 1652 579 527 572 533 577 1647 573 1653 578 1675 545 534 576 1649 571"
            ),
            InfraredRemote.Parsed(
                nameInternal = "Mute",
                typeInternal = "parsed",
                protocol = "NEC",
                address = "77 00 00 00",
                command = "F3 00 00 00",
            ),
            InfraredRemote.Parsed(
                nameInternal = "Vol_up",
                typeInternal = "parsed",
                protocol = "NEC",
                address = "77 00 00 00",
                command = "FB 00 00 00",
            )
        )
        Assert.assertArrayEquals(actualRemote.toTypedArray(), expectedRemote.toTypedArray())
    }
}

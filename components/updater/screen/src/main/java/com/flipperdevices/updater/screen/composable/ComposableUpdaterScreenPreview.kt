package com.flipperdevices.updater.screen.composable

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.flipperdevices.core.preference.pb.HardwareColor
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.updater.model.DistributionFile
import com.flipperdevices.updater.model.FirmwareChannel
import com.flipperdevices.updater.model.FirmwareVersion
import com.flipperdevices.updater.model.VersionFiles
import com.flipperdevices.updater.screen.model.FailedReason
import com.flipperdevices.updater.screen.model.UpdaterScreenState

@Composable
@Preview(
    showBackground = true,
    showSystemUi = true
)
private fun ComposableUpdaterScreenNotStartedPreview() {
    FlipperThemeInternal {
        ComposableUpdaterScreen(
            updaterScreenState = UpdaterScreenState.NotStarted,
            flipperColor = HardwareColor.BLACK,
            onCancel = {},
            onRetry = {}
        )
    }
}

@Composable
@Preview(
    showBackground = true,
    showSystemUi = true
)
private fun ComposableUpdaterScreenCancelingSynchronizationPreview() {
    val data = VersionFiles(
        version = FirmwareVersion(
            channel = FirmwareChannel.RELEASE,
            version = "0.65.2"
        ),
        updaterFile = DistributionFile(url = "", sha256 = ""),
        changelog = changelog
    )
    FlipperThemeInternal {
        ComposableUpdaterScreen(
            updaterScreenState = UpdaterScreenState.CancelingSynchronization(firmwareData = data),
            flipperColor = HardwareColor.BLACK,
            onCancel = {},
            onRetry = {}
        )
    }
}

@Composable
@Preview(
    showBackground = true,
    showSystemUi = true
)
private fun ComposableUpdaterScreenDownloadingFromNetworkPreview() {
    val data = VersionFiles(
        version = FirmwareVersion(
            channel = FirmwareChannel.RELEASE,
            version = "0.65.2"
        ),
        updaterFile = DistributionFile(url = "", sha256 = ""),
        changelog = changelog
    )
    FlipperThemeInternal {
        ComposableUpdaterScreen(
            updaterScreenState = UpdaterScreenState.DownloadingFromNetwork(
                firmwareData = data,
                percent = 0.5f
            ),
            flipperColor = HardwareColor.BLACK,
            onCancel = {},
            onRetry = {}
        )
    }
}

@Composable
@Preview(
    showBackground = true,
    showSystemUi = true
)
private fun ComposableUpdaterScreenUploadOnFlipperPreview() {
    val data = VersionFiles(
        version = FirmwareVersion(
            channel = FirmwareChannel.RELEASE,
            version = "0.65.2"
        ),
        updaterFile = DistributionFile(url = "", sha256 = ""),
        changelog = changelog
    )
    FlipperThemeInternal {
        ComposableUpdaterScreen(
            updaterScreenState =
            UpdaterScreenState.UploadOnFlipper(firmwareData = data, percent = 0.5f),
            flipperColor = HardwareColor.BLACK,
            onCancel = {},
            onRetry = {}
        )
    }
}

@Composable
@Preview(
    showBackground = true,
    showSystemUi = true
)
private fun ComposableUpdaterScreenFailedNetworkPreview() {
    FlipperThemeInternal {
        ComposableUpdaterScreen(
            updaterScreenState = UpdaterScreenState.Failed(FailedReason.DOWNLOAD_FROM_NETWORK),
            flipperColor = HardwareColor.BLACK,
            onCancel = {},
            onRetry = {}
        )
    }
}

@Composable
@Preview(
    showBackground = true,
    showSystemUi = true
)
private fun ComposableUpdaterScreenFailedOnFlipperPreview() {
    FlipperThemeInternal {
        ComposableUpdaterScreen(
            updaterScreenState = UpdaterScreenState.Failed(FailedReason.UPLOAD_ON_FLIPPER),
            flipperColor = HardwareColor.BLACK,
            onCancel = {},
            onRetry = {}
        )
    }
}

@Composable
@Preview(
    showBackground = true,
    showSystemUi = true
)
private fun ComposableUpdaterScreenRebootingPreview() {
    FlipperThemeInternal {
        ComposableUpdaterScreen(
            updaterScreenState = UpdaterScreenState.Rebooting,
            flipperColor = HardwareColor.BLACK,
            onCancel = {},
            onRetry = {}
        )
    }
}

@Composable
@Preview(
    showBackground = true,
    showSystemUi = true
)
private fun ComposableUpdaterScreenCancelingUpdatePreview() {
    FlipperThemeInternal {
        ComposableUpdaterScreen(
            updaterScreenState = UpdaterScreenState.CancelingUpdate,
            flipperColor = HardwareColor.BLACK,
            onCancel = {},
            onRetry = {}
        )
    }
}

@Composable
@Preview(
    showBackground = true,
    showSystemUi = true
)
private fun ComposableUpdaterScreenFinishPreview() {
    FlipperThemeInternal {
        ComposableUpdaterScreen(
            updaterScreenState = UpdaterScreenState.Finish,
            flipperColor = HardwareColor.BLACK,
            onCancel = {},
            onRetry = {}
        )
    }
}

private const val changelog = "# TL;DR\r\n\r\n- New Picopass plugin (by Eric Betts, bettse)\r\n- BLE Keyboard improvements (by Michael Marcucci, Cutch)\r\n- GPIO Over RPC (by Samuel Yvon, SamuelYvon)\r\n- NFC: fixes and improvements, better MiFare Classic emulation\r\n- Fbt: fixes and improvements\r\n- RPC: app control protocol\r\n\r\n# Changelog \r\n\r\n- Nfc: fix exit after emulation (#1385)\r\n- Added gui-shift command to ducky script (#1381)\r\n- Save picopass as picopass or, for 26bit, as lfrfid (#1380)\r\n- Namespace loclass library (#1379)\r\n    * Namespace loclass library\r\n    * Lib: const for immutable variables and bss cleanup\r\n- Bluetooth Remote Additions (#1330)\r\n    * Update the HID Keycodes to pull from the library\r\n    * Composite BLE Report Map, add consumer \u0026 mouse HID\r\n    * Add Mouse \u0026 keyboard bt remote, fixed media remote\r\n    * BT Keyboard remove long press shift\r\n    * Fix usb hid modifier keys\r\n    * Fixed misaligned bad usb keys\r\n    * Fix keyboard app keys\r\n    * Partial fix for bt app and linux\r\n    * Update to work across platforms\r\n    * Fix for report ids\r\n    * BtHidApp: move variable from bss to model, cleanup naming.\r\n    * FuriHal: add const to immutable data declaration\r\n- SubGh: fix a race condition  (#1376)\r\n- FL-2612, FL-2618, FL-2619, FL-2622] CLI, threads, notifications, archive fixes (#1354)\r\n    * CLI, notifications, archive fixes\r\n    * Led blink fix\r\n    * Fix thread flags notification index\r\n    * Archive: fix infinite tab switch on empty SD card\r\n- Fix buffer overflow in mifare classic lib #1374\r\n- Added Javacard Emulated mifare classic 1K compatibility (#1369)\r\n    * Add Mifare classic 1k JC handling\r\n      Add mifare classic Javacard emulation handling\r\n    * Adding MIFARE 1K Javacard Emulation Compatibility\r\n      MIFARE Classic 1K Cards from NXP have the SAK value of 0x08.\r\n      MIFARE Classic 1K Cards that are emulated via javacard applet have an SAK value of 0x09.\r\n      Adding the SAK values accordingly so that Javacard emulated mifare classic tags are properly handled.\r\n    * update mifare_common.c\r\n      added javacard emulation handling for mifare classic 1k\r\n- iClass UI (#1366)\r\n    * Move structs to header\r\n    * roll mbedtls into loclass\r\n    * Picopass with scene for reading card\r\n    * Picopass: fix memory leak\r\n    * Lib: return mbedtls back\r\n    * Picopass: rename symbols to match naming guide\r\n- Fbt: compile_commands fixes \u0026 better `latest` directory handling  (#1368)\r\n    * fbt: fixed linking updater as latest build dir for \"flash_usb\"\r\n    * fbt: fixed cdb regeneration logic; refactored build/latest linking logic\r\n    * fbt: docs update\r\n- Storage: lfs fingerprint wasn't updated when both geometry changed \u0026 factory reset was requested, resulting in second re-format on next boot (#1372)\r\n- Plugins: snake: simplification in direction calculation (#1361)\r\n- Fbt: added support for FBT_NO_SYNC environment variable to skip submodule update at start (#1363)\r\n- Fbt: initial blackmagic support (#1362)\r\n    * fbt: added separate script for Windows env setup; moved flash targets from firmware.scons to SConstruct; added Blackmagic support with automatic probe port resolution; added apps.c rebuild on any manifest.fam changes; fixed simultaneous flash \u0026 debug ops\r\n    * fbt: added networked BlackmagicResolver mode; added `get_blackmagic` target for IDE integration\r\n    * fbt: cleanup\r\n    * fbt: docs update; fixed blackmagic lookup on certain usb hubs\r\n    * fbt: removed explicit python serial port import\r\n    * fbt: cleanup\r\n    * fbt: raising exception on multiple serial blackmagic probes\r\n- Add GPIO control through RPC (#1282)\r\n    * Add GPIO control through RPC\r\n    * Assets: sync protobuf to 0.10\r\n    * Assets: update protobuf to fixed v10\r\n- FuriHal: RTC recovery routine and ext3v3 enabled on start (#1357)\r\n    * FuriHal: leave ext3v3 enabled on start\r\n    * FuriHal: RTC recovery routine, cleanup resources\r\n- [FL-2578] Updater fixes related to /int handling (#1359)\r\n    * Updater fixes related to /int handling\r\n      updater: performing factory reset on update, checking for LFS free space before updating, fixed improper error handling on backup/restore operations, rebalanced update stage weights for better progress visuals\r\n      scripts: added CLI output validation for selfupdate.py\r\n      storage: added pointer validation in storage_int_common_fs_info\r\n      desktop: fixed crash on rendering invalid slideshows\r\n    * Typo fix\r\n    * rpc: Updated protobuf to 0.9\r\n    * rpc: removed updater status conversion\r\n- [FL-2589] RPC App control commands (#1350)\r\n    * RPC App control commands\r\n    * Button release timeout\r\n    * SubGhz tx fix\r\n- Nfc: On-device tag generator (#1319)\r\n    * nfc: Add tag generator base\r\n    * nfc: Fix BCC generation\r\n    * nfc: Add MFUL EV1 generators\r\n    * nfc: Fix typos in generator\r\n    * nfc: Add NTAG21x generators\r\n    * nfc: More const\r\n    * nfc: Add NTAG I2C generators\r\n    * nfc: Add field names to generator initializers\r\n    * nfc: Move generators to add manually scene\r\n    * nfc: Revise tag generator UX\r\n    * nfc: Revert add manually menu item name\r\n    * nfc: Remove unused scene start submenu index\r\n- Add iClass keys to source (#1360)\r\n- FL-2610] SubGhz: add keypad lock SubGhz -\u003e Read (#1343)\r\n    * SubGhz: fix multiple clicks on the back button\r\n    * SubGhz: turn on the backlight when receiving with the keypad locked. key processing delay when exiting Locked mode\r\n    * SubGhz: chanage lock variable and enums names\r\n    * SubGhz: replace direct return with consumed\r\n- Improved PR build comment (#1348)\r\n    * Improved PR build comment\r\n    * Update build.yml\r\n- NFC emulation software tunning (#1341)\r\n    * digital_signal: optimize calculationxx\r\n    * firmware: add listen start and listen rx\r\n    * digital signal: rework with fixed point calculation\r\n    * nfc: tune timings\r\n    * nfc: fix array overflow\r\n    * mifare classic: fix key access\r\n    * nfc: rework spi bus access\r\n    * nfc: rework listen mode with st25r3916 calls\r\n    * digital signal: speed up digital_signal_append()\r\n    * digital signal: remove unused profiling\r\n    * nfc: clean up code\r\n    * nfc: correct sleep state\r\n    * nfc: add unit tests\r\n    * nfc: fix memory leak in unit test\r\n    * digital_signal: remove unused code\r\n    * nfc: fix incorrect sak load in pt memory\r\n- PicoPass / iClass (#1298)\r\n    * add mdedtls for des3 implementation\r\n    * add localss from RfidResearchGroup/proxmark3\r\n    * picopass reader app and rfal for communicating with picopass cards\r\n    * always turn off field\r\n    * close storage when keys are not found\r\n    * Add mbedtls as submodule\r\n    * add mbedtl_config\r\n    * Switched to only including specific mbedtls files I need.  Thank you @kevinwallace\r\n    * cherry-pick kevinwallace sconsify\r\n    * scons for mbedtls/loclass\r\n    * Reset to ready state on error\r\n    * unsigned FC/CN\r\n    * clean FC/CN if not decoded\r\n- Fbt: fixes (#1352)\r\n    * fbt: added --git-tasks; fixed typos\r\n    * fbt: fixed --extra-int-apps handling; scripts: moved storage.py \u0026 selfupdate.py to App() framework\r\n    * fbt: changed pseudo-builders to PhonyTargets with commands; added link to latest build dir as build/latest\r\n    * fbt: Restored old ep git handling\r\n    * fbt: dropped git tasks \u0026 dirlink.py\r\n    * fbt: removed extra quoting in fbt.cmd\r\n    * docs: added flash_usb to ReadMe.md\r\n- Add mifare infineon (#1346)\r\n    * Adding MIFARE 1K Infineon Compatibility\r\n      As per Issue #1342,\r\n      MIFARE Classic 1K Cards from NXP have the SAK value of 0x08.\r\n      MIFARE Classic 1K Cards from Infineon have an SAK value of 0x88.\r\n      Adding the SAK values accordingly so that Infineon tags are properly handled.\r\n\r\n# Ongoing\r\n\r\n- Application SDK, loading from SD\r\n- RFID rewrite in C, RAW and new protocols\r\n- New build toolchain" // ktlint-disable max-line-length

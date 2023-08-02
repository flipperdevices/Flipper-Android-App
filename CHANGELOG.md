# Changelog

# 1.6.3 - In progress

- [FIX] Fix 101% issue on update screen
- [FIX] Fix build for fdroid
- [FIX] Fix rpc request/update for 0.64.3 firmware

# 1.6.2

- [FIX] Fix bug with restoreState navigation
- [FIX] Fix problems with large amount of application
- [Feature] Add pull to refresh update for update card
- [Feature] Infrared Emulate (without editing)
- [Feature] Use as key for fap appcard application id
- [Feature] Add install all button
- [REFACTOR] Bump deps

# 1.6.1

- [FIX] FapHub build status layout
- [FIX] Custom install and file manager uploader, deeplink refactor
- [FIX] FapHub build status hide icon instead of info
- [FIX] Firmware outdated fix
- [Feature] Add pull to refresh to fapscreen and category screen
- [Feature] Add "Not Compatible with your Firmware" error
- [Feature] Remove InApp flag in settings

# 1.6.0

- [FIX] Crash application on cancel update
- [FIX] White Screen after import key
- [FIX] Add confirm delete dialog and some small fixes
- [FIX] Hide self updater dialog on click, add self updater in debug mode, remove nfc mfkey32 from
  options
- [FIX] Text alignment for simple device info
- [FIX] Endpoint for fap hub build download
- [FIX] App open metric
- [FIX] Remove kotlin incremental
- [FIX] Faphub search, build status dialogs, placeholders
- [Feature] Open application and screen streaming if install
- [Feature] FapHub MVPv0
- [Feature] Update README with Fdroid badge
- [Feature] Migrate faphub to target flow
- [Feature] Update Geminio for create modules - simple, navigation, api/impl
- [Feature] FapHub manifest support offline mode and add target support for buttons 
- [Feature] Add flipper not connected dialog
- [Feature] Markdown support for fap description and changelog
- [Feature] Infrared Emulate (parse name remotes)
- [Feature] Deeplik on open fap application
- [Feature] Add url to release catalog
- [Feature] Add build status card to app screen
- [Feature] Add report app button
- [Feature] Now the installed apps are even shown offline
- [Feature] Change design for installed tab
- [Feature] Add metric for firmware origin
- [Feature] Add more error catching for FapHub
- [Refactor] Migrate all feature components modules from KSP to Anvil
- [Refactor] Migrate to Ktorfit
- [Refactor] Migrate key emulate to new module, decompose Emulate Helper
- [Refactor] Key Screen state in API and KeyCard with state
- [Refactor] Add detekt property and fix perfomance issue in nfc editor
- [Refactor] Key Parser new module
- [Refactor] Stream for crypto and share key content
- [Refactor] Emulate by config
- [Refactor] Refactor BLE API

# 1.5.1

- [Feature] FapHub api integration (70%) (Network, FAP Manifest, Target, Installation)
- [FIX] Position bubble on key sub-ghz emulate and update hold modifier by new api
- [FIX] Hotfix wearos app and app catalog switch

# 1.5.0

- [Feature] Flipper app loader errors
- [Feature] More smoothly synchronization percent
- [Feature] Skip synchronization if file system on flipper not changed
- [Feature] Pass flipper color from searching screen
- [Feature] Animation on theme changing
- [Feature] Reset tab on double tap
- [Feature] Add back button in options screen
- [Feature] Hide bottombar when keyboard close
- [Feature] New report bug screen
- [Feature] Self update(Google play, Github, FDroid)
- [Feature] Add SAVE_DUMP, MFKEY32, OPEN_NFC_DUMP_EDITOR
- [Feature] Add pull to refresh to full device info
- [Feature] Add report changes to synchronization end event
- [Feature] Add GitHub and Forum link to options
- [Feature] New screen streaming design with animation
- [Feature] Migrate screen streaming to hub
- [Feature] Dialog on lock/unlock unsupported by firmware
- [Feature] Dialog on first pair failed
- [Feature] Add unlock request
- [Feature] New update dialogs
- [Feature] FapHub api integration (10%)
- [GitHub] Update github repo picture
- [CI] Migrate to Detekt
- [CI] Android lint in CI
- [CI] Enable back github queue
- [CI] Migrate to matrix CI with source install
- [CI] Use release build for release
- [CI] Migrate to AGP 8
- [CI] Bump deps
- [FIX] Delete autodisconnect and decrease reconnect time
- [FIX] Disable verbose logging for ble
- [FIX] Clickable modifier ktx
- [FIX] Refactor `share:receive` module
- [FIX] Bump length for server-side share link
- [FIX] Key encryption without paddings
- [FIX] On `save dump as` save only editable key
- [FIX] Remove emulate button on deleted screen
- [FIX] Fix shadow file synchronization
- [FIX] Fix broken synchronization with changes from android
- [FIX] Fix shadow file sharing as file
- [FIX] Firstpair flow open by navigation graph route
- [FIX] Fix crush with inapp storage on startup
- [FIX] Fix change color scheme bug
- [FIX] Fix status bar color
- [FIX] Fix open key screen in global scope
- [FIX] Fix fap hub design
- [FIX] Wait for disconnect while update
- [FIX] Fix mfkey32
- [FIX] Use CurrentActivityHolder for get activity in updater(replace Context cast)
- [FIX] Back on mfkey32 screen
- [FIX] Cash with ulong mapping to string in mfkey32
- [FIX] Move apps from experimental settings to debug
- [FIX] Fix customize button when set widget state
- [FIX] Fix incorrect diff combiner, add KeyDiffCombinerTest
- [FIX] Use lifecycle event for inapp notification
- [FIX] Fix Report bug scroll
- [FIX] InAppNotification update redesign for font scale
- [FIX] Decrease reconnect timeouts and lags detector timeout
- [FIX] Device info mapping keys
- [FIX] Wait disconnecting from device when restart rpc
- [FIX] Replace radio type to sub number in device info
- [FIX] Progress bar on disconnected state on device info screen
- [FIX] Remove proguard rules from submodules, remove minify from submodules
- [FIX] Crash on theme changing
- [REFACTOR] Migrate bottom bar to compose navigation
- [REFACTOR] Bump Android Gradle Plugin
- [REFACTOR] Fix detekt compose issues
- [REFACTOR] Format markdown changelog with clickable link and bold nickname
- [REFACTOR] Enable detekt formatting with rules
- [REFACTOR] Fix gradle warnings (Deprecated API, PredefinedEnumMap)
- [REFACTOR] Replace some clickable to card function + reuse orange app bar
- [REFACTOR] Google Compose Navigation (archive, firstpair, widgets, updater, import, deeplinks)
- [REFACTOR] Migrate to new device info api
- [REFACTOR] Migrate to isSupported in version api
- [REFACTOR] Remove cicerone
- [REFACTOR] Composable Preview Theme without protobuf
- [REFACTOR] Bump deps

# 1.4.1

- [FIX] Updater now respect f7 target

# 1.4.0

- [Feature] Placeholder view on receive key
- [Feature] Add forbidden frequency dialog
- [Feature] Rework all system dialog to custom
- [Feature] New share flow(with `sf#path=()&id=()key=`) + metric + error handling
- [Feature] Share shadow file if that exist on NFC
- [Feature] Add application catalog button
- [Feature] Add application catalog list with sorted button
- [Feature] Add categories, categories list and search
- [Feature] Add installed, new switch, install button
- [Feature] Add support for 3 version of NFC file
- [FIX] Uploading share errors
- [FIX] Display by white color meta data on NFC card
- [FIX] Scrim status bar and design changes on bottom sheet
- [FIX] Fix crash when delete from trash key with favorites
- [FIX] Favorites synchronization
- [CI] Bump deps
- [CI] Fix local build of application
- [CI] Git submodule `Flipper Protobuf`

# 1.3.0

- [REFACTOR] Migrate widgets to WorkManager
- [Feature] Add mfkey32 attack in debug/options + button back
- [Feature] Add HUB tab
- [Feature] Wrap more errors with widget
- [Feature] Add error screen on mfkey32
- [Feature] Not found if key not found in mfkey32
- [Feature] Fix format for user keys
- [Feature] Updater card display update from url
- [Feature] Add screenshot and unlock to screenstreaming
- [Feature] Rework mfkey32 founded key screen
- [Feature] Design fix for widget
- [Feature] Add storage and flipper connected error
- [CI] Auto git submodule update via gradle task
- [FIX] Options button in device screen
- [FIX] WearOS connection
- [FIX] Screen streaming broke
- [FIX] Flipper color on error screen
- [FIX] Fix fatal crash on empty backup
- [FIX] Widget options not working in some android devices
- [FIX] Try to fix uncanceled notification
- [FIX] Move resources from night qualifier

# 1.2.2

- [Feature] Add ability edit uid and zero block
- [Feature] Add support adaptive monochrome icon
- [Feature] Add save as on nfc editor
- [Feature] Add dialog on exit from nfc editor
- [Feature] Update from internal storage and web updater
- [Feature][Suspended] Add widgets
- [Feature] Add delete to FM
- [Feature] Add selection in device info card
- [Feature] Add export keys button
- [REFACTOR] Migrate synchronization task graph to TaskSynchronizationComponent
- [REFACTOR] Redesign card when we waiting update
- [REFACTOR] Get last region from flipper
- [REFACTOR] Storage Stats formatter by kibibyte
- [REFACTOR] Add test for nfc load/save
- [REFACTOR] Bump deps
- [FIX] Rename updater folder
- [FIX] Choose app theme from selected theme, not is light
- [FIX] Check total bytes from server(less 0 mean error from server)
- [FIX] Whitespace in key name and upper first letter in edit screen
- [FIX] Formatter convert bytes to string
- [FIX] Disconnect while connecting
- [FIX] Synchronization status when flipper not connected

# 1.2.1

- [REFACTOR] Change Flipper logo
- [REFACTOR] Redesign Full Device Information
- [REFACTOR] Redesign dialog after update (change colors)
- [REFACTOR] Migrate to faster build configuration
- [REFACTOR] Add test for subghz provisioning
- [REFACTOR] Enable gradle configuration cache
- [REFACTOR] Bump all deps version
- [REFACTOR] Remove bottom bar on key screen
- [REFACTOR] Display error if /int/ full in update progress
- [REFACTOR] Markdown compose bump
- [REFACTOR] New ci with upload wearos app
- [Feature] Add support for NFC Shadow Files
- [Feature] Offer update, if region changes or region file not exit on Flipper
- [Feature] Do not provide SubGhz provisioning on zero hardware region
- [Feature] Restart RPC on fail RPC process response or flipper lags
- [Feature] Prepare for Fdroid app store
- [Feature] Try to intent google assistant command with examples
- [Feature] Handle errors while emulate/send
- [Feature] New emulate/send buttons
- [Feature] New logic for emulate
- [Feature] Bubble for sub-ghz emulate
- [Feature] Add wearos application
- [Feature] Warning color on storage info
- [Feature] Full device info share with storage stats(nothing when storage not found)
- [FIX] Now search for devices by Mac address and by flipper name
- [FIX] Subghz provisioning while geoip is null
- [FIX] Separate screen for edit key
- [FIX] Synchronization with shadow files
- [FIX] Migrate wearos model to protobuf
- [FIX] Double synchronization

# 1.2.0

- [Feature] Upload key-file from file manager
- [Feature] Offer update, if assets not exit on Flipper
- [Feature] Custom hex keyboard
- [Feature] Changelog on updater screen
- [Feature] Add NFC Editor with colors and card
- [Feature] Support Unknown firmware
- [Feature] Compose Navigation on "Info" Screen
- [Feature] Dark Theme(with support switch, splash screen for all android versions)
- [Feature] Add black flipper support (update and info screen)
- [Feature] Compose Theme
- [Feature] Auto disconnect after 5 minutes inactivity
- [Feature] Add outdated application dialog
- [Feature] Add instantly search for flipper if device already connected or bounded
- [Feature] Try auto connect on each app open
- [Feature] Add ability to upload file from file manager
- [Feature] Add ability to download file from file manager
- [Feature] Update RTC on Flipper by Android App
- [Feature] Add ability to edit file from file manager
- [Feature] Add ability to create file from file manager
- [Feature] Add SubGhz provisioning
- [BUGFIX] Use selector in nfc editor
- [BUGFIX] Execute connectIfNotForceDisconnect in FlipperAutoDisconnect
- [BUGFIX] Exit from emulate screen
- [BUGFIX] Respect emulate order
- [BUGFIX] Lock portrait orientation
- [BUGFIX] Update card not shown when flipper not connected
- [BUGFIX] Static URL on dynamic link docs
- [BUGFIX] Fix unsupported version comparison
- [BUGFIX] Fix crash on cancel updating
- [BUGFIX] Fix navigation after updating when phone locked or app not visible
- [BUGFIX] Fix updating image hide after update failed/canceled
- [BUGFIX] Fix padding and typography on information screen
- [BUGFIX] Fix icon color dropdown menu on Deleted keys
- [BUGFIX] Fix emulate when key edited
- [BUGFIX] Nfceditor on little screens
- [BUGFIX] Nfceditor legend card background
- [BUGFIX] Open deeplink on already exist activity
- [BUGFIX] Not display changelog
- [BUGFIX] Changelog support `[text]`
- [BUGFIX] Change background color card in notification
- [BUGFIX] Fix padding on placeholder firmware version
- [BUGFIX] In progress state when retry updater card
- [BUGFIX] Update synchronization status instantly and respect the flag
- [BUGFIX] Rewrite animation for tab state in bottom bar
- [BUGFIX] Upload from 3rd file manager on Android 11+
- [CI] Update deps
- [REFACTOR] Preview (Updater card/screen, Device info)
- [Feature] Button Emulate/Send with auto close
- [REFACTOR] Add cache for gradle build
- [REFACTOR] Migrate FileManager to compose navigation
- [REFACTOR] Changelog with scroll
- [REFACTOR] Change text on updater flipper status
- [REFACTOR] Animation and vibration on emulate/send button

# 1.1.5 - HotFix

- [HOTFIX] Move "Fix unsupported version comparison" to 1.1.4 release

# 1.1.4

- [BUGFIX] Crash on bad protobuf package
- [Feature] Add new server for metrics

# 1.1.3

- [REFACTOR] Split `core:ui` module
- [REFACTOR] Move card from `info:impl` to `updater:card`
- [Feature] New error placeholder in FW Update
- [Feature] Redesign Menu for choose firmware channel
- [Feature] Add Low Battery dialog
- [Feature] Save last open channel for updater
- [Feature] Show when flipper charging
- [Feature] Add metrics (fully anonymously)
- [Feature] Add dialog about sucs/not update
- [Feature] Add failed upload and network dialog
- [Feature] Add synchronization percent in archive screen
- [Feature] Change percent scheme for synchronization
- [Feature] Placeholder on connecting device status
- [BUGFIX] Fix incorrect corner radius for battery icon
- [BUGFIX] Fix searching pic
- [BUGFIX] Fix padding for updater card
- [BUGFIX] Fix update button corner angel
- [BUGFIX] Fix placeholder state in info and updater card
- [BUGFIX] Fix ripple effect on firmware version choose
- [CI] Pass countly prod creds in application when building
- [CI] Dump version_code and version_name to apk-version.properties

# 1.1.2

- [BUGFIX] Fix crash with custom firmware version
- [BUGFIX] Fix crash when key file not exist
- [BUGFIX] Ignore file in synchronization which start with dots
- [Feature] Add image for updater on Flipper
- [Feature] Redesign Settings

# 1.1.1

- [Feature] Add rebooting now dialog and state
- [BUGFIX] Version comparison in updater
- [BUGFIX] Fix text typos in dialog updates

# 1.1

- [BUGFIX] Concurrent exception in request api
- [BUGFIX] Remove bottom divider from Bottom Bar
- [BUGFIX] Now we can cancel editing key
- [BUGFIX] Fix reconnect issues
- [BUGFIX] Fix synchronization start after first login
- [BUGFIX] Fix wording in the settings menu
- [Feature] Add report bug button
- [Feature] Emulate button on key screen
- [Feature] Not not upload file if already exist
- [Feature] Add update Flipper via OTA on separate screen
- [Feature] Added an option to cancel existing Flipper RPC requests
- [Feature] Use new design for unsupported card and for unsupported dialog
- [Feature] Restart connection if flipper not respond 30 seconds
- [Feature] Now we can disconnect instantly, without waiting next retry
- [Feature] Add disconnect button and improve reconnect
- [Feature] Now you can delete or restore all keys from trash
- [Feature] Add recover support to deleted keys (on keyscreen)
- [Feature] Compose Navigation in "Options"
- [CI] Now we check that CHANGELOG.md changed
- [CI] JVM protobuf library

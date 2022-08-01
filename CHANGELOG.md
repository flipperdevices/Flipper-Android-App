# Changelog

# 1.1.6 - In progress

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
- [BUGFIX] Exit from emulate screen
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
- [CI] Update deps
- [REFACTOR] Preview (Updater card/screen, Device info)
- [Feature] Button Emulate/Send with auto close
- [REFACTOR] Add cache for gradle build
- [REFACTOR] Migrate FileManager to compose navigation

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

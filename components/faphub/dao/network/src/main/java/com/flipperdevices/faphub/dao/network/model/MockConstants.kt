package com.flipperdevices.faphub.dao.network.model

import com.flipperdevices.core.data.SemVer
import com.flipperdevices.faphub.dao.api.model.FapCategory
import com.flipperdevices.faphub.dao.api.model.FapDeveloperInformation
import com.flipperdevices.faphub.dao.api.model.FapItem
import com.flipperdevices.faphub.dao.api.model.FapMetaInformation
import java.util.UUID

@Suppress("MagicNumber")
internal object MockConstants {
    const val MOCK_DELAY = 3000L
    private const val MOCK_APP_LOGO_URL = "https://minecraft.glitchless.ru/tmp/app_logo.png"
    const val MOCK_CATEGORY_NAME = "Games"
    const val MOCK_CATEGORY_LOGO_URL =
        "https://minecraft.glitchless.ru/tmp/category_icon.png"
    private const val MOCK_NAME = "Snake Game"
    private const val MOCK_DESCRIPTION =
        "Press the control buttons to move the snake around the board. " +
                "As the snake finds food, it eats the food, and thereby grows larger. " +
                "The game ends when the snake either moves off the screen or moves into itself. " +
                "The goal is to make the snake as large as possible before that happens."
    private const val MOCK_CHANGELOG = """
- [Feature] Add forbidden frequency dialog
- [Feature] Rework all system dialog to custom
- [Feature] New share flow(with `sf#path=()&id=()key=`)
- [Feature] Share shadow file if that exist on NFC
- [Feature] Add application catalog button
- [Feature] Add application catalog list with sorted button
- [Feature] Add categories, categories list and search
- [FIX] Uploading share errors
- [FIX] Display by white color meta data on NFC card
- [FIX] Scrim status bar and design changes on bottom sheet
    """
    private const val MOCK_SCREENSHOT_URL =
        "https://minecraft.glitchless.ru/tmp/flipper-screenshot.png"
    private val MOCK_SCREENSHOTS = MutableList(6) { MOCK_SCREENSHOT_URL }
    private val MOCK_META_INFORMATION = FapMetaInformation(
        version = SemVer(1, 0, 0),
        sizeBytes = 1024 * 100,
        apiVersion = SemVer(2, 2)
    )
    private val MOCK_DEVELOPER_INFORMATION = FapDeveloperInformation(
        githubRepositoryLink = "https://github.com/flipperdevices/Flipper-Android-App",
        manifestRepositoryLink = "https://github.com/flipperdevices/Flipper-Android-App/blob/dev/README.md"
    )

    fun getMockItem() = FapItem(
        id = UUID.randomUUID().toString(),
        picUrl = MOCK_APP_LOGO_URL,
        description = MOCK_DESCRIPTION,
        name = MOCK_NAME,
        category = FapCategory(
            name = MOCK_CATEGORY_NAME,
            picUrl = MOCK_CATEGORY_LOGO_URL
        ),
        screenshots = MOCK_SCREENSHOTS,
        metaInformation = MOCK_META_INFORMATION,
        changelog = MOCK_CHANGELOG,
        fapDeveloperInformation = MOCK_DEVELOPER_INFORMATION
    )
}

package com.flipperdevices.faphub.dao.network.model

import com.flipperdevices.faphub.dao.api.model.FapCategory
import com.flipperdevices.faphub.dao.api.model.FapItem

internal object MockConstants {
    const val MOCK_DELAY = 3000L
    const val MOCK_APP_LOGO_URL = "https://minecraft.glitchless.ru/tmp/app_logo.png"
    const val MOCK_CATEGORY_NAME = "Games"
    const val MOCK_CATEGORY_LOGO_URL = "https://minecraft.glitchless.ru/tmp/category_icon.png"
    const val MOCK_NAME = "Snake Game"
    const val MOCK_DESCRIPTION = "Press the control buttons to move the snake around the board. " +
            "As the snake finds food, it eats the food, and thereby grows larger. " +
            "The game ends when the snake either moves off the screen or moves into itself. " +
            "The goal is to make the snake as large as possible before that happens."
    private const val MOCK_SCREENSHOT_URL =
        "https://minecraft.glitchless.ru/tmp/flipper-screenshot.png"
    private val MOCK_SCREENSHOTS = MutableList(6) { MOCK_SCREENSHOT_URL }
    val MOCK_FAP_ITEM = FapItem(
        picUrl = MOCK_APP_LOGO_URL,
        description = MOCK_DESCRIPTION,
        name = MOCK_NAME,
        category = FapCategory(
            name = MOCK_CATEGORY_NAME,
            picUrl = MOCK_CATEGORY_LOGO_URL
        ),
        screenshots = MOCK_SCREENSHOTS
    )
}

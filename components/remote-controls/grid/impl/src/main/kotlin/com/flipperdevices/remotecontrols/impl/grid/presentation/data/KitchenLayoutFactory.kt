@file:Suppress("MagicNumber")

package com.flipperdevices.remotecontrols.impl.grid.presentation.data

import com.flipperdevices.ifrmvp.core.ui.util.GridConstants
import com.flipperdevices.ifrmvp.model.IfrButton
import com.flipperdevices.ifrmvp.model.IfrKeyIdentifier
import com.flipperdevices.ifrmvp.model.PageLayout
import com.flipperdevices.ifrmvp.model.PagesLayout
import com.flipperdevices.ifrmvp.model.buttondata.Base64ImageButtonData
import com.flipperdevices.ifrmvp.model.buttondata.ChannelButtonData
import com.flipperdevices.ifrmvp.model.buttondata.IconButtonData
import com.flipperdevices.ifrmvp.model.buttondata.NavigationButtonData
import com.flipperdevices.ifrmvp.model.buttondata.TextButtonData
import com.flipperdevices.ifrmvp.model.buttondata.VolumeButtonData

object KitchenLayoutFactory {
    const val FILE_NAME: String = "KitchenLayout"

    @Suppress("LongMethod")
    fun create(): PagesLayout {
        return PagesLayout(
            pages = buildList {
                PageLayout(
                    buttons = buildList {
                        // Sample Text Button
                        IfrButton(
                            data = IconButtonData(
                                keyIdentifier = IfrKeyIdentifier.Sha256(
                                    name = "power",
                                    sha256String = "60d18bb96f05eee3bde60a0c3f87b13f74b0c4d3d934d659ef7738f415881740"
                                ),
                                iconId = IconButtonData.IconType.POWER
                            ),
                            position = IfrButton.Position(
                                x = 1,
                                y = 0,
                                zIndex = 10f,
                                alignment = IfrButton.Alignment.CENTER,
                                containerWidth = 2,
                            ),
                        ).run(::add)
                        // First
                        IfrButton(
                            data = TextButtonData(
                                keyIdentifier = IfrKeyIdentifier.Sha256(
                                    name = "power",
                                    sha256String = "60d18bb96f05eee3bde60a0c3f87b13f74b0c4d3d934d659ef7738f415881740"
                                ),
                                text = "PWR"
                            ),
                            position = IfrButton.Position(0, 0)
                        ).run(::add)
                        IfrButton(
                            data = TextButtonData(
                                keyIdentifier = IfrKeyIdentifier.Sha256(
                                    name = "menu",
                                    sha256String = "60d18bb96f05eee3bde60a0c3f87b13f74b0c4d3d934d659ef7738f415881740"
                                ),
                                text = "MENU"
                            ),
                            position = IfrButton.Position(0, 2)
                        ).run(::add)
                        IfrButton(
                            data = Base64ImageButtonData(
                                keyIdentifier = IfrKeyIdentifier.Sha256(
                                    name = "power",
                                    sha256String = "60d18bb96f05eee3bde60a0c3f87b13f74b0c4d3d934d659ef7738f415881740"
                                ),
                                pngBase64 = GridConstants.TEMP_BASE64_IMAGE
                            ),
                            position = IfrButton.Position(2, 0, zIndex = 10f)
                        ).run(::add)
                        IfrButton(
                            data = TextButtonData(
                                keyIdentifier = IfrKeyIdentifier.Sha256(
                                    name = "tv_av",
                                    sha256String = "60d18bb96f05eee3bde60a0c3f87b13f74b0c4d3d934d659ef7738f415881740"
                                ),
                                text = "TV/AV"
                            ),
                            position = IfrButton.Position(0, 4)
                        ).run(::add)
                        // Second
                        IfrButton(
                            data = IconButtonData(
                                keyIdentifier = IfrKeyIdentifier.Sha256(
                                    name = "info",
                                    sha256String = "60d18bb96f05eee3bde60a0c3f87b13f74b0c4d3d934d659ef7738f415881740"
                                ),
                                iconId = IconButtonData.IconType.INFO
                            ),
                            position = IfrButton.Position(1, 0)
                        ).run(::add)
                        IfrButton(
                            data = IconButtonData(
                                keyIdentifier = IfrKeyIdentifier.Sha256(
                                    name = "hm",
                                    sha256String = "60d18bb96f05eee3bde60a0c3f87b13f74b0c4d3d934d659ef7738f415881740"
                                ),
                                iconId = IconButtonData.IconType.HOME
                            ),
                            position = IfrButton.Position(1, 1)
                        ).run(::add)
                        IfrButton(
                            data = IconButtonData(
                                keyIdentifier = IfrKeyIdentifier.Sha256(
                                    name = "back",
                                    sha256String = "60d18bb96f05eee3bde60a0c3f87b13f74b0c4d3d934d659ef7738f415881740"
                                ),
                                iconId = IconButtonData.IconType.BACK
                            ),
                            position = IfrButton.Position(1, 3)
                        ).run(::add)
                        IfrButton(
                            data = IconButtonData(
                                keyIdentifier = IfrKeyIdentifier.Sha256(
                                    name = "more",
                                    sha256String = "60d18bb96f05eee3bde60a0c3f87b13f74b0c4d3d934d659ef7738f415881740"
                                ),
                                iconId = IconButtonData.IconType.MORE
                            ),
                            position = IfrButton.Position(1, 4)
                        ).run(::add)
                        // Nav
                        IfrButton(
                            data = NavigationButtonData(
                                upKeyIdentifier = IfrKeyIdentifier.Sha256(
                                    name = "up",
                                    sha256String = "60d18bb96f05eee3bde60a0c3f87b13f74b0c4d3d934d659ef7738f415881740"
                                ),
                                leftKeyIdentifier = IfrKeyIdentifier.Sha256(
                                    name = "left",
                                    sha256String = "60d18bb96f05eee3bde60a0c3f87b13f74b0c4d3d934d659ef7738f415881740"
                                ),
                                downKeyIdentifier = IfrKeyIdentifier.Sha256(
                                    name = "down",
                                    sha256String = "60d18bb96f05eee3bde60a0c3f87b13f74b0c4d3d934d659ef7738f415881740"
                                ),
                                rightKeyIdentifier = IfrKeyIdentifier.Sha256(
                                    name = "right",
                                    sha256String = "60d18bb96f05eee3bde60a0c3f87b13f74b0c4d3d934d659ef7738f415881740"
                                ),
                                okKeyIdentifier = IfrKeyIdentifier.Sha256(
                                    name = "apply",
                                    sha256String = "60d18bb96f05eee3bde60a0c3f87b13f74b0c4d3d934d659ef7738f415881740"
                                )
                            ),
                            position = IfrButton.Position(
                                y = 3,
                                x = 1,
                                containerWidth = 3,
                                containerHeight = 3
                            ),

                        ).run(::add)
                        // Channel Volume
                        IfrButton(
                            data = ChannelButtonData(
                                addKeyIdentifier = IfrKeyIdentifier.Sha256(
                                    name = "ch+",
                                    sha256String = "60d18bb96f05eee3bde60a0c3f87b13f74b0c4d3d934d659ef7738f415881740"
                                ),
                                reduceKeyIdentifier = IfrKeyIdentifier.Sha256(
                                    name = "ch-",
                                    sha256String = "60d18bb96f05eee3bde60a0c3f87b13f74b0c4d3d934d659ef7738f415881740"
                                ),
                            ),
                            position = IfrButton.Position(
                                y = 7,
                                x = 0,
                                containerWidth = 1,
                                containerHeight = 3
                            ),

                        ).run(::add)
                        IfrButton(
                            data = VolumeButtonData(
                                addKeyIdentifier = IfrKeyIdentifier.Sha256(
                                    name = "v+",
                                    sha256String = "60d18bb96f05eee3bde60a0c3f87b13f74b0c4d3d934d659ef7738f415881740"
                                ),
                                reduceKeyIdentifier = IfrKeyIdentifier.Sha256(
                                    name = "v-",
                                    sha256String = "60d18bb96f05eee3bde60a0c3f87b13f74b0c4d3d934d659ef7738f415881740"
                                )
                            ),
                            position = IfrButton.Position(
                                y = 7,
                                x = 4,
                                containerWidth = 1,
                                containerHeight = 3
                            ),

                        ).run(::add)
                        // Bottom Button
                        IfrButton(
                            data = TextButtonData(
                                keyIdentifier = IfrKeyIdentifier.Sha256(
                                    name = "123",
                                    sha256String = "60d18bb96f05eee3bde60a0c3f87b13f74b0c4d3d934d659ef7738f415881740"
                                ),
                                text = "123"
                            ),
                            position = IfrButton.Position(10, 0)
                        ).run(::add)
                        IfrButton(
                            data = IconButtonData(
                                keyIdentifier = IfrKeyIdentifier.Sha256(
                                    name = "sound_toggle",
                                    sha256String = "60d18bb96f05eee3bde60a0c3f87b13f74b0c4d3d934d659ef7738f415881740"
                                ),
                                iconId = IconButtonData.IconType.MUTE
                            ),
                            position = IfrButton.Position(10, 4)
                        ).run(::add)
                    }
                ).run(::add)
            }
        )
    }
}

package com.flipperdevices.core.ui.dialog.composable.multichoice

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Stable
@Suppress("TooManyFunctions")
class FlipperMultiChoiceDialogModel private constructor(
    val imageComposable: (@Composable () -> Unit)?,
    val titleComposable: (@Composable () -> Unit)?,
    val textComposable: (@Composable () -> Unit)?,
    val onDismissRequest: (() -> Unit)?,
    val closeOnClickOutside: Boolean,
    val buttonComposables: List<(@Composable () -> Unit)>
) {
    class Builder {
        private var imageComposable: (@Composable () -> Unit)? = null
        private var titleComposable: (@Composable () -> Unit)? = null
        private var textComposable: (@Composable () -> Unit)? = null
        private var onDismissRequestInternal: (() -> Unit)? = null
        private var closeOnClickOutside: Boolean = true
        private val buttonComposables: MutableList<(@Composable () -> Unit)> = mutableListOf()

        fun setTitle(resource: StringResource): Builder {
            titleComposable = {
                ComposableTitle(stringResource(resource))
            }
            return this
        }

        fun setTitle(text: String): Builder {
            titleComposable = {
                ComposableTitle(text)
            }
            return this
        }

        fun setTitle(composableText: @Composable () -> String): Builder {
            titleComposable = {
                ComposableTitle(composableText.invoke())
            }
            return this
        }

        fun setCloseOnClickOutside(closeOnClickOutside: Boolean): Builder {
            this.closeOnClickOutside = closeOnClickOutside
            return this
        }

        fun setDescription(resource: StringResource): Builder {
            textComposable = { ComposableDescription(AnnotatedString(stringResource(resource))) }
            return this
        }

        fun setDescription(text: AnnotatedString): Builder {
            textComposable = { ComposableDescription(text) }
            return this
        }

        @JvmName("setDescriptionComposableText")
        fun setDescription(composableText: @Composable () -> String): Builder {
            textComposable = { ComposableDescription(AnnotatedString(composableText.invoke())) }
            return this
        }

        @JvmName("setDescriptionComposableContent")
        fun setDescription(content: @Composable () -> Unit): Builder {
            textComposable = content
            return this
        }

        fun setImage(content: @Composable () -> Unit): Builder {
            imageComposable = content
            return this
        }

        @Composable
        private fun ComposableTitle(text: String) {
            Text(
                text = text,
                style = LocalTypography.current.bodySSB14,
                textAlign = TextAlign.Center,
                color = LocalPallet.current.text100
            )
        }

        @Composable
        private fun ComposableDescription(text: AnnotatedString) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = text,
                style = LocalTypography.current.bodyR14,
                textAlign = TextAlign.Center,
                color = LocalPallet.current.text40
            )
        }

        fun setOnDismissRequest(onDismissRequest: () -> Unit): Builder {
            onDismissRequestInternal = onDismissRequest
            return this
        }

        fun addButton(
            resource: StringResource,
            onClick: () -> Unit,
            isActive: Boolean = false
        ): Builder {
            buttonComposables.add {
                ComposableFlipperFlatButton(
                    stringResource(resource),
                    onClick,
                    if (isActive) LocalPallet.current.accentSecond else null
                )
            }
            return this
        }

        fun addButton(
            textComposable: @Composable () -> String,
            onClick: () -> Unit,
            isActive: Boolean = false
        ): Builder {
            buttonComposables.add {
                ComposableFlipperFlatButton(
                    textComposable.invoke(),
                    onClick,
                    if (isActive) LocalPallet.current.accentSecond else null
                )
            }
            return this
        }

        fun addButton(
            text: String,
            onClick: () -> Unit,
            isActive: Boolean = false
        ): Builder {
            buttonComposables.add {
                ComposableFlipperFlatButton(
                    text,
                    onClick,
                    if (isActive) LocalPallet.current.accentSecond else null
                )
            }
            return this
        }

        fun addButton(
            resource: StringResource,
            onClick: () -> Unit,
            textColor: Color
        ): Builder {
            buttonComposables.add {
                ComposableFlipperFlatButton(
                    stringResource(resource),
                    onClick,
                    textColor
                )
            }
            return this
        }

        fun addButton(
            textComposable: @Composable () -> String,
            onClick: () -> Unit,
            textColor: Color
        ): Builder {
            buttonComposables.add {
                ComposableFlipperFlatButton(
                    textComposable.invoke(),
                    onClick,
                    textColor
                )
            }
            return this
        }

        fun addButton(
            text: String,
            onClick: () -> Unit,
            textColor: Color
        ): Builder {
            buttonComposables.add {
                ComposableFlipperFlatButton(
                    text,
                    onClick,
                    textColor
                )
            }
            return this
        }

        fun build(): FlipperMultiChoiceDialogModel = FlipperMultiChoiceDialogModel(
            imageComposable,
            titleComposable,
            textComposable,
            onDismissRequestInternal,
            closeOnClickOutside,
            buttonComposables
        )
    }
}

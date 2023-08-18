package com.flipperdevices.core.ui.dialog.composable.multichoice

import androidx.annotation.StringRes
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography

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

        fun setTitle(@StringRes textId: Int): Builder {
            titleComposable = {
                Text(
                    text = stringResource(textId),
                    style = LocalTypography.current.bodySSB14,
                    textAlign = TextAlign.Center,
                    color = LocalPallet.current.text100
                )
            }
            return this
        }

        fun setDescription(@StringRes textId: Int): Builder {
            textComposable = { ComposableDescription(AnnotatedString(stringResource(textId))) }
            return this
        }

        fun setDescription(text: AnnotatedString): Builder {
            textComposable = { ComposableDescription(text) }
            return this
        }

        fun setDescription(content: @Composable () -> Unit): Builder {
            textComposable = content
            return this
        }

        fun setImage(content: @Composable () -> Unit): Builder {
            imageComposable = content
            return this
        }

        @Composable
        private fun ComposableDescription(text: AnnotatedString) {
            Text(
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
            @StringRes textId: Int,
            onClick: () -> Unit,
            isActive: Boolean = false
        ): Builder {
            buttonComposables.add {
                ComposableFlipperFlatButton(
                    stringResource(textId),
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
            @StringRes textId: Int,
            onClick: () -> Unit,
            textColor: Color
        ): Builder {
            buttonComposables.add {
                ComposableFlipperFlatButton(
                    stringResource(textId),
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

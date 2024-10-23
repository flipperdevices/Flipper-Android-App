package com.flipperdevices.remotecontrols.impl.setup.composable.components

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterExitState
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.flipperdevices.core.ui.ktx.clickableRipple
import com.flipperdevices.core.ui.ktx.elements.ComposableFlipperButton
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.core.ui.theme.LocalPalletV2
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.ifrmvp.backend.model.SignalResponse
import com.flipperdevices.remotecontrols.setup.impl.R as SetupR

@Suppress("LongMethod")
@Composable
fun ConfirmContent(
    text: String,
    onPositiveClick: () -> Unit,
    onNegativeClick: () -> Unit,
    onSkipClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
            .background(LocalPalletV2.current.surface.sheet.body.default),
        content = {
            Column(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .width(36.dp)
                        .height(4.dp)
                        .clip(CircleShape)
                        .background(LocalPalletV2.current.surface.contentCard.separator.default)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = text,
                    style = LocalTypography.current.titleB18,
                    color = LocalPalletV2.current.text.title.primary,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 38.dp)
                        .padding(top = 42.dp)
                ) {
                    Text(
                        text = stringResource(SetupR.string.no),
                        style = LocalTypography.current.buttonB16,
                        color = LocalPalletV2.current.action.blue.text.default,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .clip(RoundedCornerShape(30.dp))
                            .clickableRipple(onClick = onNegativeClick)
                            .padding(vertical = 12.dp, horizontal = 36.dp),
                    )
                    ComposableFlipperButton(
                        text = stringResource(SetupR.string.yes),
                        onClick = onPositiveClick
                    )
                }
                Text(
                    text = stringResource(SetupR.string.skip),
                    style = LocalTypography.current.buttonB16,
                    color = LocalPalletV2.current.action.blue.text.default,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .clip(RoundedCornerShape(30.dp))
                        .clickableRipple(onClick = onSkipClick)
                        .padding(vertical = 18.dp, horizontal = 36.dp)
                        .padding(bottom = 22.dp),
                )
            }
        }
    )
}

@Composable
fun AnimatedConfirmContent(
    lastEmulatedSignal: SignalResponse?,
    onNegativeClick: () -> Unit,
    onSuccessClick: () -> Unit,
    onSkipClick: () -> Unit,
    onDismissConfirm: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.BottomCenter
    ) {
        val transition = updateTransition(
            targetState = lastEmulatedSignal,
            label = lastEmulatedSignal?.signalModel?.id?.toString()
        )
        if (transition.targetState != null || transition.currentState != null || transition.isRunning) {
            Dialog(
                onDismissRequest = onDismissConfirm,
                properties = DialogProperties(
                    usePlatformDefaultWidth = false,
                    decorFitsSystemWindows = false,
                    dismissOnClickOutside = true,
                    dismissOnBackPress = true
                )
            ) {
                transition.AnimatedVisibility(
                    visible = { localLastEmulatedSignal -> localLastEmulatedSignal != null },
                    enter = slideInVertically(initialOffsetY = { it / 2 }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { it / 2 }) + fadeOut(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        // This is required to close dialog when "clicking outside of borders"
                        // The dialog displayed at bottom, therefore, the dialog itself
                        // fills entire screen to display content at bottom
                        .clickable(onClick = onDismissConfirm),
                ) {
                    val contentState = when (this.transition.targetState) {
                        EnterExitState.Visible -> transition.targetState
                        else -> transition.currentState
                    }

                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        ConfirmContent(
                            text = when (contentState) {
                                null -> ""
                                else -> contentState.message
                            },
                            onNegativeClick = onNegativeClick,
                            onPositiveClick = onSuccessClick,
                            onSkipClick = onSkipClick,
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                // Redefine clickable listener to not close dialog when clicking on dialog content
                                .clickable { }
                        )
                    }
                }
            }
        }
    }
}

@Preview(
    showSystemUi = true,
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
private fun ComposableConfirmContentLightPreview() {
    FlipperThemeInternal {
        ConfirmContent(
            text = "Super mega text of preview confirm element",
            onPositiveClick = {},
            onNegativeClick = {},
            onSkipClick = {}
        )
    }
}

@Preview(
    showSystemUi = true,
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun ComposableConfirmContentDarkPreview() {
    FlipperThemeInternal {
        ConfirmContent(
            text = "Super mega text of preview confirm element",
            onPositiveClick = {},
            onNegativeClick = {},
            onSkipClick = {}
        )
    }
}

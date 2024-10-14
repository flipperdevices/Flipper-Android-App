package com.flipperdevices.settings.impl.composable.category

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.dialog.composable.FlipperDialogAndroid
import com.flipperdevices.core.ui.ktx.clickableRipple
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.settings.impl.R
import com.flipperdevices.settings.impl.composable.components.SimpleElement
import com.flipperdevices.settings.impl.viewmodels.VersionViewModel

private const val EXPERT_MODE_CLICK_COUNT = 10

@Composable
internal fun VersionCategory(
    versionViewModel: VersionViewModel,
    modifier: Modifier = Modifier,
    onActivateExpertMode: () -> Unit
) {
    val inProgress by versionViewModel.inProgress().collectAsState()
    val dialogState by versionViewModel.getDialogState().collectAsState()

    val version = versionViewModel.versionApp()
    val sourceInstall = versionViewModel.sourceInstall()
    val isSelfUpdateTypeCanCheck = versionViewModel.isSelfUpdateManualChecked().not()

    var howMuchClick by remember { mutableIntStateOf(0) }

    SelfUpdaterNoUpdatesDialog(state = dialogState, onClose = versionViewModel::dismissDialog)

    CardCategory(
        modifier = modifier.padding(bottom = 14.dp)
    ) {
        Row(
            modifier = Modifier
                .clickable {
                    howMuchClick++
                    if (howMuchClick > EXPERT_MODE_CLICK_COUNT) {
                        onActivateExpertMode()
                    }
                }
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(R.drawable.pic_app_logo),
                contentDescription = null
            )
            SimpleElement(
                modifier = Modifier.weight(weight = 1f),
                title = stringResource(id = R.string.check_updates_text),
                description = stringResource(id = R.string.check_updates_desc, sourceInstall, version),
                titleTextStyle = LocalTypography.current.bodyR14
            )

            if (isSelfUpdateTypeCanCheck) {
                return@CardCategory
            }

            if (inProgress) {
                CircularProgressIndicator(
                    modifier = Modifier.size(size = 20.dp),
                    strokeWidth = 2.dp,
                    color = LocalPallet.current.accentSecond
                )
            } else {
                Text(
                    modifier = Modifier
                        .clickableRipple(onClick = versionViewModel::onCheckUpdates),
                    text = stringResource(id = R.string.check_updates_button),
                    style = LocalTypography.current.subtitleM12,
                    color = LocalPallet.current.accentSecond
                )
            }
        }
    }
}

@Composable
private fun SelfUpdaterNoUpdatesDialog(
    state: Boolean,
    onClose: () -> Unit
) {
    if (!state) return

    val imageId = if (MaterialTheme.colors.isLight) {
        R.drawable.pic_self_updates_not_found
    } else {
        R.drawable.pic_self_updates_not_found_dark
    }

    FlipperDialogAndroid(
        buttonTextId = R.string.check_no_updates_button,
        imageId = imageId,
        titleId = R.string.check_no_updates_title,
        textId = R.string.check_no_updates_text,
        onClickButton = onClose,
        onDismissRequest = onClose
    )
}

package com.flipperdevices.settings.impl.composable.category

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.preference.pb.SelectedTheme
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.notification.model.UpdateNotificationState
import com.flipperdevices.settings.impl.R
import com.flipperdevices.settings.impl.composable.components.GrayDivider
import com.flipperdevices.settings.impl.composable.components.SwitchableElement
import com.flipperdevices.settings.impl.composable.elements.PushNotificationElement
import com.flipperdevices.settings.impl.composable.elements.ThemeChangerElement

@Composable
fun AppCategory(
    theme: SelectedTheme,
    onSelectTheme: (SelectedTheme) -> Unit,
    notificationState: UpdateNotificationState,
    onChangeNotificationState: (Boolean) -> Unit,
    vibrationState: Boolean,
    onSwitchVibration: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    CardCategory(modifier = modifier) {
        Text(
            modifier = Modifier.padding(all = 12.dp),
            text = stringResource(R.string.app_title),
            style = LocalTypography.current.buttonB16
        )
        ThemeChangerElement(
            theme = theme,
            onSelectTheme = onSelectTheme
        )
        GrayDivider()
        PushNotificationElement(
            notificationState = notificationState,
            onChangeNotificationState = onChangeNotificationState
        )
        GrayDivider()
        SwitchableElement(
            state = vibrationState,
            titleId = R.string.app_vibration_title,
            descriptionId = R.string.app_vibration_desc,
            onSwitchState = onSwitchVibration
        )
    }
}

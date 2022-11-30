package com.flipperdevices.faphub.catalogtab.impl.composable.faps

import com.flipperdevices.core.ui.res.R as DesignSystem
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.faphub.catalogtab.impl.R
import com.flipperdevices.faphub.catalogtab.impl.viewmodel.FapsListViewModel
import com.flipperdevices.faphub.dao.api.model.SortType

@Composable
fun ComposableFapsListTitle(
    fapsListViewModel: FapsListViewModel
) = Row(
    modifier = Modifier
        .fillMaxWidth()
        .padding(start = 14.dp, end = 14.dp, top = 20.dp),
    verticalAlignment = Alignment.CenterVertically
) {
    Text(
        modifier = Modifier
            .weight(1f),
        text = stringResource(R.string.faphub_catalog_title),
        style = LocalTypography.current.titleB18,
        color = LocalPallet.current.text100
    )

    ComposableFapsChoice(fapsListViewModel)
}

@Composable
private fun ComposableFapsChoice(
    fapsListViewModel: FapsListViewModel
) {
    val sortedType by fapsListViewModel.getSortTypeFlow().collectAsState()
    var choiceDialogOpen by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .border(
                BorderStroke(1.dp, LocalPallet.current.fapHubDividerColor),
                RoundedCornerShape(16.dp)
            )
            .clickable(
                interactionSource = MutableInteractionSource(),
                indication = rememberRipple(),
                onClick = {
                    choiceDialogOpen = true
                }
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val textId = when (sortedType) {
            SortType.UPDATED -> R.string.faphub_catalog_choice_updated
            SortType.PUBLISHED -> R.string.faphub_catalog_choice_published
        }
        Text(
            modifier = Modifier.padding(top = 5.dp, bottom = 5.dp, start = 12.dp),
            text = stringResource(textId),
            style = LocalTypography.current.subtitleM12,
            color = LocalPallet.current.fapHubSortedColor
        )
        Icon(
            modifier = Modifier
                .padding(start = 4.dp, end = 12.dp)
                .size(12.dp),
            painter = painterResource(DesignSystem.drawable.ic_down),
            contentDescription = null,
            tint = LocalPallet.current.fapHubSortedColor
        )
        ComposableDropDown(
            isDialogOpen = choiceDialogOpen,
            onCloseDialog = { choiceDialogOpen = false },
            onSelectSortType = { fapsListViewModel.onSelectSortType(it) }
        )
    }
}

@Composable
private fun ComposableDropDown(
    isDialogOpen: Boolean,
    onCloseDialog: () -> Unit,
    onSelectSortType: (SortType) -> Unit
) {
    DropdownMenu(
        expanded = isDialogOpen,
        onDismissRequest = onCloseDialog
    ) {
        DropdownMenuItem(onClick = {
            onSelectSortType(SortType.UPDATED)
            onCloseDialog()
        }) {
            Text(
                text = stringResource(R.string.faphub_catalog_choice_updated),
                style = LocalTypography.current.bodyR14,
                color = LocalPallet.current.text100
            )
        }
        DropdownMenuItem(onClick = {
            onSelectSortType(SortType.PUBLISHED)
            onCloseDialog()
        }) {
            Text(
                text = stringResource(R.string.faphub_catalog_choice_published),
                style = LocalTypography.current.bodyR14,
                color = LocalPallet.current.text100
            )
        }
    }
}

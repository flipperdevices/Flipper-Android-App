package com.flipperdevices.faphub.appcard.composable.paging

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
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
import com.flipperdevices.core.ui.ktx.clickableRipple
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.faphub.appcard.composable.R
import com.flipperdevices.faphub.dao.api.model.SortType
import com.flipperdevices.core.ui.res.R as DesignSystem

@Composable
fun ComposableSortChoice(
    title: String?,
    sortType: SortType,
    onSelectSortType: (SortType) -> Unit,
    modifier: Modifier = Modifier
) = Row(
    modifier = modifier
        .fillMaxWidth()
        .padding(start = 14.dp, end = 14.dp, top = 20.dp),
    verticalAlignment = Alignment.CenterVertically
) {
    if (title != null) {
        Text(
            modifier = Modifier
                .weight(1f),
            text = title,
            style = LocalTypography.current.titleB18,
            color = LocalPallet.current.text100
        )
    } else {
        Spacer(Modifier.weight(1f))
    }

    ComposableFapsChoice(sortType, onSelectSortType)
}

@Composable
private fun ComposableFapsChoice(
    sortType: SortType,
    onSelectSortType: (SortType) -> Unit
) {
    var choiceDialogOpen by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .border(
                BorderStroke(1.dp, LocalPallet.current.fapHubDividerColor),
                RoundedCornerShape(16.dp)
            )
            .clickableRipple { choiceDialogOpen = true },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.padding(top = 5.dp, bottom = 5.dp, start = 12.dp),
            text = getSortTypeName(sortType),
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
            onSelectSortType = { onSelectSortType(it) }
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
        SortType.values().forEach { sortType ->
            DropdownMenuItem(onClick = {
                onSelectSortType(sortType)
                onCloseDialog()
            }) {
                Text(
                    text = getSortTypeName(sortType),
                    style = LocalTypography.current.bodyR14,
                    color = LocalPallet.current.text100
                )
            }
        }
    }
}

@Composable
private fun getSortTypeName(sortType: SortType): String {
    return when (sortType) {
        SortType.UPDATED -> stringResource(R.string.faphub_catalog_choice_updated)
        SortType.PUBLISHED -> stringResource(R.string.faphub_catalog_choice_published)
    }
}

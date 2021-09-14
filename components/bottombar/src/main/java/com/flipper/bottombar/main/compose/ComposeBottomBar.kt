package com.flipper.bottombar.main.compose

import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.flipper.bottombar.model.FlipperBottomTab

@Preview(
    showBackground = true
)
@Composable
fun ComposeBottomBar(
    selectedItem: FlipperBottomTab = FlipperBottomTab.STORAGE,
    onBottomBarClick: (FlipperBottomTab) -> Unit = {}
) {
    BottomNavigation {
        FlipperBottomTab.values().forEach { item ->
            BottomNavigationItem(
                icon = {
                    Icon(
                        painter = painterResource(id = item.icon),
                        contentDescription = stringResource(item.title)
                    )
                },
                label = { Text(text = stringResource(id = item.title)) },
                selected = item == selectedItem,
                onClick = { onBottomBarClick(item) }
            )
        }
    }
}

package com.flipperdevices.remotecontrols.impl.brands.composable.composable

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.ktx.clickableRipple
import com.flipperdevices.core.ui.searchbar.ComposableSearchBar
import com.flipperdevices.core.ui.theme.LocalPalletV2
import com.flipperdevices.ifrmvp.core.ui.layout.shared.SharedTopBar
import com.flipperdevices.remotecontrols.brands.impl.R

@Composable
internal fun ComposableBrandsAppBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onBackClick: () -> Unit
) {
    var isQueryShown by rememberSaveable {
        mutableStateOf(query.isNotBlank())
    }
    AnimatedContent(
        targetState = isQueryShown,
        transitionSpec = { fadeIn().togetherWith(fadeOut()) },
    ) { localIsQueryShown ->
        if (localIsQueryShown) {
            ComposableSearchBar(
                text = query,
                hint = stringResource(R.string.rcb_search_hint),
                onClear = {
                    isQueryShown = false
                    onQueryChange.invoke("")
                },
                onChangeText = onQueryChange,
                onBack = onBackClick
            )
        } else {
            SharedTopBar(
                title = stringResource(R.string.brands_title),
                subtitle = stringResource(R.string.rcb_step_2),
                onBackClick = onBackClick,
                actions = {
                    Icon(
                        modifier = Modifier
                            .padding(end = 14.dp)
                            .size(24.dp)
                            .clickableRipple(onClick = { isQueryShown = !isQueryShown }),
                        painter = painterResource(com.flipperdevices.core.ui.res.R.drawable.ic_search),
                        contentDescription = null,
                        tint = LocalPalletV2.current.icon.blackAndWhite.blackOnColor
                    )
                }
            )
        }
    }
}

package com.flipperdevices.core.ui.searchbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.ktx.clickableRipple
import com.flipperdevices.core.ui.theme.LocalPallet
import flipperapp.components.core.ui.res.generated.resources.ic_back_arrow
import flipperapp.components.core.ui.searchbar.generated.resources.Res
import flipperapp.components.core.ui.searchbar.generated.resources.ic_clear
import org.jetbrains.compose.resources.painterResource
import flipperapp.components.core.ui.res.generated.resources.Res as CoreUiRes

@Composable
fun ComposableSearchBar(
    hint: String,
    onChangeText: (String) -> Unit,
    onBack: () -> Unit
) {
    var text by remember { mutableStateOf("") }
    ComposableSearchBar(
        hint = hint,
        text = text,
        onClear = { onChangeText.invoke("") },
        onChangeText = {
            text = it
            onChangeText(it)
        },
        onBack = onBack
    )
}

@Composable
fun ComposableSearchBar(
    hint: String,
    text: String,
    onChangeText: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    onClear: () -> Unit = { onChangeText.invoke("") }
) {
    Row(
        modifier = modifier
            .background(LocalPallet.current.background)
            .statusBarsPadding(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ComposableSearchBarBack(onBack)
        ComposableSearchTextField(
            modifier = Modifier
                .weight(weight = 1f)
                .padding(horizontal = 24.dp, vertical = 14.dp),
            text = text,
            hint = hint,
            onTextChange = onChangeText
        )
        Icon(
            modifier = Modifier
                .padding(end = 20.dp, top = 14.dp, bottom = 14.dp)
                .clickableRipple(bounded = false, onClick = onClear),
            painter = painterResource(Res.drawable.ic_clear),
            contentDescription = null
        )
    }
}

@Composable
private fun ComposableSearchBarBack(onBack: () -> Unit) {
    Icon(
        modifier = Modifier
            .padding(start = 24.dp, top = 14.dp, bottom = 14.dp)
            .size(size = 24.dp)
            .clickableRipple(bounded = false, onClick = onBack),
        painter = painterResource(CoreUiRes.drawable.ic_back_arrow),
        contentDescription = null
    )
}

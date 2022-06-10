package com.flipperdevices.firstpair.impl.composable.searching

import com.flipperdevices.core.ui.res.R as DesignSystem
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flipperdevices.core.ui.ktx.ComposeLottiePic
import com.flipperdevices.firstpair.impl.R

@Composable
fun ComposableSearchingProgress(modifier: Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier.padding(bottom = 36.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier
                    .size(size = 82.dp)
                    .padding(end = 13.dp),
                painter = painterResource(R.drawable.pic_phone),
                contentDescription = null
            )
            ComposeLottiePic(
                modifier = Modifier.size(size = 32.dp),
                picResId = R.raw.dots_loader,
                rollBackPicResId = R.drawable.pic_loader
            )
            Icon(
                modifier = Modifier
                    .size(size = 82.dp)
                    .padding(start = 13.dp),
                painter = painterResource(R.drawable.pic_flipper_heavy),
                contentDescription = stringResource(R.string.firstpair_search_flipper_status),
                tint = colorResource(DesignSystem.color.accent_secondary)
            )
        }

        Text(
            text = stringResource(R.string.firstpair_search_loader_text),
            color = colorResource(DesignSystem.color.black_30),
            fontWeight = FontWeight.W400,
            fontSize = 18.sp
        )
    }
}

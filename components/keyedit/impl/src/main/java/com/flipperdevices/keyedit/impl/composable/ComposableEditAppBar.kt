package com.flipperdevices.keyedit.impl.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import com.flipperdevices.core.ui.R as DesignSystem
import com.flipperdevices.keyedit.impl.R

@Composable
@Preview(
    showBackground = true,
    showSystemUi = true
)
fun ComposableEditAppBar(
    onBack: () -> Unit = {},
    onSave: () -> Unit = {}
) {
    ConstraintLayout(
        modifier = Modifier
            .padding(horizontal = 14.dp, vertical = 16.dp)
            .fillMaxWidth()
    ) {
        val (cancel, title, save) = createRefs()

        Text(
            modifier = Modifier
                .constrainAs(cancel) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(title.start)
                }
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(bounded = false),
                    onClick = onBack
                ),
            text = stringResource(R.string.keyedit_bar_cancel),
            fontSize = 14.sp,
            color = colorResource(DesignSystem.color.black_40),
            fontWeight = FontWeight.W500
        )
        Text(
            modifier = Modifier.constrainAs(title) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                start.linkTo(cancel.end)
                end.linkTo(save.start)
            },
            text = stringResource(R.string.keyedit_bar_title),
            fontSize = 18.sp,
            fontWeight = FontWeight.W800,
            color = colorResource(DesignSystem.color.black_88)
        )
        Text(
            modifier = Modifier
                .constrainAs(save) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(title.end)
                    end.linkTo(parent.end)
                }
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(bounded = false),
                    onClick = onSave
                ),
            text = stringResource(R.string.keyedit_bar_save),
            fontWeight = FontWeight.W500,
            fontSize = 14.sp,
            color = colorResource(DesignSystem.color.accent_secondary)
        )

        createHorizontalChain(
            cancel, title, save,
            chainStyle = ChainStyle.SpreadInside
        )
    }
}

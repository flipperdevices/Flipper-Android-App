package com.flipperdevices.keyscreen.shared

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout

@Composable
fun ComposableKeyScreenAppBar(
    startBlock: @Composable (Modifier) -> Unit = {},
    centerBlock: @Composable (Modifier) -> Unit = {},
    endBlock: @Composable (Modifier) -> Unit = {}
) {
    ConstraintLayout(
        modifier = Modifier
            .padding(horizontal = 14.dp, vertical = 16.dp)
            .fillMaxWidth()
    ) {
        val (cancel, title, save) = createRefs()

        startBlock(
            Modifier
                .constrainAs(cancel) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                }
        )

        centerBlock(
            Modifier.constrainAs(title) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        )

        endBlock(
            Modifier
                .constrainAs(save) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    end.linkTo(parent.end)
                }
        )
    }
}

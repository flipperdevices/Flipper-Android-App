package com.flipperdevices.core.ui.hexkeyboard

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.constraintlayout.compose.ConstrainedLayoutReference
import androidx.constraintlayout.compose.ConstraintLayoutScope
import androidx.constraintlayout.compose.Dimension
import com.flipperdevices.core.data.PredefinedEnumMap

@Composable
internal fun ConstraintLayoutScope.ComposableKeys123(
    refs: PredefinedEnumMap<HexKey, ConstrainedLayoutReference>
) {
    ComposableKey(
        key = HexKey.One,
        modifier = Modifier.constrainAs(refs[HexKey.One]) {
            top.linkTo(parent.top)
            bottom.linkTo(refs[HexKey.Four].top)
            start.linkTo(parent.start)
            end.linkTo(refs[HexKey.Two].start)

            height = Dimension.fillToConstraints
            width = Dimension.fillToConstraints
        }
    )
    ComposableKey(
        key = HexKey.Two,
        modifier = Modifier.constrainAs(refs[HexKey.Two]) {
            top.linkTo(refs[HexKey.One].top)
            bottom.linkTo(refs[HexKey.One].bottom)
            start.linkTo(refs[HexKey.One].end)
            end.linkTo(refs[HexKey.Three].start)

            height = Dimension.fillToConstraints
            width = Dimension.fillToConstraints
        }
    )
    ComposableKey(
        key = HexKey.Three,
        modifier = Modifier.constrainAs(refs[HexKey.Three]) {
            top.linkTo(refs[HexKey.Two].top)
            bottom.linkTo(refs[HexKey.Two].bottom)
            start.linkTo(refs[HexKey.Two].end)
            end.linkTo(refs[HexKey.A].start)

            height = Dimension.fillToConstraints
            width = Dimension.fillToConstraints
        }
    )
}

@Composable
internal fun ConstraintLayoutScope.ComposableKeysABC(
    refs: PredefinedEnumMap<HexKey, ConstrainedLayoutReference>
) {
    ComposableKey(
        key = HexKey.A,
        modifier = Modifier.constrainAs(refs[HexKey.A]) {
            top.linkTo(refs[HexKey.Three].top)
            bottom.linkTo(refs[HexKey.Three].bottom)
            start.linkTo(refs[HexKey.Three].end)
            end.linkTo(refs[HexKey.B].start)

            height = Dimension.fillToConstraints
            width = Dimension.fillToConstraints
        }
    )
    ComposableKey(
        key = HexKey.B,
        modifier = Modifier.constrainAs(refs[HexKey.B]) {
            top.linkTo(refs[HexKey.A].top)
            bottom.linkTo(refs[HexKey.A].bottom)
            start.linkTo(refs[HexKey.A].end)
            end.linkTo(refs[HexKey.C].start)

            height = Dimension.fillToConstraints
            width = Dimension.fillToConstraints
        }
    )
    ComposableKey(
        key = HexKey.C,
        modifier = Modifier.constrainAs(refs[HexKey.C]) {
            top.linkTo(refs[HexKey.B].top)
            bottom.linkTo(refs[HexKey.B].bottom)
            start.linkTo(refs[HexKey.B].end)
            end.linkTo(parent.end)

            height = Dimension.fillToConstraints
            width = Dimension.fillToConstraints
        }
    )
}

@Composable
internal fun ConstraintLayoutScope.ComposableKeys456(
    refs: PredefinedEnumMap<HexKey, ConstrainedLayoutReference>
) {
    ComposableKey(
        key = HexKey.Four,
        modifier = Modifier.constrainAs(refs[HexKey.Four]) {
            top.linkTo(refs[HexKey.One].bottom)
            bottom.linkTo(refs[HexKey.Seven].top)
            start.linkTo(parent.start)
            end.linkTo(refs[HexKey.Five].start)

            height = Dimension.fillToConstraints
            width = Dimension.fillToConstraints
        }
    )
    ComposableKey(
        key = HexKey.Five,
        modifier = Modifier.constrainAs(refs[HexKey.Five]) {
            top.linkTo(refs[HexKey.Four].top)
            bottom.linkTo(refs[HexKey.Four].bottom)
            start.linkTo(refs[HexKey.Four].end)
            end.linkTo(refs[HexKey.Six].start)

            height = Dimension.fillToConstraints
            width = Dimension.fillToConstraints
        }
    )
    ComposableKey(
        key = HexKey.Six,
        modifier = Modifier.constrainAs(refs[HexKey.Six]) {
            top.linkTo(refs[HexKey.Five].top)
            bottom.linkTo(refs[HexKey.Five].bottom)
            start.linkTo(refs[HexKey.Five].end)
            end.linkTo(refs[HexKey.D].start)

            height = Dimension.fillToConstraints
            width = Dimension.fillToConstraints
        }
    )
}

@Composable
internal fun ConstraintLayoutScope.ComposableKeysDEF(
    refs: PredefinedEnumMap<HexKey, ConstrainedLayoutReference>
) {
    ComposableKey(
        key = HexKey.D,
        modifier = Modifier.constrainAs(refs[HexKey.D]) {
            top.linkTo(refs[HexKey.Six].top)
            bottom.linkTo(refs[HexKey.Six].bottom)
            start.linkTo(refs[HexKey.Six].end)
            end.linkTo(refs[HexKey.E].start)

            height = Dimension.fillToConstraints
            width = Dimension.fillToConstraints
        }
    )
    ComposableKey(
        key = HexKey.E,
        modifier = Modifier.constrainAs(refs[HexKey.E]) {
            top.linkTo(refs[HexKey.D].top)
            bottom.linkTo(refs[HexKey.D].bottom)
            start.linkTo(refs[HexKey.D].end)
            end.linkTo(refs[HexKey.F].start)

            height = Dimension.fillToConstraints
            width = Dimension.fillToConstraints
        }
    )
    ComposableKey(
        key = HexKey.F,
        modifier = Modifier.constrainAs(refs[HexKey.F]) {
            top.linkTo(refs[HexKey.E].top)
            bottom.linkTo(refs[HexKey.E].bottom)
            start.linkTo(refs[HexKey.E].end)
            end.linkTo(parent.end)

            height = Dimension.fillToConstraints
            width = Dimension.fillToConstraints
        }
    )
}

@Composable
internal fun ConstraintLayoutScope.ComposableKeys7890(
    refs: PredefinedEnumMap<HexKey, ConstrainedLayoutReference>
) {
    ComposableKey(
        key = HexKey.Seven,
        modifier = Modifier.constrainAs(refs[HexKey.Seven]) {
            top.linkTo(refs[HexKey.Four].bottom)
            bottom.linkTo(refs[HexKey.Zero].top)
            start.linkTo(parent.start)
            end.linkTo(refs[HexKey.Eight].start)

            height = Dimension.fillToConstraints
            width = Dimension.fillToConstraints
        }
    )
    ComposableKey(
        key = HexKey.Eight,
        modifier = Modifier.constrainAs(refs[HexKey.Eight]) {
            top.linkTo(refs[HexKey.Seven].top)
            bottom.linkTo(refs[HexKey.Seven].bottom)
            start.linkTo(refs[HexKey.Seven].end)
            end.linkTo(refs[HexKey.Nine].start)

            height = Dimension.fillToConstraints
            width = Dimension.fillToConstraints
        }
    )
    ComposableKey(
        key = HexKey.Nine,
        modifier = Modifier.constrainAs(refs[HexKey.Nine]) {
            top.linkTo(refs[HexKey.Eight].top)
            bottom.linkTo(refs[HexKey.Eight].bottom)
            start.linkTo(refs[HexKey.Eight].end)
            end.linkTo(refs[HexKey.Six].end)

            height = Dimension.fillToConstraints
            width = Dimension.fillToConstraints
        }
    )
    ComposableKey(
        key = HexKey.Zero,
        modifier = Modifier.constrainAs(refs[HexKey.Zero]) {
            top.linkTo(refs[HexKey.Seven].bottom)
            bottom.linkTo(parent.bottom)
            start.linkTo(refs[HexKey.Seven].start)
            end.linkTo(refs[HexKey.Nine].end)

            height = Dimension.fillToConstraints
            width = Dimension.fillToConstraints
        }
    )
}

@Composable
internal fun ConstraintLayoutScope.ComposableKeysClearOk(
    refs: PredefinedEnumMap<HexKey, ConstrainedLayoutReference>
) {
    ComposableKey(
        key = HexKey.Clear,
        modifier = Modifier.constrainAs(refs[HexKey.Clear]) {
            top.linkTo(refs[HexKey.D].bottom)
            bottom.linkTo(parent.bottom)
            start.linkTo(refs[HexKey.Nine].end)
            end.linkTo(refs[HexKey.Ok].start)

            width = Dimension.fillToConstraints
            height = Dimension.fillToConstraints
        }
    )
    ComposableKey(
        key = HexKey.Ok,
        modifier = Modifier.constrainAs(refs[HexKey.Ok]) {
            top.linkTo(refs[HexKey.E].bottom)
            bottom.linkTo(parent.bottom)
            start.linkTo(refs[HexKey.E].start)
            end.linkTo(parent.end)

            height = Dimension.fillToConstraints
            width = Dimension.fillToConstraints
        }
    )
}

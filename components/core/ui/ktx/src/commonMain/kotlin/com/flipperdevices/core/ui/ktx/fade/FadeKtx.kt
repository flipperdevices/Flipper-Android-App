package com.flipperdevices.core.ui.ktx.fade

import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.platform.InspectorInfo

@Stable
fun Modifier.fadingEdge(orientation: FadeOrientation): Modifier {
    return this
        .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
        .then(FadingEdgeElement(orientation))
}

private data class FadingEdgeElement(
    private val orientation: FadeOrientation
) : ModifierNodeElement<FadingEdgeNode>() {

    override fun create(): FadingEdgeNode {
        return FadingEdgeNode(orientation)
    }

    override fun update(node: FadingEdgeNode) {
        node.orientation = orientation
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "FadingEdge"
        properties["orientation"] = orientation.name
    }
}

private class FadingEdgeNode(var orientation: FadeOrientation) : Modifier.Node(), DrawModifierNode {
    private fun FadeOrientation.toBrush() = when (this) {
        is FadeOrientation.Bottom -> {
            Brush.verticalGradient(
                0f to FILL_COLOR,
                this.THRESHOLD to FILL_COLOR,
                1f to Color.Transparent
            )
        }

        is FadeOrientation.Top -> {
            Brush.verticalGradient(
                0f to Color.Transparent,
                this.THRESHOLD to FILL_COLOR
            )
        }
    }

    override fun ContentDrawScope.draw() {
        val brush = orientation.toBrush()
        drawContent()
        drawRect(brush = brush, blendMode = BlendMode.DstIn)
    }

    companion object {
        private val FILL_COLOR = Color.White
    }
}

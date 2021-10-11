package com.flipperdevices.pair.impl.composable.guide.imageslider

import android.view.ViewGroup
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.flipperdevices.pair.impl.R
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderView

@Composable
fun ComposableImageSlider(
    modifier: Modifier = Modifier,
    items: List<ImageSliderItem>
) {
    var size by remember { mutableStateOf(IntSize.Zero) }
    val imageSliderIndicatorRadius =
        dimensionResource(R.dimen.image_slider_indicator_radius)
    val marginBottomDp = dimensionResource(R.dimen.image_slider_bottom_empty_margin)
    val marginBottomPx = with(LocalDensity.current) { marginBottomDp.roundToPx() }
    Box(
        modifier = modifier
            .onSizeChanged {
                size = it
            }
    ) {
        AndroidView(
            modifier = Modifier.fillMaxWidth(),
            factory = { context ->
                SliderView(context).apply {
                    layoutParams = ViewGroup.LayoutParams(size.width, size.height)
                    setSliderAdapter(ImageSliderAdapter(items))
                    setIndicatorEnabled(true)
                    indicatorRadius = imageSliderIndicatorRadius.value.toInt()
                    indicatorSelectedColor = android.graphics.Color.BLACK
                    indicatorUnselectedColor = android.graphics.Color.GRAY
                    setIndicatorAnimation(IndicatorAnimationType.WORM)

                    val totalMargin = marginBottomPx / 2 - indicatorRadius
                    setIndicatorMargin(totalMargin)
                }
            },
            update = {
                it.layoutParams = ViewGroup.LayoutParams(size.width, size.height)
            }
        )
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun PreviewComposableImageSlider() {
    val testList = listOf(
        ImageSliderItem(R.drawable.ic_scanning),
        ImageSliderItem(R.drawable.ic_scanning)
    )

    Column {
        ComposableImageSlider(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            testList
        )
    }
}

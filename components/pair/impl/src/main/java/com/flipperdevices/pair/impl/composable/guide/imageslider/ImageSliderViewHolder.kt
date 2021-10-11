package com.flipperdevices.pair.impl.composable.guide.imageslider

import com.flipperdevices.pair.impl.databinding.ImageSliderLayoutItemBinding
import com.smarteist.autoimageslider.SliderViewAdapter

class ImageSliderViewHolder(
    private val itemSliderLayout: ImageSliderLayoutItemBinding
) : SliderViewAdapter.ViewHolder(itemSliderLayout.root) {
    fun bind(item: ImageSliderItem) {
        itemSliderLayout.image.setImageResource(item.imageId)
    }
}

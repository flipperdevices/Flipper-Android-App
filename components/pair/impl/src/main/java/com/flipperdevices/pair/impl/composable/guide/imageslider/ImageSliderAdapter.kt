package com.flipperdevices.pair.impl.composable.guide.imageslider

import android.view.LayoutInflater
import android.view.ViewGroup
import com.flipperdevices.pair.impl.databinding.ImageSliderLayoutItemBinding
import com.smarteist.autoimageslider.SliderViewAdapter

class ImageSliderAdapter(private val items: List<ImageSliderItem>) :
    SliderViewAdapter<ImageSliderViewHolder>() {
    override fun getCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup): ImageSliderViewHolder {
        val itemView = ImageSliderLayoutItemBinding.inflate(LayoutInflater.from(parent.context))
        return ImageSliderViewHolder(itemView)
    }

    override fun onBindViewHolder(
        viewHolder: ImageSliderViewHolder,
        position: Int
    ) {
        viewHolder.bind(items[position])
    }
}

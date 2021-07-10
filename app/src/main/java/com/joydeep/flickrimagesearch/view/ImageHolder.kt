package com.joydeep.flickrimagesearch.view

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.facebook.shimmer.Shimmer
import com.facebook.shimmer.ShimmerDrawable
import com.joydeep.flickrimagesearch.databinding.HolderImageBinding

class ImageHolder(private val binding: HolderImageBinding) : RecyclerView.ViewHolder(binding.root) {

    private val shimmer: Shimmer = Shimmer.AlphaHighlightBuilder()
        .setDuration(1800)
        .setBaseAlpha(0.7f)
        .setHighlightAlpha(0.6f)
        .setDirection(Shimmer.Direction.LEFT_TO_RIGHT)
        .setAutoStart(true)
        .build()

    private val shimmerDrawable = ShimmerDrawable().apply {
        setShimmer(shimmer)
    }

    fun bind(url: String) {
        Glide.with(binding.root.context)
            .load(url)
            .centerCrop()
            .placeholder(shimmerDrawable)
            .into(binding.imageView)
    }

}
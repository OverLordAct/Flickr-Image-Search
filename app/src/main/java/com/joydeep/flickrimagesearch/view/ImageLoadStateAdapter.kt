package com.joydeep.flickrimagesearch.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import com.joydeep.flickrimagesearch.databinding.HolderFooterBinding

class ImageLoadStateAdapter(
    private val retry: (() -> Unit)? = null
) : LoadStateAdapter<LoadStateHolder>() {
    override fun onBindViewHolder(holder: LoadStateHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadStateHolder {
        val binding =
            HolderFooterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LoadStateHolder(binding, retry)
    }
}
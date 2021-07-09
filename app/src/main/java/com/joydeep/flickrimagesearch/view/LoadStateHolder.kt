package com.joydeep.flickrimagesearch.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.joydeep.flickrimagesearch.databinding.HolderFooterBinding

class LoadStateHolder(
    private val binding: HolderFooterBinding,
    private val retry: (() -> Unit)? = null
) : RecyclerView.ViewHolder(binding.root) {
    init {
        binding.retryButton.setOnClickListener { retry?.invoke() }
    }

    fun bind(loadState: LoadState) {
        if (loadState is LoadState.Error) {
            binding.errorMessage.text = loadState.error.localizedMessage
        }

        binding.retryButton.isVisible = loadState is LoadState.Error
        binding.progressBar.isVisible = loadState is LoadState.Loading
        binding.errorMessage.isVisible = loadState is LoadState.Error

        if (loadState is LoadState.Error) {
            binding.errorMessage.text = loadState.error.localizedMessage
        }
    }
}
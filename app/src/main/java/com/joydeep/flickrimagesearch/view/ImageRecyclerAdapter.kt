package com.joydeep.flickrimagesearch.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.joydeep.flickrimagesearch.databinding.HolderImageBinding
import com.joydeep.flickrimagesearch.model.PhotoEntity

class ImageRecyclerAdapter(
    private var list: MutableList<PhotoEntity>
) : RecyclerView.Adapter<ImageHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageHolder {
        val binding = HolderImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageHolder, position: Int) {
        val item = list[position]
        holder.bind(item.url)
    }

    override fun getItemCount(): Int = list.size

    fun updateData(newList: List<PhotoEntity>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }
}
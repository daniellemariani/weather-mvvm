package com.dmariani.weathermvvm.ui.main

import androidx.recyclerview.widget.RecyclerView
import com.dmariani.weathermvvm.databinding.ItemRecentSearchBinding

/**
 * This class holds a reference to the row's view in a RecyclerView
 */
class RecentSearchViewHolder(private val binding: ItemRecentSearchBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind (cityName: String) {
        binding.textRecentSearchItem.text = cityName
    }
}
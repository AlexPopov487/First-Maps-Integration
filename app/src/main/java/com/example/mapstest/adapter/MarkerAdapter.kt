package com.example.mapstest.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mapstest.databinding.MarkerListItemBinding
import com.example.mapstest.db.MarkerEntity

class MarkerAdapter : ListAdapter<MarkerEntity, MarkerViewHolder>(MarkerDiffCallback) {

    object MarkerDiffCallback : DiffUtil.ItemCallback<MarkerEntity>() {
        override fun areItemsTheSame(oldItem: MarkerEntity, newItem: MarkerEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: MarkerEntity, newItem: MarkerEntity): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MarkerViewHolder {
        val binding = MarkerListItemBinding.inflate(LayoutInflater.from(parent.context),
        parent, false)
        return MarkerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MarkerViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}

class MarkerViewHolder(private val binding: MarkerListItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
        fun bind(marker: MarkerEntity) {
            with(binding){
                markerTitleTv.text = marker.title
                markerLongTv.text = marker.longitude.toString()
                markerLatTv.text = marker.latitude.toString()
            }

        }
}

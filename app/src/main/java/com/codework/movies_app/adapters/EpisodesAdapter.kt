package com.codework.movies_app.adapters

import android.annotation.SuppressLint
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.codework.movies_app.R
import com.codework.movies_app.data.Episode
import com.codework.movies_app.databinding.ItemEpisodeBinding

class EpisodesAdapter : RecyclerView.Adapter<EpisodesAdapter.EpisodeViewHolder>() {

    inner class EpisodeViewHolder(val binding: ItemEpisodeBinding) : ViewHolder(binding.root) {
        @SuppressLint("UseCompatLoadingForDrawables")
        fun bind(episode: Episode, isSelected: Boolean, isFirst: Boolean) {
            binding.btnEpisode.text = episode.name

            // Apply background based on selection or first item
            when {
                isSelected -> {
                    binding.btnEpisode.background =
                        ColorDrawable(itemView.context.resources.getColor(R.color.g_orange_yellow))
                }
                isFirst -> {
                    binding.btnEpisode.background =
                        ColorDrawable(itemView.context.resources.getColor(R.color.background_episode))
                }
                else -> {
                    binding.btnEpisode.background =
                        itemView.context.getDrawable(R.drawable.bg_episodes)
                }
            }
        }
    }

    private val diffCallBack = object : DiffUtil.ItemCallback<Episode>() {
        override fun areItemsTheSame(oldItem: Episode, newItem: Episode): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: Episode, newItem: Episode): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallBack)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EpisodeViewHolder {
        return EpisodeViewHolder(
            ItemEpisodeBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private var selectedEpisode = 0 // Default selected is the first item

    override fun onBindViewHolder(holder: EpisodeViewHolder, position: Int) {
        val episode = differ.currentList[position]
        val isFirst = position == 0 // Check if it's the first item
        holder.bind(episode, selectedEpisode == position, isFirst)

        // Set click listener for each episode
        holder.binding.btnEpisode.setOnClickListener {
            // Deselect the previously selected item
            if (selectedEpisode >= 0) notifyItemChanged(selectedEpisode)

            // Update the selected episode
            selectedEpisode = holder.adapterPosition
            notifyItemChanged(selectedEpisode)

            // Notify the click listener
            onClick?.invoke(episode)
        }
    }

    var onClick: ((Episode) -> Unit)? = null
}


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

class EpisodesAdapter: RecyclerView.Adapter<EpisodesAdapter.EpisodeViewHolder>() {
    inner class EpisodeViewHolder(val binding: ItemEpisodeBinding) : ViewHolder(binding.root) {
        @SuppressLint("UseCompatLoadingForDrawables")
        fun bind(episode: Episode, isSelected: Boolean) {
            binding.btnEpisode.text = episode.name
            if(isSelected){
                binding.btnEpisode.background = ColorDrawable(itemView.context.resources.getColor(R.color.g_orange_yellow))
            }
            else{
                binding.btnEpisode.background = itemView.context.getDrawable(R.drawable.bg_episodes)
            }
        }
    }

    private val diffCallBack = object : DiffUtil.ItemCallback<Episode>(){
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

    private var selectedEpisode = -1
    override fun onBindViewHolder(holder: EpisodeViewHolder, position: Int) {
        val episode = differ.currentList[position]
        holder.bind(episode, selectedEpisode == position)
        holder.binding.btnEpisode.setOnClickListener{
            if(selectedEpisode >= 0)
                notifyItemChanged(selectedEpisode)
            selectedEpisode = holder.adapterPosition
            notifyItemChanged(selectedEpisode)
            onClick?.invoke(episode)
        }
    }

    var onClick: ((Episode) -> Unit)? = null
}
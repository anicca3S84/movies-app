package com.codework.movies_app.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.codework.movies_app.data.Item
import com.codework.movies_app.databinding.ItemSuggestBinding

class SuggestFilmAdapter: RecyclerView.Adapter<SuggestFilmAdapter.SuggestFilmViewHolder>()  {
    inner class SuggestFilmViewHolder(private val binding: ItemSuggestBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(film: Item) {
            Glide.with(itemView).load(film.posterUrl).into(binding.imgAvatar)
            binding.tvFilmName.text = film.name
        }
    }

    private val diffCallBack = object : DiffUtil.ItemCallback<Item>(){
        override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
            return  oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this, diffCallBack)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuggestFilmViewHolder {
        return SuggestFilmViewHolder(
            ItemSuggestBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: SuggestFilmViewHolder, position: Int) {
        val film = differ.currentList[position]
        holder.bind(film)

        holder.itemView.setOnClickListener{
            onClick?.invoke(film)
        }
    }

    var onClick: ((Item) -> Unit)? = null
}
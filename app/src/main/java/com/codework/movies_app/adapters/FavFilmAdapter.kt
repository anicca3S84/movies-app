package com.codework.movies_app.adapters

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.codework.movies_app.data.Item
import com.codework.movies_app.databinding.ItemFavBinding

class FavFilmAdapter : RecyclerView.Adapter<FavFilmAdapter.FavViewHolder>() {

    inner class FavViewHolder(private val binding: ItemFavBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Item) {
            Glide.with(itemView).load(item.posterUrl).into(binding.imgFilm)
            binding.tvFilmTitle.text = item.name
            binding.tvYear.text = item.year.toString()
            binding.tvOriginName.text = item.originName
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavViewHolder {
        return FavViewHolder(
            ItemFavBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    private val diffCallback = object : DiffUtil.ItemCallback<Item>() {
        override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)

    override fun getItemCount(): Int = differ.currentList.size

    override fun onBindViewHolder(holder: FavViewHolder, position: Int) {
        val film = differ.currentList[position]
        holder.bind(film)

        holder.itemView.setOnClickListener {
            onClick?.invoke(film)
        }

        holder.itemView.setOnLongClickListener {
            onLongClick?.invoke(film, position)
            true
        }
    }


    var onLongClick: ((Item, Int) -> Unit)? = null

    var onClick: ((Item) -> Unit)? = null
}


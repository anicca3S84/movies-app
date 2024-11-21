package com.codework.movies_app.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.codework.movies_app.data.Item
import com.codework.movies_app.databinding.ItemHistoryBinding

class HistoryAdapter: RecyclerView.Adapter<HistoryAdapter.SameCategoryViewHolder>() {
    inner class SameCategoryViewHolder(private val binding: ItemHistoryBinding): ViewHolder(binding.root){
        fun bind(item: Item) {
            Glide.with(itemView).load(item.thumbUrl).into(binding.imgFilm)
            binding.tvFilmName.text = item.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SameCategoryViewHolder {
        return SameCategoryViewHolder(
            ItemHistoryBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    private val diffCallBack = object : DiffUtil.ItemCallback<Item>(){
        override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallBack)

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: SameCategoryViewHolder, position: Int) {
        val film = differ.currentList[position]
        holder.bind(film)

        holder.itemView.setOnClickListener{
            onClick?.invoke(film)
        }
    }

    var onClick: ((Item) -> Unit)? = null
}
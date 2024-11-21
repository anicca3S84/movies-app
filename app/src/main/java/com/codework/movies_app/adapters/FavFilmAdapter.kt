package com.codework.movies_app.adapters

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.codework.movies_app.data.Item
import com.codework.movies_app.databinding.ItemFavBinding
import com.codework.movies_app.viewmodes.FavoriteViewModel


class FavFilmAdapter: RecyclerView.Adapter<FavFilmAdapter.FavViewHolder>() {
    inner class FavViewHolder(private val binding: ItemFavBinding): ViewHolder(binding.root){
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

    override fun onBindViewHolder(holder: FavViewHolder, position: Int) {
        val film = differ.currentList[position]
        holder.bind(film)

        holder.itemView.setOnClickListener{
            onClick?.invoke(film)
        }

        holder.itemView.setOnLongClickListener {
            AlertDialog.Builder(it.context).apply {
                setTitle("Xác nhận")
                setMessage("Bạn có chắc chắn muốn xóa ${film.name}?")
                setPositiveButton("Xóa") { _, _ ->
                    removeItem(position)
                }
                setNegativeButton("Hủy", null)
            }.show()
            true
        }

    }

    private fun removeItem(position: Int) {
        val currentList = differ.currentList.toMutableList()
        onLongClick?.invoke(currentList[position])
        currentList.removeAt(position)
        differ.submitList(currentList)

    }

    var onLongClick: ((Item) -> Unit)? = null

    var onClick: ((Item) -> Unit)? = null

}
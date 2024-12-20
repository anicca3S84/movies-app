package com.codework.movies_app.adapters

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.codework.movies_app.R
import com.codework.movies_app.data.Item

class SearchItemAdapter(
    private val itemList: List<Item>,
    private val fragment: Fragment
) : RecyclerView.Adapter<SearchItemAdapter.ItemViewHolder>() {
    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemImage: ImageView = view.findViewById(R.id.searchItem_image)
        val itemName: TextView = view.findViewById(R.id.searchItem_name)
        val itemYear: TextView = view.findViewById(R.id.searchItem_year)
        val rootSearchItemLayout: ConstraintLayout = view.findViewById(R.id.rootSearchItemLayout)
    }
    init {
        Log.d("Adapter", "Item list size: ${itemList.size}")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_search, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = itemList[position]

        holder.itemName.text = item.name
        holder.itemYear.text = item.year.toString()
        Glide.with(holder.itemImage.context)
            .load(item.posterUrl)
            .into(holder.itemImage)

        Log.d("Adapter: Name: ", item.name)
        Log.d("Adapter: Year: ", item.year.toString())
        holder.rootSearchItemLayout.setOnClickListener {
            val b = Bundle().apply { putString("slug", item.slug)}
            fragment.findNavController().navigate(R.id.action_searchFragment_to_filmDetailFragment, b)
        }

    }

    override fun getItemCount(): Int {
        return itemList.size
    }

}
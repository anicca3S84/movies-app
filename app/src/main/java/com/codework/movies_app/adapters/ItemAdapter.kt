package com.codework.movies_app.adapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.codework.movies_app.R
import com.codework.movies_app.data.Item
import androidx.navigation.fragment.findNavController

class ItemAdapter(
    private val itemList: List<Item>,
    private val fragment: Fragment
//    private val navController: NavController
) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {
    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemImage: ImageView = view.findViewById(R.id.itemImage)
        val itemName: TextView = view.findViewById(R.id.itemName)
        val  rootItemLayout: LinearLayout = view.findViewById(R.id.rootItemLayout)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        return ItemViewHolder(view)
    }
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = itemList[position]
        holder.itemName.text = item.name
        Glide.with(holder.itemImage.context)
            .load(item.posterUrl)
            .into(holder.itemImage)
        holder.rootItemLayout.setOnClickListener {
            val b = Bundle().apply { putString("slug", item.slug)}
            fragment.findNavController().navigate(R.id.action_homeFragment_to_movieDetailFragment, b)
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}
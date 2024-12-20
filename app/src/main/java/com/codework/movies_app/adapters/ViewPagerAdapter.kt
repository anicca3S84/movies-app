package com.codework.movies_app.adapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.codework.movies_app.R
import com.codework.movies_app.data.Item

class ViewPagerAdapter(
    private val itemList: List<Item>,
    private val fragment: Fragment
) : RecyclerView.Adapter<ViewPagerAdapter.ViewPagerViewHolder>() {
    inner class ViewPagerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val viewPagerImage: ImageView = view.findViewById(R.id.viewPagerImage)
        val rootLayout: FrameLayout = view.findViewById(R.id.rootItemPagerLayout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.viewpager_item_layout, parent, false)
        return ViewPagerViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewPagerViewHolder, position: Int) {
        val item = itemList[position]
        Glide.with(holder.viewPagerImage.context)
            .load(item.posterUrl)
            .into(holder.viewPagerImage)
        holder.rootLayout.setOnClickListener {
            val b = Bundle().apply { putString("slug", item.slug)}
            fragment.findNavController().navigate(R.id.action_homeFragment_to_movieDetailFragment, b)
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}
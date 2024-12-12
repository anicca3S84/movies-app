package com.codework.movies_app.adapters

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.codework.movies_app.data.Notification
import com.codework.movies_app.databinding.ItemNotificationBinding
import com.codework.movies_app.utils.FormatDate


class NotificationAdapter : RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {
    inner class NotificationViewHolder(private val binding: ItemNotificationBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Notification) {
            binding.tvActionType.text = item.actionType
            binding.tvContent.text = item.message
            binding.tvTime.text = FormatDate.formatDate(item.createdAt)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        return NotificationViewHolder(
            ItemNotificationBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    private val diffCallBack = object : DiffUtil.ItemCallback<Notification>() {
        override fun areItemsTheSame(oldItem: Notification, newItem: Notification): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Notification, newItem: Notification): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallBack)

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val comment = differ.currentList[position]
        holder.bind(comment)

        holder.itemView.setOnClickListener {
            onClick?.invoke(comment)
        }

        holder.itemView.setOnLongClickListener {
            AlertDialog.Builder(it.context).apply {
                setTitle("Xác nhận")
                setMessage("Bạn có chắc chắn muốn xóa?")
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

    var onLongClick: ((Notification) -> Unit)? = null

    var onClick: ((Notification) -> Unit)? = null

}
package com.codework.movies_app.adapters

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.codework.movies_app.data.Comment
import com.codework.movies_app.data.Item
import com.codework.movies_app.databinding.ItemCommentBinding
import com.codework.movies_app.utils.FormatDate

class CommentAdapter : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {
    inner class CommentViewHolder(private val binding: ItemCommentBinding) :
        ViewHolder(binding.root) {

        fun bind(item: Comment) {
            binding.tvUsername.text = item.username
            binding.tvContent.text = item.content
            binding.tvTime.text = FormatDate.formatDate(item.createdAt)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        return CommentViewHolder(
            ItemCommentBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    private val diffCallBack = object : DiffUtil.ItemCallback<Comment>() {
        override fun areItemsTheSame(oldItem: Comment, newItem: Comment): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Comment, newItem: Comment): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallBack)

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
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

    var onLongClick: ((Comment) -> Unit)? = null

    var onClick: ((Comment) -> Unit)? = null

}
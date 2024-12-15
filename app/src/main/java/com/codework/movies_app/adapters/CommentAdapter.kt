package com.codework.movies_app.adapters

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.codework.movies_app.data.Comment
import com.codework.movies_app.data.Item
import com.codework.movies_app.databinding.ItemCommentBinding
import com.codework.movies_app.dialogs.showLoginDialog
import com.codework.movies_app.utils.Constants
import com.codework.movies_app.utils.FormatDate

class CommentAdapter(
    private val context: Context, // Pass context from the Fragment or Activity
    private val showLoginDialog: () -> Unit // Callback to show the login dialog
) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    inner class CommentViewHolder(private val binding: ItemCommentBinding) :
        ViewHolder(binding.root) {

        fun bind(item: Comment) {
            binding.tvUsername.text = item.user.userName
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
            onLongClick?.invoke(comment, position) // Pass the correct position
            true // Indicate that the event has been handled
        }
    }


    var onLongClick: ((Comment, Int) -> Unit)? = null

    var onClick: ((Comment) -> Unit)? = null
}

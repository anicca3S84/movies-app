package com.codework.movies_app.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.codework.movies_app.R
import com.codework.movies_app.data.FAQItems

class FAQAdapter(private val faqList: List<FAQItems>) : RecyclerView.Adapter<FAQAdapter.FAQViewHolder>() {

    class FAQViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val questionTextView: TextView = itemView.findViewById(R.id.tvQuestion)
        val answerTextView: TextView = itemView.findViewById(R.id.tvAnswer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FAQViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_faq, parent, false)
        return FAQViewHolder(view)
    }

    override fun onBindViewHolder(holder: FAQViewHolder, position: Int) {
        val faqItem = faqList[position]
        holder.questionTextView.text = faqItem.question
        holder.answerTextView.text = faqItem.answer
    }

    override fun getItemCount(): Int = faqList.size
}

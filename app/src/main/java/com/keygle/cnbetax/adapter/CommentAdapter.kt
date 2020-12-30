package com.keygle.cnbetax.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

import com.keygle.cnbetax.bean.CommentList
import com.keygle.cnbetax.databinding.LayoutItemCommentBinding

class CommentAdapter(var commentList: MutableList<CommentList>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = LayoutItemCommentBinding.inflate(inflater, parent, false)
        return CommentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        // Populate ViewHolder with data that corresponds to the position in the list
        // which we are told to load
        (holder as CommentViewHolder).bind(commentList[position])
    }

    inner class CommentViewHolder(private val binding:LayoutItemCommentBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(comment: CommentList) {
            binding.commentUsername.text = comment.username
            binding.commentContent.text = comment.content
            binding.commentCreated.text = comment.created_time
            binding.commentSupport.text = comment.support
            binding.commentAgainst.text = comment.against
        }
    }

    override fun getItemCount(): Int {
        return commentList.size
    }

    fun update(result: MutableList<CommentList>) {
        if (commentList.size == 0) {
            commentList = result
        } else {
            commentList.addAll(result)
        }
    }
}
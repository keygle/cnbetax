package com.keygle.cnbetax

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.keygle.cnbetax.databinding.ArticleListItemBinding

class ArticleAdapter(var articleItemList: List<ArticleList>, private val clickListener: (ArticleList) ->Unit): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ArticleListItemBinding.inflate(inflater, parent, false)
        return ArticleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        // Populate ViewHolder with data that corresponds to the position in the list
        // which we are told to load
        (holder as ArticleViewHolder).bind(articleItemList[position], clickListener)
    }
    override fun getItemCount() = articleItemList.size

    inner class ArticleViewHolder(private val binding:ArticleListItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(article: ArticleList, clickListener: (ArticleList) -> Unit) {
            binding.articleTitle.text = article.title
            binding.articlePubtime.text = article.pubtime
            binding.articleCounter.text = article.counter
            binding.articleReplayCount.text = "0"
            binding.root.setOnClickListener { clickListener(article) }
        }
    }
}
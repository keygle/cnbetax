package com.keygle.cnbetax.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.keygle.cnbetax.bean.ArticleList
import com.keygle.cnbetax.databinding.LayoutItemArticleBinding

class ArticleAdapter(var articleList: MutableList<ArticleList>, val clickListener: (ArticleList) ->Unit, val commentClickListener: (ArticleList) ->Unit): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = LayoutItemArticleBinding.inflate(inflater, parent, false)
        return ArticleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        // Populate ViewHolder with data that corresponds to the position in the list
        // which we are told to load
        (holder as ArticleViewHolder).bind(articleList[position], clickListener, commentClickListener)
    }

    inner class ArticleViewHolder(private val binding:LayoutItemArticleBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(article: ArticleList, clickListener: (ArticleList) -> Unit, commentClickListener: (ArticleList) -> Unit) {
            binding.articleTitle.text = article.title
            binding.articlePubtime.text = article.pubtime
            binding.articleCounter.text = article.counter
            binding.articleComments.text = article.comments
            binding.root.setOnClickListener { clickListener(article) }
            binding.articleComments.setOnClickListener { commentClickListener(article) }
        }
    }

    override fun getItemCount(): Int {
        return articleList.size
    }


    /**
     * 获取最后一个 item 的 sid
     * @return last sid
     */
    fun getLastSid(): String? {
        val sid: String
        sid = if (articleList.size <= 0) {
            Int.MAX_VALUE.toString() + ""
        } else {
            val size: Int = articleList.size
            articleList[size - 1].sid
        }
        return sid
    }

    fun update(result: MutableList<ArticleList>) {
        if (articleList.size == 0) {
            articleList = result
        } else {
            articleList.addAll(result)
        }
    }
}
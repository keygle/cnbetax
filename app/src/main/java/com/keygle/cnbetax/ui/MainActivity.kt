package com.keygle.cnbetax.ui

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.keygle.cnbetax.R
import com.keygle.cnbetax.adapter.ArticleAdapter
import com.keygle.cnbetax.bean.ArticleList
import com.keygle.cnbetax.bean.ArticleListResponse
import com.keygle.cnbetax.databinding.ActivityMainBinding
import com.keygle.cnbetax.network.WebAccess
import com.keygle.cnbetax.utils.RequestUtil.getArticleListUrl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.IOException


class MainActivity : AppCompatActivity(){
    private val tag : String = MainActivity::class.java.simpleName
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: ArticleAdapter
    private var isLoading = false // 是否正在加载更多

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        // toolbar
        setToolBar()

        binding.rvArticles.layoutManager = LinearLayoutManager(this)
        /*
         * Use this setting to improve performance if you know that changes in content do not
         * change the child layout size in the RecyclerView
         */
        binding.rvArticles.setHasFixedSize(true)

        // Create the PartAdapter
        // 1st parameter: our generated testData. listOf() generates empty list with correct type
        // 2nd parameter: item click handler function (implemented below) as function parameter
        adapter = ArticleAdapter(mutableListOf(), { item: ArticleList -> itemClicked(item) }, { item: ArticleList -> commentClicked(item) })
        binding.rvArticles.adapter = adapter

        binding.rvArticles.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    val lastVisibleItemPosition: Int = (binding.rvArticles.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
                    if (!isLoading && lastVisibleItemPosition == (binding.rvArticles.adapter as ArticleAdapter).itemCount - 1) {
                        // start loadMore
                        isLoading = true;
                        Log.d("last", (binding.rvArticles.adapter as ArticleAdapter).getLastSid().toString())
                        loadArticleList((binding.rvArticles.adapter as ArticleAdapter).getLastSid())
                    }
                }
            }
        })

        autoRefresh()

        // 设置 下拉刷新的 layout
        val swipeRefreshLayout: SwipeRefreshLayout = binding.srMain
        swipeRefreshLayout.setOnRefreshListener { autoRefresh() }
    }

    private fun setToolBar() {
        val toolbar:Toolbar = findViewById(R.id.toolbar);
        toolbar.title = resources.getString(R.string.app_name)
        setSupportActionBar(toolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.article_act_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override  fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.about -> Toast.makeText(this, """感谢使用 by gressx""",
                Toast.LENGTH_LONG
            ).show()
            R.id.totop -> binding.rvArticles.smoothScrollToPosition(0)
            R.id.refresh -> autoRefresh()
            R.id.home -> onBackPressed()
            R.id.setting -> Toast.makeText(this, "开发中!", Toast.LENGTH_SHORT).show()
        }
        return true
    }

    private fun loadArticleList(sid : String?) {
        // Launch Kotlin Coroutine on Android's main thread
        // Note: better not to use GlobalScope, see:
        // https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-coroutine-scope/index.html
        // An even better solution would be to use the Android livecycle-aware viewmodel
        // instead of attaching the scope to the activity.
        GlobalScope.launch(Dispatchers.Main) {
            try {
                // Execute web request through coroutine call adapter & retrofit
                val url = getArticleListUrl(sid)
                Log.d(tag, url.toString())
                val webResponse = WebAccess.api.getArticlesAsync(url).await()

                if (webResponse.isSuccessful) {
                    // Get the returned & parsed JSON from the web response.
                    // Type specified explicitly here to make it clear that we already
                    // get parsed contents.
                    val articleListResponse: ArticleListResponse? = webResponse.body()
                    Log.d(tag, articleListResponse.toString())
                    // Assign the list to the recycler view. If partsList is null,
                    // assign an empty list to the adapter.
                    var articleList: MutableList<ArticleList> = articleListResponse!!.result
                    // Inform recycler view that data has changed.
                    // Makes sure the view re-renders itself
                    if (isLoading){
                        isLoading = false
                        adapter.update(articleList);
                        // 向上滚动的距离
                        binding.rvArticles.smoothScrollBy(0, 100);
                    }else {
                        isLoading = false
                        adapter.articleList = articleList
                    }
                    adapter.notifyDataSetChanged()
                } else {
                    // Print error information to the console
                    Log.e(tag, "Error ${webResponse.code()}")
                    Toast.makeText(this@MainActivity, "Error ${webResponse.code()}", Toast.LENGTH_LONG).show()
                }
            } catch (e: IOException) {
                // Error with network request
                Log.e(tag, "Exception " + e.printStackTrace())
                Toast.makeText(this@MainActivity, "Exception ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }


    private fun autoRefresh(){
        Log.d(tag, "===========下拉加载刷新==================")
        // 关闭下拉刷新进度条
        binding.srMain.isRefreshing = false
        isLoading = false
        // 加载数据
        loadArticleList(Int.MAX_VALUE.toString() + "")
    }

    private fun itemClicked(item : ArticleList) {
        // Launch article activity, pass sid as string parameter
        val detailActivityIntent = Intent(this, ArticleActivity::class.java)
        detailActivityIntent.putExtra("title", item.title)
        detailActivityIntent.putExtra("sid", item.sid)
        startActivity(detailActivityIntent)
    }

    private fun commentClicked(item : ArticleList) {
        if(item.comments.toInt() > 0) {
            // Launch comment activity, pass sid as string parameter
            val commentActivityIntent = Intent(this, CommentActivity::class.java)
            commentActivityIntent.putExtra("comments", item.comments)
            commentActivityIntent.putExtra("sid", item.sid)
            startActivity(commentActivityIntent)
        }else{
            Toast.makeText(this, "暂时还没评论~", Toast.LENGTH_SHORT).show()
        }
    }
}
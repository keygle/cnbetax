package com.keygle.cnbetax.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.keygle.cnbetax.R
import com.keygle.cnbetax.adapter.CommentAdapter
import com.keygle.cnbetax.bean.CommentList
import com.keygle.cnbetax.bean.CommentListResponse
import com.keygle.cnbetax.databinding.ActivityCommentListBinding
import com.keygle.cnbetax.network.WebAccess
import com.keygle.cnbetax.utils.RequestUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.IOException

class CommentActivity : AppCompatActivity() {
    private val tag : String = MainActivity::class.java.simpleName
    private lateinit var binding: ActivityCommentListBinding
    private lateinit var adapter: CommentAdapter
    private var isLoading = false // 是否正在加载更多
    private var curSid : String? = ""
    private var page: Int = 1 // 分页数
    private var comments: Int = 0 // 总数

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommentListBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        getCommentData(intent);
        // toolbar
        setToolBar()

        binding.rvComments.layoutManager = LinearLayoutManager(this)
        binding.rvComments.setHasFixedSize(true)

        adapter = CommentAdapter(mutableListOf())
        binding.rvComments.adapter = adapter

        binding.rvComments.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    val lastVisibleItemPosition: Int = (binding.rvComments.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
                    // 超过了 当前的
                    if (!isLoading && lastVisibleItemPosition == (binding.rvComments.adapter as CommentAdapter).itemCount - 1 && (binding.rvComments.adapter as CommentAdapter).itemCount > comments ) {
                        // start loadMore
                        isLoading = true;
                        Log.d("last", page.toString())
                        page += 1
                        loadCommentList(curSid, page)
                    }
                }
            }
        })
        // 设置 下拉刷新的 layout
        val swipeRefreshLayout: SwipeRefreshLayout = binding.srComments
        swipeRefreshLayout.setOnRefreshListener { autoRefresh(curSid) }
    }


    private fun setToolBar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbar.title = "评论"
        setSupportActionBar(toolbar)
        supportActionBar!!.setHomeButtonEnabled(true);
        supportActionBar!!.setDisplayHomeAsUpEnabled(true);
    }

    private fun loadCommentList(sid : String?, page : Int) {
        // Launch Kotlin Coroutine on Android's main thread
        // Note: better not to use GlobalScope, see:
        // https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-coroutine-scope/index.html
        // An even better solution would be to use the Android livecycle-aware viewmodel
        // instead of attaching the scope to the activity.
        GlobalScope.launch(Dispatchers.Main) {
            try {
                // Execute web request through coroutine call adapter & retrofit
                val url = RequestUtil.getCommentListUrl(sid, page)
                Log.d(tag, url.toString())
                val webResponse = WebAccess.api.getCommentsAsync(url).await()

                if (webResponse.isSuccessful) {
                    // Get the returned & parsed JSON from the web response.
                    // Type specified explicitly here to make it clear that we already
                    // get parsed contents.
                    val commentListResponse: CommentListResponse? = webResponse.body()
                    Log.d(tag, commentListResponse.toString())
                    // Assign the list to the recycler view. If partsList is null,
                    // assign an empty list to the adapter.
                    var commentList: MutableList<CommentList> = commentListResponse!!.result
                    // Inform recycler view that data has changed.
                    // Makes sure the view re-renders itself
                    if (isLoading && commentList.size > 0){
                        isLoading = false
                        adapter.update(commentList);
                        // 向上滚动的距离
                        binding.rvComments.smoothScrollBy(0, 100);
                    }else {
                        isLoading = false
                        adapter.commentList = commentList
                    }
                    adapter.notifyDataSetChanged()
                } else {
                    // Print error information to the console
                    Log.e(tag, "Error ${webResponse.code()}")
                    Toast.makeText(this@CommentActivity, "Error ${webResponse.code()}", Toast.LENGTH_LONG).show()
                }
            } catch (e: IOException) {
                // Error with network request
                Log.e(tag, "Exception " + e.printStackTrace())
                Toast.makeText(this@CommentActivity, "Exception ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    /**
     * 获得当前文章的评论数据
     */
    private fun getCommentData(intent: Intent?) {
        if (intent != null) {
            val sid = intent.getStringExtra("sid")
            comments = intent.getStringExtra("comments")?.toInt() ?: 0
            Log.d(tag, sid.toString())
            if (!TextUtils.isEmpty(sid)) {
                curSid = sid
                autoRefresh(sid)
                return
            }
        }
        Toast.makeText(this, "error", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun autoRefresh(sid: String?){
        Log.d(tag, "===========下拉加载刷新==================")
        // 关闭下拉刷新进度条
        binding.srComments.isRefreshing = false
        isLoading = false
        // 加载数据
        page = 1
        loadCommentList(sid, page)
    }
    /**
     * 返回按钮 实现
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish() // back button
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
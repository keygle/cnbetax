package com.keygle.cnbetax

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.keygle.cnbetax.adapter.ArticleAdapter
import com.keygle.cnbetax.databinding.ActivityMainBinding
import com.keygle.cnbetax.network.WebAccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.IOException
import java.math.BigInteger
import java.security.MessageDigest


class MainActivity : AppCompatActivity(){
    private val tag : String = MainActivity::class.java.simpleName
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: ArticleAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        /*
         * A LinearLayoutManager is responsible for measuring and positioning item views within a
         * RecyclerView into a linear list. This means that it can produce either a horizontal or
         * vertical list depending on which parameter you pass in to the LinearLayoutManager
         * constructor. By default, if you don'tag specify an orientation, you get a vertical list.
         * In our case, we want a vertical list, so we don'tag need to pass in an orientation flag to
         * the LinearLayoutManager constructor.
         *
         * There are other LayoutManagers available to display your data in uniform grids,
         * staggered grids, and more! See the developer documentation for more details.
         */
        binding.rvArticles.layoutManager = LinearLayoutManager(this)
        /*
         * Use this setting to improve performance if you know that changes in content do not
         * change the child layout size in the RecyclerView
         */
        binding.rvArticles.setHasFixedSize(true)

        // Create the PartAdapter
        // 1st parameter: our generated testData. listOf() generates empty list with correct type
        // 2nd parameter: item click handler function (implemented below) as function parameter
        adapter =
            ArticleAdapter(mutableListOf()) { item: ArticleList ->
                itemClicked(item)
            }
        binding.rvArticles.adapter = adapter

        loadArticlesList()

        // 设置 下拉刷新的 layout
        var swipeRefreshLayout: SwipeRefreshLayout = binding.srMain
        swipeRefreshLayout.setOnRefreshListener { onRefresh() }
    }

    private fun loadArticlesList() {
        // Launch Kotlin Coroutine on Android's main thread
        // Note: better not to use GlobalScope, see:
        // https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-coroutine-scope/index.html
        // An even better solution would be to use the Android livecycle-aware viewmodel
        // instead of attaching the scope to the activity.
        GlobalScope.launch(Dispatchers.Main) {
            try {
                // Execute web request through coroutine call adapter & retrofit
                val url = getArticleListUrl("0")
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
                    adapter.articleList = articleList
                    // Inform recycler view that data has changed.
                    // Makes sure the view re-renders itself
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


    private fun onRefresh(){
        Log.d(tag, "===========下拉刷新==================")
        // 关闭下拉刷新进度条
        binding.srMain.isRefreshing = false
        // 加载数据
        loadArticlesList()
    }



    private fun itemClicked(item : ArticleList) {
        // Test code to add a new item to the list
        // Will be replaced with UI function soon
        //val newPart = PartData(Random.nextLong(0, 999999), "Infrared sensor")
        //addPart(newPart)
        //return

        Toast.makeText(this, "Clicked: ${item.title}", Toast.LENGTH_LONG).show()

//        // Launch second activity, pass part ID as string parameter
//        val showDetailActivityIntent = Intent(this, PartDetailActivity::class.java)
//        //showDetailActivityIntent.putExtra(Intent.EXTRA_TEXT, partItem.id.toString())
//        showDetailActivityIntent.putExtra("title", item.title)
//        showDetailActivityIntent.putExtra("created", item.created)
//        startActivity(showDetailActivityIntent)
    }


    /**
     * 获取 Sid 小于 endSid 文章列表
     * @param endSid 文章Sid
     * @return 文章列表url
     */
    fun getArticleListUrl(endSid: String?): String? {
        val sb = StringBuilder()
        sb.append("app_key=10000")
        sb.append("&end_sid=").append(endSid)
        sb.append("&format=json")
        sb.append("&method=Article.Lists")
        sb.append("&timestamp=").append(System.currentTimeMillis())
        sb.append("&v=2.8.5")
        val signed: String = md5("$sb&mpuffgvbvbttn3Rc")
        sb.append("&sign=").append(signed)
        sb.insert(0, "https://api.cnbeta.com/capi?")
        return sb.toString()
    }

    fun md5(input:String): String {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
    }
}
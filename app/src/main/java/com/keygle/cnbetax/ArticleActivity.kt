package com.keygle.cnbetax

import android.os.Bundle
import android.util.Log
import android.view.MenuItem;
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.keygle.cnbetax.bean.ArticleList
import com.keygle.cnbetax.bean.ArticleListResponse
import com.keygle.cnbetax.databinding.ArticleDetailBinding
import com.keygle.cnbetax.network.WebAccess
import com.keygle.cnbetax.utils.Tools
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.IOException

class ArticleActivity : AppCompatActivity() {
    private val tag : String = MainActivity::class.java.simpleName
    private lateinit var binding: ArticleDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ArticleDetailBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // toolbar
        setToolBar()
    }

    private fun setToolBar() {
        var toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbar.title = "详情"
        setSupportActionBar(toolbar)
        supportActionBar!!.setHomeButtonEnabled(true);
        supportActionBar!!.setDisplayHomeAsUpEnabled(true);
    }

    private fun loadDetail(sid : String?) {
        // Launch Kotlin Coroutine on Android's main thread
        // Note: better not to use GlobalScope, see:
        // https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-coroutine-scope/index.html
        // An even better solution would be to use the Android livecycle-aware viewmodel
        // instead of attaching the scope to the activity.
//        GlobalScope.launch(Dispatchers.Main) {
//            try {
//                // Execute web request through coroutine call adapter & retrofit
//                val url = getDetailUrl(sid)
//                Log.d(tag, url.toString())
//                val webResponse = WebAccess.api.getArticlesAsync(url).await()
//
//                if (webResponse.isSuccessful) {
//                    // Get the returned & parsed JSON from the web response.
//                    // Type specified explicitly here to make it clear that we already
//                    // get parsed contents.
//                    val articleListResponse: ArticleListResponse? = webResponse.body()
//                    Log.d(tag, articleListResponse.toString())
//                    // Assign the list to the recycler view. If partsList is null,
//                    // assign an empty list to the adapter.
//                    var articleList: MutableList<ArticleList> = articleListResponse!!.result
//                    // Inform recycler view that data has changed.
//                    // Makes sure the view re-renders itself
//                    adapter.article = article
//                    adapter.notifyDataSetChanged()
//                } else {
//                    // Print error information to the console
//                    Log.e(tag, "Error ${webResponse.code()}")
//                    Toast.makeText(this@ArticleActivity, "Error ${webResponse.code()}", Toast.LENGTH_LONG).show()
//                }
//            } catch (e: IOException) {
//                // Error with network request
//                Log.e(tag, "Exception " + e.printStackTrace())
//                Toast.makeText(this@ArticleActivity, "Exception ${e.message}", Toast.LENGTH_LONG).show()
//            }
//        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.d("33333", item.itemId.toString())
        when (item.itemId) {
            android.R.id.home -> {
                finish() // back button
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    /**
     * 获取 对应 sid 的文章
     * @param endSid 文章id
     * @return 文章 url
     */
    fun getDetailUrl(sid: String?): String? {
        val sb = StringBuilder()
        sb.append("app_key=10000")
        sb.append("&end_sid=").append(sid)
        sb.append("&format=json")
        sb.append("&method=Article.Lists")
        sb.append("&timestamp=").append(System.currentTimeMillis())
        sb.append("&v=2.8.5")
        val signed: String = Tools.md5("$sb&mpuffgvbvbttn3Rc")
        sb.append("&sign=").append(signed)
        sb.insert(0, "https://api.cnbeta.com/capi?")
        return sb.toString()
    }
}
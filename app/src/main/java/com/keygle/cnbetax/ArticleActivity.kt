package com.keygle.cnbetax

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.text.HtmlCompat
import com.keygle.cnbetax.bean.Detail
import com.keygle.cnbetax.bean.DetailResponse
import com.keygle.cnbetax.databinding.ArticleDetailBinding
import com.keygle.cnbetax.network.WebAccess
import com.keygle.cnbetax.utils.HtmlUtil
import com.keygle.cnbetax.utils.RequestUtil.getDetailUrl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.IOException


class ArticleActivity : AppCompatActivity() {
    private val tag : String = MainActivity::class.java.simpleName
    private var curSid : String? = ""
    private lateinit var binding: ArticleDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ArticleDetailBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        getCurSid(intent);
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
        GlobalScope.launch(Dispatchers.Main) {
            try {
                // Execute web request through coroutine call adapter & retrofit
                val url = getDetailUrl(sid)
                Log.d(tag, url.toString())
                val webResponse = WebAccess.api.getDetailAsync(url).await()

                if (webResponse.isSuccessful) {
                    // Get the returned & parsed JSON from the web response.
                    // Type specified explicitly here to make it clear that we already
                    // get parsed contents.
                    val detailResponse: DetailResponse? = webResponse.body()
                    Log.d("55555", detailResponse.toString())
                    // Assign the list to the recycler view. If partsList is null,
                    // assign an empty list to the adapter.
                    var detail: Detail = detailResponse!!.result
                    // Inform recycler view that data has changed.
                    // Makes sure the view re-renders itself
                    detail.bodytext = HtmlCompat.fromHtml(HtmlUtil.htmlFilter(detail.bodytext), HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
                    detail.hometext = HtmlCompat.fromHtml(HtmlUtil.htmlFilter(detail.hometext), HtmlCompat.FROM_HTML_MODE_LEGACY).toString()

                    binding.detail = detail
                } else {
                    // Print error information to the console
                    Log.e(tag, "Error ${webResponse.code()}")
                    Toast.makeText(this@ArticleActivity, "Error ${webResponse.code()}", Toast.LENGTH_LONG).show()
                }
            } catch (e: IOException) {
                // Error with network request
                Log.e(tag, "Exception " + e.printStackTrace())
                Toast.makeText(this@ArticleActivity, "Exception ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }


    private fun getCurSid(intent: Intent?) {
        if (intent != null) {
            val sid = intent.getStringExtra("sid")
            Log.d(tag, sid.toString())
            if (!TextUtils.isEmpty(sid)) {
                curSid = sid
                loadDetail(sid)
                return
            }
        }
        Toast.makeText(this, "error", Toast.LENGTH_SHORT).show()
        finish()
    }
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
package com.keygle.cnbetax.ui

import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.TextUtils
import android.text.style.QuoteSpan
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import com.keygle.cnbetax.R
import com.keygle.cnbetax.bean.Detail
import com.keygle.cnbetax.bean.DetailResponse
import com.keygle.cnbetax.databinding.ActivityArticleDetailBinding
import com.keygle.cnbetax.network.WebAccess
import com.keygle.cnbetax.utils.HtmlUtil
import com.keygle.cnbetax.utils.ImageGetterUtil
import com.keygle.cnbetax.utils.QuoteSpanClass
import com.keygle.cnbetax.utils.RequestUtil.getDetailUrl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.IOException


class ArticleActivity : AppCompatActivity() {
    private val tag : String = MainActivity::class.java.simpleName
    private var curSid : String? = ""
    private lateinit var binding: ActivityArticleDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityArticleDetailBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        getCurSid(intent);
        // toolbar
        setToolBar()
    }

    private fun setToolBar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbar.title = "详情"
        setSupportActionBar(toolbar)
        supportActionBar!!.setHomeButtonEnabled(true);
        supportActionBar!!.setDisplayHomeAsUpEnabled(true);
    }

    /**
     * 加载远程数据
     */
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
                val webResponse = WebAccess.api.getArticleDetailAsync(url).await()

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
                    // 数据转换
                    var source: String = HtmlCompat.fromHtml(detail.source, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
                    detail.source = "${detail.time}   $source   ${detail.counter}次阅读  ${detail.comments}条评论"
                    detail.hometext = HtmlCompat.fromHtml(HtmlUtil.htmlFilter(detail.hometext), HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
                    binding.detail = detail
                    // 内容 特殊处理
                    displayHtml(HtmlUtil.htmlFilter(detail.bodytext))
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

    /**
     * 获得当前文章的sid
     */
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

    private fun displayHtml(html: String) {
        var tvContent = binding.tvContent
        // Creating object of ImageGetter class you just created
        val imageGetter = ImageGetterUtil(resources, tvContent)

        // Using Html framework to parse html
        val styledText=HtmlCompat.fromHtml(html,
            HtmlCompat.FROM_HTML_MODE_LEGACY,
            imageGetter,null)
        replaceQuoteSpans(styledText as Spannable)
        // to enable image/link clicking
        // stvContent.movementMethod = LinkMovementMethod.getInstance()

        // setting the text after formatting html and downloadig and setting images
        tvContent.text = styledText
    }


    private fun replaceQuoteSpans(spannable: Spannable)
    {
        val quoteSpans: Array<QuoteSpan> =
                spannable.getSpans(0, spannable.length - 1, QuoteSpan::class.java)

        for (quoteSpan in quoteSpans)
        {
            val start: Int = spannable.getSpanStart(quoteSpan)
            val end: Int = spannable.getSpanEnd(quoteSpan)
            val flags: Int = spannable.getSpanFlags(quoteSpan)
            spannable.removeSpan(quoteSpan)
            spannable.setSpan(
                    QuoteSpanClass(
                            // background color
                            ContextCompat.getColor(this, R.color.white),
                            // strip color
                            ContextCompat.getColor(this, R.color.white),
                            // strip width
                            0F, 0F
                    ),
                    start, end, flags
            )
        }
    }


}
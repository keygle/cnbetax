package com.keygle.cnbetax

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.keygle.cnbetax.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.IOException

class MainActivity : AppCompatActivity() {
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
        adapter = ArticleAdapter(listOf()) { item: ArticleList -> itemClicked(item) }
        binding.rvArticles.adapter = adapter

        loadPartsAndUpdateList()
    }



    private fun loadPartsAndUpdateList() {
        // Launch Kotlin Coroutine on Android's main thread
        // Note: better not to use GlobalScope, see:
        // https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-coroutine-scope/index.html
        // An even better solution would be to use the Android livecycle-aware viewmodel
        // instead of attaching the scope to the activity.
        GlobalScope.launch(Dispatchers.Main) {
            try {
                // Execute web request through coroutine call adapter & retrofit
                val webResponse = WebAccess.api.getArticlesAsync().await()

                if (webResponse.isSuccessful) {
                    // Get the returned & parsed JSON from the web response.
                    // Type specified explicitly here to make it clear that we already
                    // get parsed contents.
                    val articleList: List<ArticleList>? = webResponse.body()
                    Log.d(tag, articleList.toString())
                    // Assign the list to the recycler view. If partsList is null,
                    // assign an empty list to the adapter.
                    adapter.articleItemList = articleList ?: listOf()
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

}
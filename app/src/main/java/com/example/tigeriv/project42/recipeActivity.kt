package com.example.tigeriv.project42

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.example.tigeriv.project42.R.id.*

import com.example.tigeriv.project42.dummy.JWilDatastore
import java.net.HttpURLConnection
import android.support.design.widget.BottomNavigationView
import android.view.MenuItem
import android.widget.Toast
import com.google.gson.Gson
import kotlinx.android.synthetic.main.recipes_view.*
import java.io.IOException
import java.net.URL
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import android.os.StrictMode
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.food_list_content.view.*
import kotlinx.android.synthetic.main.recipe_list_content.view.*
import java.text.SimpleDateFormat
import java.util.*


/**
 * An activity representing a list of Pings. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a [foodDetailActivity] representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
class recipeActivity : AppCompatActivity() {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private var twoPane: Boolean = false
    private lateinit var recipeList: Array<Recipes>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        //CHANGE THIS
        setContentView(R.layout.recipes_view)

        val bottomNavigationView = findViewById<View>(R.id.bottom_navigation) as BottomNavigationView

        bottomNavigationView.setOnNavigationItemSelectedListener(
                object : BottomNavigationView.OnNavigationItemSelectedListener {
                    override fun onNavigationItemSelected(item: MenuItem): Boolean {
                        when (item.itemId) {
                            action_favorites -> {
                                val intent = Intent(this@recipeActivity, foodListActivity::class.java).apply {
                                }
                                startActivity(intent)
                            }
                            action_schedules -> {
                                val intent = Intent(this@recipeActivity, addFoodActivity::class.java).apply {
                                }
                                startActivity(intent)
                            }
                            action_music -> {
                                Toast.makeText(this@recipeActivity, "Going To Recipes", Toast.LENGTH_SHORT).show()
                            }
                        }
                        return true
                    }
                })
        this.displayRecipes()
    }

    private fun displayRecipes() {
        try {
            GetRecipesAsyncTask().doItemSearch(JWilDatastore.ITEMS[0].id)
        }
        catch (e: Exception) {
            Toast.makeText(this@recipeActivity, "There must be a food!", Toast.LENGTH_SHORT).show()
            val intent = Intent(this@recipeActivity, foodListActivity::class.java).apply {
            }
            startActivity(intent)
        }
    }

    class SimpleItemRecyclerViewAdapter(private val parentActivity: recipeActivity,
                                        private val values: Array<Recipes>,
                                        private val twoPane: Boolean) :
            RecyclerView.Adapter<recipeActivity.SimpleItemRecyclerViewAdapter.ViewHolder>() {

        private val onClickListener: View.OnClickListener

        init {
            onClickListener = View.OnClickListener { v ->
                val item = v.tag as Recipes
                val intent = Intent(v.context, recipeDetailActivity::class.java).apply {
                    putExtra("ARG_ITEM_ID", item.id.toString())
                }
                v.context.startActivity(intent)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): recipeActivity.SimpleItemRecyclerViewAdapter.ViewHolder {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.recipe_list_content, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: recipeActivity.SimpleItemRecyclerViewAdapter.ViewHolder, position: Int) {
            val recipe = values[position]
            holder.imView.setImageBitmap(BitmapFactory.decodeStream(URL(recipe.image).openConnection().getInputStream()))
            holder.textView.text = (position+1).toString() + ". " + recipe.title + "\nLikes: " + recipe.likes

            with(holder.itemView) {
                tag = recipe
                setOnClickListener(onClickListener)
            }
        }

        override fun getItemCount() = values.size

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val imView: ImageView = view.imageView1
            val textView: TextView = view.recipeView1
        }
    }

    //HTTP
    inner class GetRecipesAsyncTask : AsyncTask<String, String, String>() {

        private lateinit var recipeList: Array<Recipes>
        val CONNECTON_TIMEOUT_MILLISECONDS = 60000

        fun doSearch(){
            val url = "https://spoonacular-recipe-food-nutrition-v1.p.mashape.com/recipes/findByIngredients?fillIngredients=false&ingredients=apples%2Cflour%2Csugar&limitLicense=false&number=5&ranking=1"
            this.execute(url)
        }

        fun doItemSearch(item: String){
            var url = "https://spoonacular-recipe-food-nutrition-v1.p.mashape.com/recipes/findByIngredients?fillIngredients=false&ingredients="
            url += item
            url += "&limitLicense=false&number=10&ranking=1"
            this.execute(url)
        }

        fun getRecipes(): Array<Recipes> {
            return recipeList
        }

        override fun onPreExecute() {
            // Before doInBackground
        }

        override fun doInBackground(vararg urls: String?): String {
            var urlConnection: HttpURLConnection? = null

            try {
                val url = URL(urls[0])

                urlConnection = url.openConnection() as HttpURLConnection
                urlConnection.connectTimeout = CONNECTON_TIMEOUT_MILLISECONDS
                urlConnection.readTimeout = CONNECTON_TIMEOUT_MILLISECONDS
                urlConnection.setRequestProperty("X-Mashape-Key", "MarfIAWsILmshwHK3REjnanESl7Yp1EHJeFjsnFmIwlE1Rqyx0")
                urlConnection.setRequestProperty("Accept", "application/json")
                //urlConnection.headerFields = mapOf(1 to "x", 2 to "y", -1 to "zz")

                //var inString = streamToString(urlConnection.inputStream)

                // replaces need for streamToString()
                val inString = urlConnection.inputStream.bufferedReader().readText()

                publishProgress(inString)
            } catch (ex: Exception) {
                println("HttpURLConnection exception" + ex)
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect()
                }
            }

            return " "
        }

        override fun onProgressUpdate(vararg values: String?) {
            try {
                var recipeData = Gson().fromJson(values[0], Array<Recipes>::class.java)

                recipeList = recipeData

            } catch (ex: Exception) {
                println("JSON parsing exception" + ex.printStackTrace())
            }
        }

        //Set the view
        override fun onPostExecute(result: String?) {
            println(recipeList[0].image)
            println(recipeList[1].image)
            println(recipeList[2].image)
            for (next in recipeList) {
                println(next)
            }
            //Set up recycler view
            recipe_list.adapter = SimpleItemRecyclerViewAdapter(this@recipeActivity, recipeList, twoPane)
        }
    }

    data class Recipes(val id: Int, val title: String, val image: String, val usedIngredientCount: Int, val likes: Int, val missedIngredientCount: Int)
}

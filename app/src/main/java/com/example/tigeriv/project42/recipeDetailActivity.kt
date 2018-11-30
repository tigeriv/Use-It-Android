package com.example.tigeriv.project42

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.os.StrictMode
import android.support.v7.app.AppCompatActivity
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_recipe_detail.*
import java.net.HttpURLConnection
import java.net.URL

class recipeDetailActivity : AppCompatActivity()  {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_detail)

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        button.setOnClickListener {
            val intent = Intent(this@recipeDetailActivity, recipeActivity::class.java).apply {
            }
            startActivity(intent)
        }

        GetRecipeInfoAsyncTask().doItemSearch(intent.getStringExtra("ARG_ITEM_ID"))
    }

    //HTTP
    inner class GetRecipeInfoAsyncTask : AsyncTask<String, String, String>() {

        private lateinit var recipeInfo: Recipe
        val CONNECTON_TIMEOUT_MILLISECONDS = 60000

        fun doItemSearch(item: String){
            var url = "https://spoonacular-recipe-food-nutrition-v1.p.mashape.com/recipes/"
            url += item
            url += "/information"
            this.execute(url)
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
                var recipeData = Gson().fromJson(values[0], Recipe::class.java)

                recipeInfo = recipeData

            } catch (ex: Exception) {
                println("JSON parsing exception" + ex.printStackTrace())
            }
        }

        //Set the view
        override fun onPostExecute(result: String?) {
            println("The ID is: " + intent.getStringExtra("ARG_ITEM_ID"))
            println(recipeInfo)
            recipeTitle.text = recipeInfo.title
            var informationText = "Ready In " + recipeInfo.readyInMinutes.toString() + " Minutes.\nHealth rating: " + recipeInfo.healthScore.toString() + ".\n"
            informationText += "Ingredients: \n"
            for (recipe in recipeInfo.extendedIngredients) {
                informationText += recipe.originalString + "\n"
            }
            info.text = informationText
            steps.text = "Instructions: \n" + recipeInfo.instructions
            picture.setImageBitmap(BitmapFactory.decodeStream(URL(recipeInfo.image).openConnection().getInputStream()))
        }
    }

    data class Recipe(val readyInMinutes: Int, val healthScore: Double,
                      val extendedIngredients: List<Ingredient>, val title: String, val image: String, val instructions: String)
    data class Ingredient(val originalString: String)
}
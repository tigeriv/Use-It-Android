package com.example.tigeriv.project42

import android.arch.lifecycle.MutableLiveData
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.tigeriv.project42.R.id.*

import com.example.tigeriv.project42.dummy.JWilDatastore
import kotlinx.android.synthetic.main.food_list_content.view.*
import kotlinx.android.synthetic.main.activity_food_list.*
import org.json.JSONObject
import java.net.HttpURLConnection
import java.text.SimpleDateFormat
import android.support.annotation.NonNull
import android.support.design.widget.BottomNavigationView
import android.view.MenuItem
import android.widget.Toast
import com.example.tigeriv.project42.dummy.FoodItemResponse
import com.example.tigeriv.project42.dummy.mapToFood
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.ArrayList
import java.util.Date
import java.util.concurrent.CountDownLatch


/**
 * An activity representing a list of Pings. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a [foodDetailActivity] representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
class foodListActivity : AppCompatActivity() {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private var twoPane: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_list)

        setSupportActionBar(toolbar)
        toolbar.title = title

        val bottomNavigationView = findViewById<View>(R.id.bottom_navigation) as BottomNavigationView

        bottomNavigationView.setOnNavigationItemSelectedListener(
                object : BottomNavigationView.OnNavigationItemSelectedListener {
                    override fun onNavigationItemSelected(item: MenuItem): Boolean {
                        when (item.itemId) {
                            action_favorites -> {
                                Toast.makeText(this@foodListActivity, "Going Home", Toast.LENGTH_SHORT).show()}
                            action_schedules -> {
                                val intent = Intent(this@foodListActivity, addFoodActivity::class.java).apply {
                                }
                                startActivity(intent)
                            }
                            action_music -> {
                                val intent = Intent(this@foodListActivity, recipeActivity::class.java).apply {
                                }
                                startActivity(intent)
                            }
                        }
                        return true
                    }
                })

        // Write a message to the database
        val database = FirebaseDatabase.getInstance()

        // Enable local persistence
        //database.setPersistenceEnabled(true)

        // Get reference to root FoodItems folder
        val foodRef = database.getReference("FoodItems")

        // Set listener to be notified to any changes in TacoStands
        foodRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                println("Snapshot type is:" + dataSnapshot.javaClass.name)

                // Break down dataSnapshot and convert to model objects
                dataSnapshot.run {

                    // Iterate through snapshot and build list of responses
                    val foods = children.mapNotNull {
                        it.getValue(FoodItemResponse::class.java)
                    }

                    // Iterate through responses and convert to TacoStands
                    var itemList = foods.map(FoodItemResponse::mapToFood)
                    for (food in itemList) {
                        if (!JWilDatastore.ITEM_MAP.containsKey(food.name))
                        {
                            JWilDatastore.addItem(JWilDatastore.createFoodItem(food.name,food.date,food.quantity,food.unit))
                        }

                    }
                }

                println(JWilDatastore.ITEMS)
                setupRecyclerView(food_list)
            }
            override fun onCancelled(error: DatabaseError) {
                println("Something went pretty wrong. FireBase died.")
            }
        })
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        recyclerView.adapter = SimpleItemRecyclerViewAdapter(this, JWilDatastore.ITEMS, twoPane)
    }

    class SimpleItemRecyclerViewAdapter(private val parentActivity: foodListActivity,
                                        private val values: List<JWilDatastore.FoodItem>,
                                        private val twoPane: Boolean) :
            RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder>() {

        private val onClickListener: View.OnClickListener

        init {
            onClickListener = View.OnClickListener { v ->
                val item = v.tag as JWilDatastore.FoodItem
                val intent = Intent(v.context, foodDetailActivity::class.java).apply {
                    putExtra("ARG_ITEM_ID", item.id)
                }
                v.context.startActivity(intent)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.food_list_content, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = values[position]
            val idText = "Food: " + item.id
            val expText = "Expiring: " + item.expDate
            val quaText = "Quantity: " + item.quantity
            val unitText = "Unit: " + item.unit
            holder.idView.text = idText
            holder.expView.text = expText
            holder.quaView.text = quaText
            holder.unitView.text = unitText

            //Set COLOR
            val sdf = SimpleDateFormat("MM/dd/yy")
            val currentDate = Date().time
            val msDay = 24*60*60*1000

            //Compare date. 3 days is black, 7 days is red, else green.
            if ((sdf.parse(item.expDate).time - currentDate) < 3*msDay)
                holder.itemView.setBackgroundResource(R.color.black)
            else if ((sdf.parse(item.expDate).time - currentDate) < 7*msDay)
                holder.itemView.setBackgroundResource(R.color.red)
            else
                holder.itemView.setBackgroundResource(R.color.green)

            with(holder.itemView) {
                tag = item
                setOnClickListener(onClickListener)
            }
        }

        override fun getItemCount() = values.size

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val idView: TextView = view.id_text1
            val quaView: TextView = view.id_text2
            val unitView: TextView = view.id_text3
            val expView: TextView = view.id_text4
        }
    }
}

package com.example.tigeriv.project42

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.NavUtils
import android.widget.Toast
import com.example.tigeriv.project42.dummy.FoodItem1
import kotlinx.android.synthetic.main.activity_add_food.*
import com.example.tigeriv.project42.dummy.JWilDatastore
import com.example.tigeriv.project42.dummy.JWilDatastore.createFoodItem
import com.google.firebase.database.FirebaseDatabase
import java.text.ParseException
import java.text.SimpleDateFormat

class addFoodActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_food)

        fab_add.setOnClickListener {
            val itemName = inputFood.text.toString()
            val itemQua = inputQuantity.text.toString()
            val itemDate = inputDate.text.toString()
            val itemUnit = inputUnit.text.toString()

            //Set the date format
            val format = SimpleDateFormat("MM/dd/yy")
            format.isLenient = false

            //Check if valid
            try {
                val date = format.parse(itemDate)
                Toast.makeText(this, "Valid date", Toast.LENGTH_SHORT).show()
                val item = FoodItem1(itemName, itemDate, itemQua, itemUnit)
                //Update Firebase
                JWilDatastore.addItemFirebase(item)
                val intent = Intent(this, foodListActivity::class.java).apply {
                }
                startActivity(intent)
            } catch (e: ParseException) {
                Toast.makeText(this, "Invalid date", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, foodListActivity::class.java).apply {
                }
                startActivity(intent)
            }

        }

        fab_cancel.setOnClickListener {
            val intent = Intent(this, foodListActivity::class.java).apply {
            }
            startActivity(intent)
        }
    }


}

package com.example.tigeriv.project42.dummy

import com.example.tigeriv.project42.dummy.JWilDatastore.ITEM_MAP
import com.google.firebase.database.FirebaseDatabase
import kotlin.math.exp
import java.text.SimpleDateFormat
import java.util.*

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 *
 * TODO: Replace all uses of this class before publishing your app.
 */
object JWilDatastore {

    /**
     * An array of sample (dummy) items.
     */
    val ITEMS: MutableList<FoodItem> = ArrayList()

    /**
     * A map of sample (dummy) items, by ID.
     */
    val ITEM_MAP: MutableMap<String, FoodItem> = HashMap()

    private val COUNT = 0

    init {
        // Add some sample items.
        for (i in 1..COUNT) {
            addItem(createFoodItem(i))
        }
    }

    fun addItem(item: FoodItem) {
        ITEMS.add(item)

        //Sort by date
        val sdf = SimpleDateFormat("MM/dd/yy")
        ITEMS.sortWith<FoodItem>(comparator = Comparator<FoodItem> { x, y -> sdf.parse(x.expDate).compareTo(sdf.parse(y.expDate)) })

        ITEM_MAP[item.id] = item
    }

    fun createFoodItem(position: Int): FoodItem {
        val foods = arrayOf("broccoli", "carrots", "bananas", "eggs", "pasta", "apples", "avocado",
                "rice", "hot dog", "crackers", "bread", "grits", "chicken", "steak", "pizza", "burger",
                "ribs", "biscuit", "pie", "cake", "brownies")
        var input = position.toString()
        return FoodItem(foods[position], (position*2).toString(), (position*4).toString(), input)
    }

    fun createFoodItem(name: String, date: String, quantity: String, unit: String): FoodItem {
        return FoodItem(name, date, quantity, unit)
    }

    /**
     * A dummy item representing a piece of content.
     */
    data class FoodItem(var id: String, var expDate: String, var quantity: String, var unit: String) {
        override fun toString(): String = id
    }

    //Removes an item in firebase
    fun removeItemFirebase(FoodName: String) {
        //Get reference to FireBase
        val database = FirebaseDatabase.getInstance()
        val foodRef = database.getReference("FoodItems")

        // Add as a new entry to TacoStands, using the name as the key
        foodRef.child(FoodName).removeValue()
    }

    //Adds the item to Firebase. Also edits an item of the same name.
    fun addItemFirebase(item: FoodItem1) {
        //Get reference to FireBase
        val database = FirebaseDatabase.getInstance()
        val foodRef = database.getReference("FoodItems")

        // Add as a new entry to TacoStands, using the name as the key
        foodRef.child(item.name).setValue(item)
    }
}

//Data Class
data class FoodItemResponse(
        val name : String = "",
        val date : String = "",
        val quantity : String = "",
        val unit : String = "")

fun FoodItemResponse.mapToFood() = FoodItem1(name, date, quantity, unit)

data class FoodItem1(
        val name : String,
        val date : String,
        val quantity : String,
        val unit : String)
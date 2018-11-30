package com.example.tigeriv.project42

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v4.app.NavUtils
import android.view.MenuItem
import android.widget.Toast
import com.example.tigeriv.project42.dummy.JWilDatastore
import kotlinx.android.synthetic.main.activity_food_detail.*
import java.text.ParseException
import java.text.SimpleDateFormat
import android.support.v4.app.FragmentManager
import com.example.tigeriv.project42.R.id.*
import com.example.tigeriv.project42.dummy.FoodItem1
import kotlinx.android.synthetic.main.activity_food_detail.view.*

/**
 * An activity representing a single food detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a [foodListActivity].
 */
class foodDetailActivity : AppCompatActivity() {

    private var item: JWilDatastore.FoodItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_detail)

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //

        item = JWilDatastore.ITEM_MAP[intent.getStringExtra("ARG_ITEM_ID")]

        // Show the dummy content as text in a TextView.
        item?.let {
            val idText = it.id
            val expText = it.expDate
            val quaText = it.quantity
            val unitText = it.unit
            new_text1.setText(idText)
            new_text2.setText(quaText)
            new_text3.setText(unitText)
            new_text4.setText(expText)
        }

        fab_save.setOnClickListener {

            //Set the date format
            val format = SimpleDateFormat("MM/dd/yy")
            format.isLenient = false
            //Check if valid
            try {
                val date = format.parse(new_text4.text.toString())
                Toast.makeText(this, "Valid date", Toast.LENGTH_SHORT).show()
            } catch (e: ParseException) {
                NavUtils.navigateUpTo(this, Intent(this, foodListActivity::class.java))
                Toast.makeText(this, "Invalid date", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            try {
                var item = JWilDatastore.ITEM_MAP.get(intent.getStringExtra("ARG_ITEM_ID"))
                JWilDatastore.ITEM_MAP.remove(intent.getStringExtra("ARG_ITEM_ID"))
                JWilDatastore.ITEMS.remove(item)
                JWilDatastore.removeItemFirebase(intent.getStringExtra("ARG_ITEM_ID"))
            }

            catch (e: Exception) {}

            try {
                val name = new_text1.text.toString()
                val qua = new_text2.text.toString()
                val unit = new_text3.text.toString()
                val exp = new_text4.text.toString()

                //Update Firebase
                val item = FoodItem1(name, exp, qua, unit)
                JWilDatastore.addItemFirebase(item)
            }
            catch (e: Exception) {}
            NavUtils.navigateUpTo(this, Intent(this, foodListActivity::class.java))
        }

        fab_delete.setOnClickListener {
            try {
                var item = JWilDatastore.ITEM_MAP.get(intent.getStringExtra("ARG_ITEM_ID"))
                JWilDatastore.ITEM_MAP.remove(intent.getStringExtra("ARG_ITEM_ID"))
                JWilDatastore.ITEMS.remove(item)
                JWilDatastore.removeItemFirebase(intent.getStringExtra("ARG_ITEM_ID"))
            }
            catch (e: Exception) {}
            NavUtils.navigateUpTo(this, Intent(this, foodListActivity::class.java))
        }
        fab_cancel.setOnClickListener {
            NavUtils.navigateUpTo(this, Intent(this, foodListActivity::class.java))
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) =
            when (item.itemId) {
                android.R.id.home -> {
                    // This ID represents the Home or Up button. In the case of this
                    // activity, the Up button is shown. Use NavUtils to allow users
                    // to navigate up one level in the application structure. For
                    // more details, see the Navigation pattern on Android Design:
                    //
                    // http://developer.android.com/design/patterns/navigation.html#up-vs-back

                    NavUtils.navigateUpTo(this, Intent(this, foodListActivity::class.java))
                    true
                }
                else -> super.onOptionsItemSelected(item)
            }
}

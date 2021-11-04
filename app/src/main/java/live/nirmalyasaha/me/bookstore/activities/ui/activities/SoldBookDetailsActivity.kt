package live.nirmalyasaha.me.bookstore.activities.ui.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_add_edit_address_crypto.*
import kotlinx.android.synthetic.main.activity_order_details.*
import kotlinx.android.synthetic.main.activity_sold_book_details.*
import live.nirmalyasaha.me.bookstore.R
import live.nirmalyasaha.me.bookstore.firestore.FirestoreClass
import live.nirmalyasaha.me.bookstore.models.SoldBooks
import live.nirmalyasaha.me.bookstore.utils.Constants
import live.nirmalyasaha.me.bookstore.utils.GlideLoader
import java.text.SimpleDateFormat
import java.util.*

class SoldBookDetailsActivity : BaseActivity() {

    private var mbookDetails: SoldBooks = SoldBooks()
    private val requestCall = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sold_book_details)

        //set up the action bar with back icon
        val actionBar = getSupportActionBar()
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24)
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        var bookDetails: SoldBooks = SoldBooks()

        if(intent.hasExtra(Constants.EXTRA_SOLD_BOOK_DETAILS)){
            bookDetails = intent.getParcelableExtra<SoldBooks>(Constants.EXTRA_SOLD_BOOK_DETAILS)!!
            mbookDetails = bookDetails
        }

        //loading the sold book details
        loadSoldBookDetails(bookDetails)

        //click functionality
        btn_submit_status.setOnClickListener {
            updateStatus(bookDetails)
        }

        //going to the maps activity
        ll_address_sold_books.setOnClickListener {
            val intent = Intent(this@SoldBookDetailsActivity, MapsActivity::class.java)
            startActivity(intent)
        }

        //going to the maps activity
        maps.setOnClickListener {
            val intent = Intent(this@SoldBookDetailsActivity, MapsActivity::class.java)
            startActivity(intent)
        }

        //going to the user details activity
        ll_order_details.setOnClickListener {
            val intent = Intent(this, PorfileDetailsActivity::class.java)
            intent.putExtra(Constants.EXTRA_USER_ID, bookDetails.receipt_id)
            startActivity(intent)
        }

        btn_call_sold.setOnClickListener {
            makePhone()
        }
    }

    //function to backpress the action bar
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    //function used to update the status in order collection
    private fun updateStatus(productDetails: SoldBooks){
        //showing the progress bar
        showProgressDialog("Updating Status")

        //getting the fields
        val statusType: String = when {
            rb_pending.isChecked -> {
                Constants.PENDING
            }
            rb_transit.isChecked -> {
                Constants.TRANSIT
            }
            else -> {
                Constants.DELIVERED
            }
        }

        //creating the sold book status hash map
        val soldBookStatusHashMap = HashMap<String, Any>()

        soldBookStatusHashMap[Constants.STATUS] = statusType

        //updating the status in order collection
        FirestoreClass().updateStatus(this, productDetails.order_id, soldBookStatusHashMap)
    }

    //loading the sold book details
    private fun loadSoldBookDetails(productDetails: SoldBooks) {
        //getting the fields
        tv_sold_product_details_id.text = productDetails.order_date.toString()

        // Date Format in which the date will be displayed in the UI.
        val dateFormat = "dd MMM yyyy HH:mm"
        // Create a DateFormatter object for displaying date in specified format.
        val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = productDetails.order_date
        tv_sold_product_details_date.text = formatter.format(calendar.time)

        GlideLoader(this@SoldBookDetailsActivity).loadBookImage(productDetails.image, iv_product_item_image)
        tv_product_item_name.text = productDetails.title
        tv_product_item_price.text ="Rs ${productDetails.price}"
        tv_sold_product_quantity.text = productDetails.sold_quantity

        tv_sold_details_address_type.text = productDetails.address.type
        tv_sold_details_full_name.text = productDetails.address.name
        tv_sold_details_address.text = "${productDetails.address.type} Address: ${productDetails.address.address}"

        tv_sold_details_mobile_number.text = productDetails.address.mobile

        tv_sold_product_sub_total.text = productDetails.sub_total_amount
        tv_sold_product_shipping_charge.text = productDetails.tax
        tv_sold_product_total_amount.text = productDetails.total_amount
    }

    //function on successfully updating the status
    fun successStatusUpdate(){
        //hiding the progress bar
        hideProgressDialog()

        showErrorSnackBar("Succesfully updated the status", false)
    }

    private fun makePhone() {
        val number: String = mbookDetails.address.mobile
        if (number.trim { it <= ' ' }.isNotEmpty()) {
            if (ContextCompat.checkSelfPermission(
                            this@SoldBookDetailsActivity,
                            Manifest.permission.CALL_PHONE
                    ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                        this@SoldBookDetailsActivity,
                        arrayOf(Manifest.permission.CALL_PHONE),
                        requestCall
                )
            } else {
                val dial = "tel:$number"
                startActivity(Intent(Intent.ACTION_CALL, Uri.parse(dial)))
            }
        } else {
            Toast.makeText(this@SoldBookDetailsActivity, "Enter Phone Number", Toast.LENGTH_SHORT).show()
        }
    }
    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String?>,
            grantResults: IntArray
    ) {
        if (requestCode == requestCall) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makePhone()
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
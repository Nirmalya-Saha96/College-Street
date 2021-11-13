package live.nirmalyasaha.me.bookstore.activities.ui.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_order_details.*
import live.nirmalyasaha.me.bookstore.R
import live.nirmalyasaha.me.bookstore.activities.ui.adapter.CartListAdapter
import live.nirmalyasaha.me.bookstore.firestore.FirestoreClass
import live.nirmalyasaha.me.bookstore.models.Order
import live.nirmalyasaha.me.bookstore.utils.Constants
import java.text.SimpleDateFormat
import java.util.*

class OrderDetailsActivity : BaseActivity() {

    private val requestCall = 1
    private var mOrderDetail: Order = Order()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_details)

        //set up the action bar with back icon
        val actionBar = getSupportActionBar()
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24)
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        var mOrderDetails: Order = Order()

        if(intent.hasExtra(Constants.EXTRA_ORDER_DETAILS)){
            mOrderDetails = intent.getParcelableExtra<Order>(Constants.EXTRA_ORDER_DETAILS)!!
            mOrderDetail = mOrderDetails
        }

        //setting the ui
        settingOrderDetails(mOrderDetails)

        //click functionality
        btn_submit_cancel_order.setOnClickListener {
            //show progress bar
            showProgressDialog(resources.getString(R.string.please_wait))

            //cancel an order
            FirestoreClass().cancelOrder(this@OrderDetailsActivity, mOrderDetails.id)
        }

        btn_call_order.setOnClickListener {
            makePhone()
        }

        btn_receive_book.setOnClickListener {
            val intent = Intent(this, GenerateQRCodeActivity::class.java)
            intent.putExtra(Constants.EXTRA_ORDER_ID, mOrderDetails.id)
            startActivity(intent)
        }
    }

    //function to backpress the action bar
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    //setting the ui received from order fragment
    private fun settingOrderDetails(orderDetails: Order){
        //getting the fields
        tv_order_details_id.text = orderDetails.title

        val dateFormat = "dd MMM yyyy HH:mm"
        // Create a DateFormatter object for displaying date in specified format.
        val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
        // Create a calendar object that will convert the date and time value in milliseconds to date.
        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = orderDetails.date
        val orderDateTime = formatter.format(calendar.time)
        tv_order_details_date.text = orderDateTime

        tv_order_status.text = orderDetails.status

        rv_my_order_items_list.layoutManager = LinearLayoutManager(this@OrderDetailsActivity)
        rv_my_order_items_list.setHasFixedSize(true)

        val cartListAdapter = CartListAdapter(this@OrderDetailsActivity, orderDetails.items, false)
        rv_my_order_items_list.adapter = cartListAdapter

        tv_my_order_details_address_type.text = orderDetails.address.type
        tv_my_order_details_full_name.text = orderDetails.address.name
        tv_my_order_details_address.text = "${orderDetails.address.type} Address: ${orderDetails.address.address}"
        tv_my_order_details_mobile_number.text = "Phone No: ${orderDetails.address.mobile}"

        tv_order_details_sub_total.text = "Rs ${orderDetails.sub_total}"
        tv_order_details_shipping_charge.text = "Rs ${orderDetails.tax}"
        tv_order_details_total_amount.text = "Rs ${orderDetails.total_amount}"
    }

    //function on successfully cancelling the order
    //basically used by firestore class
    fun orderCancelSuccess(){
        //hiding the progress bar
        hideProgressDialog()

        showErrorSnackBar("Order cancelled successfully", false)
    }

    private fun makePhone() {
        val number: String = mOrderDetail.address.mobile
        if (number.trim { it <= ' ' }.isNotEmpty()) {
            if (ContextCompat.checkSelfPermission(
                            this@OrderDetailsActivity,
                            Manifest.permission.CALL_PHONE
                    ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                        this@OrderDetailsActivity,
                        arrayOf(Manifest.permission.CALL_PHONE),
                        requestCall
                )
            } else {
                val dial = "tel:$number"
                startActivity(Intent(Intent.ACTION_CALL, Uri.parse(dial)))
            }
        } else {
            Toast.makeText(this@OrderDetailsActivity, "Enter Phone Number", Toast.LENGTH_SHORT).show()
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
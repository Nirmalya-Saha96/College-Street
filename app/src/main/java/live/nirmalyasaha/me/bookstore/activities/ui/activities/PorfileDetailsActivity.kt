package live.nirmalyasaha.me.bookstore.activities.ui.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_order_details.*
import kotlinx.android.synthetic.main.activity_porfile_details.*
import kotlinx.android.synthetic.main.activity_product_book_details.*
import live.nirmalyasaha.me.bookstore.R
import live.nirmalyasaha.me.bookstore.firestore.FirestoreClass
import live.nirmalyasaha.me.bookstore.models.Product
import live.nirmalyasaha.me.bookstore.models.User
import live.nirmalyasaha.me.bookstore.utils.Constants
import live.nirmalyasaha.me.bookstore.utils.GlideLoader

class PorfileDetailsActivity : BaseActivity() {

    private var mProfileId: String = ""
    private var mMobileNumber: String = ""
    private val requestCall = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_porfile_details)

        //set up the action bar with back icon
        val actionBar = getSupportActionBar()
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24)
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        //getting the intent with the user id
        if(intent.hasExtra(Constants.EXTRA_USER_ID)){
            mProfileId = intent.getStringExtra(Constants.EXTRA_USER_ID)!!
            Log.i("User Id", mProfileId)
        }

        //getting all the profile details
        getProfileDetails()

        //donot change any fields
        et_first_name.isEnabled = false
        et_last_name.isEnabled = false
        et_email.isEnabled = false
        et_mobile_number.isEnabled = false
        et_fav_book.isEnabled = false
        et_website.isEnabled = false
        et_profesion.isEnabled = false

        btn_users_call.setOnClickListener {
            makePhone()
        }

        tv_users_books.setOnClickListener {
            val intent = Intent(this, UsersBookActivity::class.java)
            intent.putExtra(Constants.EXTRA_USER_ID, mProfileId)
            startActivity(intent)
        }

        tv_users_reviews.setOnClickListener {
            val intent = Intent(this, UserBlogActivity::class.java)
            intent.putExtra(Constants.EXTRA_USER_ID, mProfileId)
            startActivity(intent)
        }
    }

    //function to backpress the action bar
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    //function used to get the profile details
    private fun getProfileDetails(){
        //showing the progress bar
        showProgressDialog(resources.getString(R.string.please_wait))
        //getting the profile details by passing the user/profile id
        //from the firebase firestore
        FirestoreClass().getProfileDetails(this, mProfileId)
    }

    //on successfully getting the book details
    //basically used by firestore class
    fun getProfileDetailsSuccess(user: User){
        //hiding the progress bar
        hideProgressDialog()

        //setting the values
        GlideLoader(this@PorfileDetailsActivity).loadBookImage(user.image, iv_user_photo)
        et_first_name.setText(user.firstName)
        et_last_name.setText(user.lastName)
        et_email.setText(user.email)
        et_mobile_number.setText(user.mobile.toString())
        mMobileNumber = user.mobile.toString()
        et_fav_book.setText(user.hobby)
        et_website.setText(user.website)
        et_gender.setText(user.gender)
        et_profesion.setText(user.profesion)
    }

    private fun makePhone() {
        val number: String = mMobileNumber
        if (number.trim { it <= ' ' }.isNotEmpty()) {
            if (ContextCompat.checkSelfPermission(
                            this@PorfileDetailsActivity,
                            Manifest.permission.CALL_PHONE
                    ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                        this@PorfileDetailsActivity,
                        arrayOf(Manifest.permission.CALL_PHONE),
                        requestCall
                )
            } else {
                val dial = "tel:$number"
                startActivity(Intent(Intent.ACTION_CALL, Uri.parse(dial)))
            }
        } else {
            Toast.makeText(this@PorfileDetailsActivity, "Enter Phone Number", Toast.LENGTH_SHORT).show()
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
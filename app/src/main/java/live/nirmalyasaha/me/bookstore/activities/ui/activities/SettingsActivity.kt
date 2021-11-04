package live.nirmalyasaha.me.bookstore.activities.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_settings.*
import live.nirmalyasaha.me.bookstore.R
import live.nirmalyasaha.me.bookstore.firestore.FirestoreClass
import live.nirmalyasaha.me.bookstore.models.User
import live.nirmalyasaha.me.bookstore.utils.Constants
import live.nirmalyasaha.me.bookstore.utils.GlideLoader

class SettingsActivity : BaseActivity(), View.OnClickListener {

    private lateinit var mUserDetails: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        //set up the action bar with back icon
        val actionBar = getSupportActionBar()
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24)
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        //setting a click listner
        btn_logout.setOnClickListener(this)
        tv_edit.setOnClickListener(this)
        ll_address.setOnClickListener(this)
        tv_chats.setOnClickListener(this)
        tv_my_blog.setOnClickListener(this)
    }

    //when the activity resumes
    //or after created
    override fun onResume(){
        super.onResume()
        //getting the user details
        UserDetails()
    }

    //function to backpress the action bar
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    //getting the user details from the firebase
    private fun UserDetails(){
        //showing the progress bar
        showProgressDialog(resources.getString(R.string.please_wait))

        //getting the user details
        //from the firestore class functions
        FirestoreClass().getUserDetails(this)
    }

    //on successfully getting all the details from the firebase
    //basically used by the firestore class
    fun getUserDetailsSuccess(user: User){
        //hiding the progress bar
        hideProgressDialog()

        mUserDetails = user

        //setting the fields
        //in the settings activity from the firebase
        GlideLoader(this@SettingsActivity).loadUserImage(user.image, iv_user_photo)
        tv_name.text = "${user.firstName} ${user.lastName}"
        tv_gender.text = "${user.gender}"
        tv_email.text = "${user.email}"
        tv_mobile_number.text = "${user.mobile}"
        tv_fav_book.text = "${user.hobby}"
        tv_profession.text = "${user.profesion}"
        tv_website.text = user.website
    }

    //function used to handle on clicks
    override fun onClick(v: View?) {
        if(v != null){
            when (v.id){
                //when logout
                R.id.btn_logout->{
                    //signout the user
                    FirebaseAuth.getInstance().signOut()
                    //going to login activity
                    //and closing all other activities
                    val intent = Intent(this@SettingsActivity, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
                R.id.tv_edit->{
                    //going to the profile activity
                    val intent = Intent(this@SettingsActivity, ProfileActivity::class.java)
                    intent.putExtra(Constants.USER_EXTRA_DETAILS, mUserDetails)
                    startActivity(intent)
                }
                R.id.ll_address->{
                    //going to the address activity
                    val intent = Intent(this@SettingsActivity, AddressListCryptoActivity::class.java)
                    startActivity(intent)
                }
                R.id.tv_chats->{
                    startActivity(Intent(this@SettingsActivity, MessengerActivity::class.java))
                }
                R.id.tv_my_blog->{
                    startActivity(Intent(this@SettingsActivity, MyBlogReviewActivity::class.java))
                }
            }
        }
    }
}


package live.nirmalyasaha.me.bookstore.activities.ui.activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_add_product.*
import kotlinx.android.synthetic.main.activity_add_review.*
import live.nirmalyasaha.me.bookstore.R
import live.nirmalyasaha.me.bookstore.firestore.FirestoreClass
import live.nirmalyasaha.me.bookstore.models.Review
import live.nirmalyasaha.me.bookstore.models.User
import live.nirmalyasaha.me.bookstore.utils.Constants

class AddReviewActivity : BaseActivity(), View.OnClickListener {

    private var mProductId: String = ""
    private var mImageId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_review)

        //set up the action bar with back icon
        val actionBar = getSupportActionBar()
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24)
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        if(intent.hasExtra(Constants.EXTRA_PRODUCT_ID)){
            mProductId = intent.getStringExtra(Constants.EXTRA_PRODUCT_ID)!!
            Log.i("Product Id", mProductId)
        }

        btn_submit_add_review.setOnClickListener(this)
    }

    //function to backpress the action bar
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onClick(v: View?) {
        if(v != null){
            when (v.id){
                R.id.btn_submit_add_review ->{
                    if(validateReviewDetails()) {
                        //load the user image
                        loadUserImage()
                    }
                }
            }
        }
    }

    private fun uploadReviewDetails(){
        //getting the username from the shared preferences
        val sharedPreferences = getSharedPreferences(Constants.BOOKSTORE_PREFERENCES, Context.MODE_PRIVATE)
        val username = sharedPreferences.getString(Constants.LOGGED_IN_USERNAME, "")!!

        //creating the review model
        //to store the review details
        val review = Review(
            FirestoreClass().getCurrentUserID(),
            username,
            mProductId,
            et_review_description.text.toString().trim() { it <= ' '},
            mImageId
        )

        //hide progress bar is handled by the firestore class

        //uploading the product book details to firebase firestore
        FirestoreClass().uploadReviewBookDetails(this, review)
    }

    //when successfully uploaded the review
    //basically used by firestore class
    fun reviewUploadSuccess(){
        //show a snack bar success message
        showErrorSnackBar("Review uploaded successfully", false)

        //hide the progress bar
        hideProgressDialog()

        //finishing this activity
        finish()
    }

    //function used to load the information of currently logged in user
    //here we are getting the image
    private fun  loadUserImage(){
        //showing the progress bar
        showProgressDialog(resources.getString(R.string.please_wait))
        //getting the information of currently logged in user from the firestore
        FirestoreClass().getUserDetails(this)
    }

    //function used to store the image when successfully getting all the information of the user
    //basically used by firebase firestore class
    fun getLoadUserSuccess(user: User){
        mImageId = user.image

        //uploading the reviews fileds
        uploadReviewDetails()
    }

    //function to validate review data and calls the show error snack bar function in the base activity
    //returns false if the validations are wrong and true if validations are true
    private fun validateReviewDetails(): Boolean {
        return when {
            //if the review description is not entered
            TextUtils.isEmpty(et_review_description.text.toString().trim { it <= ' ' }) -> {
                //calls the show error snack bar function passing the error message and error type
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_review_description), true)
                false
            }
            else -> {
                true
            }
        }
    }
}
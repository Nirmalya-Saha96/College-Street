package live.nirmalyasaha.me.bookstore.activities.ui.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_add_blog_review.*
import kotlinx.android.synthetic.main.activity_add_product.*
import live.nirmalyasaha.me.bookstore.R
import live.nirmalyasaha.me.bookstore.firestore.FirestoreClass
import live.nirmalyasaha.me.bookstore.models.BlogReview
import live.nirmalyasaha.me.bookstore.models.Product
import live.nirmalyasaha.me.bookstore.utils.Constants
import live.nirmalyasaha.me.bookstore.utils.GlideLoader
import java.io.IOException

class EditMyBlogReviewActivity : BaseActivity(), View.OnClickListener {

    private lateinit var mproductD: BlogReview
    private var mSelectedImageURL: Uri? = null
    private var mImageURL: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_blog_review)

        //set up the action bar with back icon
        val actionBar = getSupportActionBar()
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24)
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        if(intent.hasExtra(Constants.BLOG_REVIEW_EXTRA_DETAILS)){
            //get the user details by get parcelable
            mproductD = intent.getParcelableExtra(Constants.BLOG_REVIEW_EXTRA_DETAILS)!!
        }

        GlideLoader(this@EditMyBlogReviewActivity).loadUserImage(mproductD.image, iv_blog_review_image)
        et_blog_review_title.setText(mproductD.title)
        et_blog_review_description.setText(mproductD.description)

        //setting a click listner
        iv_add_update_blog_review.setOnClickListener(this)
        btn_submit_add_blog_review.setOnClickListener(this)
    }

    //function to backpress the action bar
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onClick(v: View?) {
        if(v != null){
            when (v.id){
                R.id.iv_add_update_blog_review->{
                    //checking permission
                    if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                        Constants.showImageChooser(this)
                    }
                    else{
                        //giving permissions
                        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), Constants.READ_STORAGE_PERMISSION_CODE)
                    }
                }
                R.id.btn_submit_add_blog_review->{
                    if(validateBlogDetails()){

                        if(mSelectedImageURL != null){
                            //uploading the book image to firebase storage
                            //and then uploading the data to firebase firestore
                            uploadBookImage()
                        }
                        else{
                            //show progress bar
                            showProgressDialog(resources.getString(R.string.please_wait))
                            //update the product details
                            updateProductDetails()
                        }
                    }
                }
            }
        }
    }

    private fun uploadBookImage(){
        showProgressDialog(resources.getString(R.string.please_wait))
        //uploading the image in the firebase storage
        FirestoreClass().uploadImageToCloudStorage(this, mSelectedImageURL, Constants.BLOG_IMAGE)
    }

    private fun updateProductDetails(){
        //creating a hash map
        //of key value pair
        val productHashMap = HashMap<String, Any>()

        val productTitle = et_blog_review_title.text.toString().trim() { it <= ' '}
        val productDesc = et_blog_review_description.text.toString().trim() { it <= ' '}

        //storing the hash map
        //with a key value pair
        if(productTitle.isNotEmpty()){
            productHashMap[Constants.PRODUCT_TITLE] = productTitle
        }

        if(productDesc.isNotEmpty()){
            productHashMap[Constants.PRODUCT_DECS] = productDesc
        }

        if(mImageURL.isNotEmpty()){
            productHashMap[Constants.IMAGE] = mImageURL
        }

        //hiding the progress bar is handled by the firebase class

        //updating user details in the firebase firestore
        //class
        FirestoreClass().updateBlogDetails(this@EditMyBlogReviewActivity, mproductD.id, productHashMap)
    }

    fun  successUpdateBlogDetails(){
        showErrorSnackBar("Blog Updated Successfully", false)

        //hiding the progress bar
        hideProgressDialog()

        //starting the activity
        finish()
    }

    fun imageUploadSuccess(imageURI: String){
        //storing the image url
        mImageURL = imageURI

        //updating the products books details
        //to firebase firestore
        //after uploading the image to firebase storage
        updateProductDetails()
    }

    private fun validateBlogDetails(): Boolean {
        return when {

            //if the book title is not entered
            TextUtils.isEmpty(et_blog_review_title.text.toString().trim { it <= ' ' }) -> {
                //calls the show error snack bar function passing the error message and error type
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_blog_review_title), true)
                false
            }

            //if the book description is not entered
            TextUtils.isEmpty(et_blog_review_description.text.toString().trim { it <= ' ' }) -> {
                //calls the show error snack bar function passing the error message and error type
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_blog_review_description), true)
                false
            }

            else -> {
                true
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        //if code matches
        if(requestCode == Constants.READ_STORAGE_PERMISSION_CODE){
            //if permission granted
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                showErrorSnackBar("Storage permission granted", false)
                Constants.showImageChooser(this)
            }
            else{
                showErrorSnackBar("Permission denied", true)
            }
        }
    }

    //boiler plate

    //deals with the image chooser
    //pre built function
    //basically used by constants
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode,  resultCode, data)
        //if request is successfully given
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == Constants.IMAGE_REQUEST_CODE){
                if(data != null){
                    //change the image to edit
                    iv_add_update_blog_review.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_vector_edit_photo))
                    try{
                        //getting the uri from the phone storage of the image choosed
                        mSelectedImageURL = data.data!!
                        //setting the image uri to display in the profile page
                        //glide
                        GlideLoader(this).loadUserImage(mSelectedImageURL!!, iv_blog_review_image)
                    }
                    //if any error occured
                    catch (e: IOException){
                        e.printStackTrace()
                        showErrorSnackBar("Failed to load image", true)
                    }
                }
            }
        }
        //if the request is cancelled
        else if(resultCode == Activity.RESULT_CANCELED){
            Log.e("Request Cancelled", "Image Selection Cancelled")
        }
    }
}
package live.nirmalyasaha.me.bookstore.activities.ui.activities

import android.Manifest
import android.app.Activity
import android.content.Context
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
import kotlinx.android.synthetic.main.activity_add_product.*
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.activity_register.*
import live.nirmalyasaha.me.bookstore.R
import live.nirmalyasaha.me.bookstore.firestore.FirestoreClass
import live.nirmalyasaha.me.bookstore.models.Product
import live.nirmalyasaha.me.bookstore.utils.Constants
import live.nirmalyasaha.me.bookstore.utils.GlideLoader
import java.io.IOException

class AddProductActivity : BaseActivity(), View.OnClickListener {

    private var mSelectedImageURL: Uri? = null
    private var mImageURL: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product)

        //set up the action bar with back icon
        val actionBar = getSupportActionBar()
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24)
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        //setting a click listner
        iv_add_update_product.setOnClickListener(this)
        btn_submit_add_product.setOnClickListener(this)
    }

    //function to backpress the action bar
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    //giving an on click funtionality
    override fun onClick(v: View?) {
        if(v != null){
            when (v.id){
                R.id.iv_add_update_product->{
                    //checking permission
                    if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                        Constants.showImageChooser(this)
                    }
                    else{
                        //giving permissions
                        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), Constants.READ_STORAGE_PERMISSION_CODE)
                    }
                }
                R.id.btn_submit_add_product->{
                    if(validateProductDetails()){
                        //uploading the book image to firebase storage
                        //and then uploading the data to firebase firestore
                        uploadBookImage()
                    }
                }
            }
        }
    }

    //function used to upload the product book details to firebase firestore
    //after storing the image
    private fun uploadProductDetails(){
        //getting the username from the shared preferences
        val sharedPreferences = getSharedPreferences(Constants.BOOKSTORE_PREFERENCES, Context.MODE_PRIVATE)
        val username = sharedPreferences.getString(Constants.LOGGED_IN_USERNAME, "")!!

        //creating the products model
        //to store the book information
        val book = Product(
             FirestoreClass().getCurrentUserID(),
             username,
             et_product_title.text.toString().trim() { it <= ' '},
             et_product_author.text.toString().trim() { it <= ' '},
             et_product_price.text.toString().trim() { it <= ' '},
             et_product_description.text.toString().trim() { it <= ' '},
             et_product_quantity.text.toString().trim() { it <= ' '},
             mImageURL
        )

        //hide progress bar is handled by the firestore class

        //uploading the product book details to firebase firestore
        FirestoreClass().uploadProductBookDetails(this, book)
    }

    //function to upload image to firestore storage
    //if successfull then upload the product book information
    private fun uploadBookImage(){
        showProgressDialog(resources.getString(R.string.please_wait))
        //uploading the image in the firebase storage
        FirestoreClass().uploadImageToCloudStorage(this, mSelectedImageURL, Constants.BOOK_IMAGE)
    }

    //function called by firebase class
    //on successfully uploading book information to firebase firestore
    fun productBookUploadSuccess(){
        //show a snack bar success message
        showErrorSnackBar("Book uploaded successfully", false)

        //hide the progress bar
        hideProgressDialog()

        //finishing this activity
        finish()
    }

    //function called by firebase class
    //on successfully uploading the image
    fun imageUploadSuccess(imageURI: String){
        //storing the image url
        mImageURL = imageURI

        //uploading the products books details
        //to firebase firestore
        //after uploading the image to firebase storage
        uploadProductDetails()
    }

    //function to validate users data and calls the show error snack bar function in the base activity
    //returns false if the validations are wrong and true if validations are true
    private fun validateProductDetails(): Boolean {
        return when {

            //if the image is not selected
            mSelectedImageURL == null -> {
                //calls the show error snack bar function passing the error message and error type
                showErrorSnackBar(resources.getString(R.string.err_msg_select_product_image), true)
                false
            }

            //if the book title is not entered
            TextUtils.isEmpty(et_product_title.text.toString().trim { it <= ' ' }) -> {
                //calls the show error snack bar function passing the error message and error type
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_product_title), true)
                false
            }

            //if the book author is not entered
            TextUtils.isEmpty(et_product_author.text.toString().trim { it <= ' ' }) -> {
                //calls the show error snack bar function passing the error message and error type
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_product_author), true)
                false
            }

            //if the book price is not entered
            TextUtils.isEmpty(et_product_price.text.toString().trim { it <= ' ' }) -> {
                //calls the show error snack bar function passing the error message and error type
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_product_price), true)
                false
            }

            //if the book description is not entered
            TextUtils.isEmpty(et_product_description.text.toString().trim { it <= ' ' }) -> {
                //calls the show error snack bar function passing the error message and error type
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_product_description), true)
                false
            }

            //if the book quantity is not entered
            TextUtils.isEmpty(et_product_quantity.text.toString().trim { it <= ' ' }) -> {
                //calls the show error snack bar function passing the error message and error type
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_product_quantity), true)
                false
            }

            //when the book quantity is less than 1
            et_product_quantity.text.toString().trim {it <=' '}.equals('0') ->{
                //calls the show error snack bar function passing the error message and error type
                showErrorSnackBar(resources.getString(R.string.err_msg_product_quantity_length), true)
                false
            }
            else -> {
                true
            }
        }
    }

    //boiler plate code

    //used to check permission
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
                    iv_add_update_product.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_vector_edit_photo))
                    try{
                        //getting the uri from the phone storage of the image choosed
                        mSelectedImageURL = data.data!!
                        //setting the image uri to display in the profile page
                        //glide
                        GlideLoader(this).loadUserImage(mSelectedImageURL!!, iv_product_image)
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
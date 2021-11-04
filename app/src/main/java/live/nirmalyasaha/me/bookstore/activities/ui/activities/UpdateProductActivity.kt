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
import kotlinx.android.synthetic.main.activity_add_product.*
import kotlinx.android.synthetic.main.activity_profile.*
import live.nirmalyasaha.me.bookstore.R
import live.nirmalyasaha.me.bookstore.firestore.FirestoreClass
import live.nirmalyasaha.me.bookstore.models.Product
import live.nirmalyasaha.me.bookstore.utils.Constants
import live.nirmalyasaha.me.bookstore.utils.GlideLoader
import java.io.IOException

class UpdateProductActivity : BaseActivity(), View.OnClickListener {

    private lateinit var mproductD: Product
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

        //getting the user details from user model

        //by parcelise data model
        if(intent.hasExtra(Constants.PRODUCT_EXTRA_DETAILS)){
            //get the user details by get parcelable
            mproductD = intent.getParcelableExtra(Constants.PRODUCT_EXTRA_DETAILS)!!
        }

        //setting the fields
        GlideLoader(this@UpdateProductActivity).loadUserImage(mproductD.image, iv_product_image)
        et_product_title.setText(mproductD.title)
        et_product_author.setText(mproductD.author)
        et_product_price.setText(mproductD.price)
        et_product_description.setText(mproductD.description)
        et_product_quantity.setText(mproductD.quantity)

        //setting a click listner
        iv_add_update_product.setOnClickListener(this)
        btn_submit_add_product.setOnClickListener(this)
    }

    //function to backpress the action bar
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    //click functionality
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

    //function to upload image to firestore storage
    //if successfull then upload the product book information
    private fun uploadBookImage(){
        showProgressDialog(resources.getString(R.string.please_wait))
        //uploading the image in the firebase storage
        FirestoreClass().uploadImageToCloudStorage(this, mSelectedImageURL, Constants.BOOK_IMAGE)
    }

    private fun updateProductDetails(){
        //creating a hash map
        //of key value pair
        val productHashMap = HashMap<String, Any>()

        val productTitle = et_product_title.text.toString().trim() { it <= ' '}
        val productAuthor = et_product_author.text.toString().trim() { it <= ' '}
        val productPrice = et_product_price.text.toString().trim() { it <= ' '}
        val productDesc = et_product_description.text.toString().trim() { it <= ' '}
        val productQuantity = et_product_quantity.text.toString().trim() { it <= ' '}

        //storing the hash map
        //with a key value pair
        if(productTitle.isNotEmpty()){
            productHashMap[Constants.PRODUCT_TITLE] = productTitle
        }

        if(productAuthor.isNotEmpty()){
            productHashMap[Constants.PRODUCT_AUTHOR] = productAuthor
        }

        if(productPrice.isNotEmpty()){
            productHashMap[Constants.PRODUCT_PRICE] = productPrice
        }

        if(productDesc.isNotEmpty()){
            productHashMap[Constants.PRODUCT_DECS] = productDesc
        }

        if(productQuantity.isNotEmpty()){
            productHashMap[Constants.PRODUCT_QUANTITY] = productQuantity
        }

        if(mImageURL.isNotEmpty()){
            productHashMap[Constants.IMAGE] = mImageURL
        }

        //hiding the progress bar is handled by the firebase class

        //updating user details in the firebase firestore
        //class
        FirestoreClass().updateProductData(this, mproductD.product_id, productHashMap)
    }

    //function called by firebase class
    //on successfully uploading the image
    fun imageUploadSuccess(imageURI: String){
        //storing the image url
        mImageURL = imageURI

        //updating the products books details
        //to firebase firestore
        //after uploading the image to firebase storage
        updateProductDetails()
    }

    //function called by firebase class
    //on successfully updated the product
    fun productUpdateSuccess(){
        showErrorSnackBar("Book Updated Successfully", false)

        //hiding the progress bar
        hideProgressDialog()

        //starting the activity
        finish()
    }

    //function to validate users data and calls the show error snack bar function in the base activity
    //returns false if the validations are wrong and true if validations are true
    private fun validateProductDetails(): Boolean {
        return when {

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
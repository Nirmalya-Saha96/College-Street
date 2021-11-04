package live.nirmalyasaha.me.bookstore.activities.ui.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.activity_profile.et_email
import kotlinx.android.synthetic.main.activity_profile.et_first_name
import kotlinx.android.synthetic.main.activity_profile.et_last_name
import kotlinx.android.synthetic.main.activity_profile.iv_user_photo
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_settings.*
import live.nirmalyasaha.me.bookstore.R
import live.nirmalyasaha.me.bookstore.firestore.FirestoreClass
import live.nirmalyasaha.me.bookstore.models.User

import live.nirmalyasaha.me.bookstore.utils.Constants
import live.nirmalyasaha.me.bookstore.utils.GlideLoader
import java.io.IOException

class ProfileActivity : BaseActivity(), View.OnClickListener {

    private lateinit var muserD: User
    private var mSelectedImageFileUri: Uri? = null
    private var mUserProfileImageURI: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        //set up the action bar with back icon
        val actionBar = getSupportActionBar()
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24)
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        //getting the user details from user model

        //by parcelise data model
        if(intent.hasExtra(Constants.USER_EXTRA_DETAILS)){
            //get the user details by get parcelable
             muserD = intent.getParcelableExtra(Constants.USER_EXTRA_DETAILS)!!
        }

        //when user comes for the first time
        if(muserD.profileCompleted == 0){
            tv_titlee.text = resources.getString(R.string.title_complete_profile)
        }

        //when user comes for the second time
        if(muserD.profileCompleted == 1){
            tv_titlee.text = resources.getString(R.string.title_edit_profile)
            GlideLoader(this@ProfileActivity).loadUserImage(muserD.image, iv_user_photo)
        }

        //setting the text
        et_first_name.setText(muserD.firstName)

        et_last_name.setText(muserD.lastName)

        til_emaill.setOnClickListener(this@ProfileActivity)
        //changing the email address is not allowed

        et_email.isEnabled = false
        et_email.setText(muserD.email)

        if(muserD.mobile > 0){
            et_mobile_number.setText(muserD.mobile.toString())
        }

        if(muserD.gender.isNotEmpty()){
            if(muserD.gender == Constants.MALE){
                rb_male.isChecked = true
            }
            else{
                rb_female.isChecked = true
            }
        }

        if(muserD.website.isNotEmpty()){
            et_website.setText(muserD.website.toString())
        }

        if(muserD.profesion.isNotEmpty()){
            if(muserD.profesion == Constants.STUDENT){
                rb_student.isChecked = true
            }
            else{
                rb_employed.isChecked = true
            }
        }

        if(muserD.hobby.isNotEmpty()){
            et_fav_book.setText(muserD.hobby.toString())
        }

        //adding click functionality
        iv_user_photo.setOnClickListener(this@ProfileActivity)

        btn_submit.setOnClickListener(this@ProfileActivity)


    }

    //function to backpress the action bar
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    //giving an on click funtionality
    override fun onClick(v: View){
        if(v != null){
            when (v.id){
                R.id.iv_user_photo->{
                    //checking permission
                    if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                        Constants.showImageChooser(this)
                    }
                    else{
                        //giving permissions
                        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), Constants.READ_STORAGE_PERMISSION_CODE)
                    }
                }
                R.id.btn_submit->{
                    if(validateProfileDetails()){

                        //showing the progress bar
                        showProgressDialog(resources.getString(R.string.please_wait))

                        //hiding the progress bar will be handled by the firebase class

                        //uploading to storage first
                        //is successfully stored then update to firebase database
                        //user collections
                        if(mSelectedImageFileUri != null){
                            //uploading the image in the firebase storage
                            FirestoreClass().uploadImageToCloudStorage(this, mSelectedImageFileUri, Constants.USER_PROFILE_IMAGE)
                        }
                        else{
                            //updating the user profile details
                            updateUserProfileDetails()
                        }
                    }
                }
                R.id.til_emaill->{
                    showErrorSnackBar("You cannot change the email", true)
                }
            }
        }
    }

    //function used to update profile
    //by creating a hash map
    private fun updateUserProfileDetails(){
        //creating a hash map
        //of key value pair
        val userHashMap = HashMap<String, Any>()

        //getting all the fields
        val firstName = et_first_name.text.toString().trim() {it<=' '}
        val lastName = et_last_name.text.toString().trim() {it<=' '}
        val mobileNumber = et_mobile_number.text.toString().trim() {it<=' '}
        val gender = if(rb_male.isChecked){
            Constants.MALE
        }
        else{
            Constants.FEMALE
        }
        val website = et_website.text.toString().trim() {it<=' '}
        val profesion = if(rb_student.isChecked){
            Constants.STUDENT
        }
        else{
            Constants.EMPLOYED
        }
        val hobby = et_fav_book.text.toString().trim() {it<=' '}


        //storing the hash map
        //with a key value pair
        if(firstName.isNotEmpty()){
            userHashMap[Constants.FIRST_NAME] = firstName
        }

        if(lastName.isNotEmpty()){
            userHashMap[Constants.LAST_NAME] = lastName
        }

        if(mUserProfileImageURI.isNotEmpty()){
            userHashMap[Constants.IMAGE] = mUserProfileImageURI
        }

        if(mobileNumber.isNotEmpty()){
            userHashMap[Constants.MOBILE] = mobileNumber.toLong()
        }

        userHashMap[Constants.GENDER] = gender

        if(website.isNotEmpty()){
            userHashMap[Constants.WEBSITE] = website
        }
        userHashMap[Constants.PROFESION] = profesion

        if(hobby.isNotEmpty()){
            userHashMap[Constants.HOBBY] = hobby
        }

        userHashMap[Constants.PROFILE_COMPLETED] = 1

        //hiding the progress bar is handled by the firebase class

        //updating user details in the firebase firestore
        //class
        FirestoreClass().updateUserProfileData(this, userHashMap)
    }

    //function called by firebase class
    //on successfully updated the profile
    fun userProfileUpdateSuccess(){
        showErrorSnackBar(resources.getString(R.string.msg_profile_update_success), false)

        //hiding the progress bar
        hideProgressDialog()

        //starting the activity
        startActivity(Intent(this@ProfileActivity, DashboardActivity::class.java))
        finish()
    }

    //function called by firebase class
    //on successfully uploading the image
    fun imageUploadSuccess(imageURI: String){
        //store the image url
        mUserProfileImageURI = imageURI
        //update the the profile details
        updateUserProfileDetails()
    }

    //function to validate users data and calls the show error snack bar function in the base activity
    //returns false if the validations are wrong and true if validations are true
    private fun validateProfileDetails(): Boolean {
        return when{
            TextUtils.isEmpty(et_first_name.text.toString().trim { it <= ' ' }) -> {
                //calls the show error snack bar function passing the error message and error type
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_first_name), true)
                false
            }
            TextUtils.isEmpty(et_last_name.text.toString().trim { it <= ' ' }) -> {
                //calls the show error snack bar function passing the error message and error type
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_last_name), true)
                false
            }
            TextUtils.isEmpty(et_mobile_number.text.toString().trim { it <= ' ' }) -> {
                //calls the show error snack bar function passing the error message and error type
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_mobile_number), true)
                false
            }
            et_mobile_number.length() < 10 ->{
                //calls the show error snack bar function passing the error message and error type
                showErrorSnackBar(resources.getString(R.string.err_msg_mobile_length), true)
                false
            }
            TextUtils.isEmpty(et_fav_book.text.toString().trim { it <= ' ' }) -> {
                //calls the show error snack bar function passing the error message and error type
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_fav_book), true)
                false
            }
            TextUtils.isEmpty(et_website.text.toString().trim { it <= ' ' }) -> {
                //calls the show error snack bar function passing the error message and error type
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_website), true)
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
                    try{
                        //getting the uri from the phone storage of the image choosed
                        mSelectedImageFileUri = data.data!!
                        //setting the image uri to display in the profile page
                        //glide
                        GlideLoader(this).loadUserImage(mSelectedImageFileUri!!, iv_user_photo)
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
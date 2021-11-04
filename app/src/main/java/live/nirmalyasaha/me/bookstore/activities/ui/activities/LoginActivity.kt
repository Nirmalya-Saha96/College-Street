package live.nirmalyasaha.me.bookstore.activities.ui.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.et_email
import kotlinx.android.synthetic.main.activity_login.et_password
import kotlinx.android.synthetic.main.activity_register.*
import live.nirmalyasaha.me.bookstore.R
import live.nirmalyasaha.me.bookstore.firestore.FirestoreClass
import live.nirmalyasaha.me.bookstore.models.User
import live.nirmalyasaha.me.bookstore.utils.Constants

class LoginActivity : BaseActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //setting the on click listener and adding click event
        tv_forgot_password.setOnClickListener(this)
        btn_login.setOnClickListener(this)
        tv_register.setOnClickListener(this)

    }

    //function used to control all clicks
    override fun onClick(v: View){
        //if not null checks which view is clicked
        if(v != null){
            when (v.id){
                //when clicked on forgot password
                R.id.tv_forgot_password -> {
                    //trigger the register activity by passing through an intent
                    val intent = Intent(this@LoginActivity, ForgotPasswordActivity::class.java)
                    startActivity(intent)
                }

                //when clicked on login button
                R.id.btn_login ->{
                    //login the user
                    loginUser()
                }

                //when clicked on register
                R.id.tv_register ->{
                    //trigger the register activity by passing through an intent
                    val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    //function to validate users data and calls the show error snack bar function in the base activity
    //returns false if the validations are wrong and true if validations are true
    private fun validateLoignDetails(): Boolean {
        return when {
            //when email is not entered
            TextUtils.isEmpty(et_email.text.toString().trim { it <= ' ' }) -> {
                //calls the show error snack bar function passing the error message and error type
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                false
            }

            //when passowrd is not entered
            TextUtils.isEmpty(et_password.text.toString().trim { it <= ' ' }) -> {
                //calls the show error snack bar function passing the error message and error type
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_password), true)
                false
            }

            //when password length is less than 6 letters
            et_password.length() < 6 ->{
                //calls the show error snack bar function passing the error message and error type
                showErrorSnackBar(resources.getString(R.string.err_msg_password_length), true)
                false
            }

            //if validated
            else -> {
                true
            }
        }
    }

    //function to login user
    private fun loginUser(){
        // Check with validate function if the entries are valid or not.
        if(validateLoignDetails()){
            //showing the progress bar
            showProgressDialog(resources.getString(R.string.please_wait))

            //getting the email and the password
            val email: String = et_email.text.toString().trim { it <= ' ' }
            val password: String = et_password.text.toString().trim { it <= ' ' }

            //boiler plate

            //login user from the firebase
            //using this function build in firebase to communicate with firebase
            //passing the email and password
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener{task->

                        //the hide progress bar is handled in firestoreClass

                        //if task is successfull it loads all the user details from the firebase firestore
                        if(task.isSuccessful){
                            FirestoreClass().getUserDetails(this@LoginActivity)
                        }
                        else{
                            //hide the progress bar
                            hideProgressDialog()
                            showErrorSnackBar(task.exception!!.message.toString(), true)
                        }
                    }
        }
    }

    //function used to remove the progress bar
    //mainly used by the firebase firestore class
    fun userLoggedInSuccess(user: User){
        showErrorSnackBar("You are logged in successfully",false)

        //hide the progress bar
        hideProgressDialog()

        if(user.profileCompleted == 0){
            //going to the main activity and finishing this activity
            val intent = Intent(this@LoginActivity, ProfileActivity::class.java)
            intent.putExtra(Constants.USER_EXTRA_DETAILS, user)
            startActivity(intent)
        }
        else{
            //going to the main activity and finishing this activity
            startActivity(Intent(this@LoginActivity, DashboardActivity::class.java))
        }
        finish()
    }
}
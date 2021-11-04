package live.nirmalyasaha.me.bookstore.activities.ui.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_register.*
import live.nirmalyasaha.me.bookstore.R
import live.nirmalyasaha.me.bookstore.firestore.FirestoreClass
import live.nirmalyasaha.me.bookstore.models.User

class RegisterActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        //set up the action bar with back icon
        val actionBar = getSupportActionBar()
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24)
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        //goes to the login activity
        tv_login.setOnClickListener{
            onBackPressed()
        }

        //validate all the details when clicked on register button
        btn_register.setOnClickListener{
            //register the user
            registerUser()
        }
    }

    //function to backpress the action bar
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    //function to validate users data and calls the show error snack bar function in the base activity
    //returns false if the validations are wrong and true if validations are true
    private fun validateRegisterDetails(): Boolean {
        return when {
            //when first name is not entered
            TextUtils.isEmpty(et_first_name.text.toString().trim { it <= ' ' }) -> {
                //calls the show error snack bar function passing the error message and error type
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_first_name), true)
                false
            }

            //when last name is not entered
            TextUtils.isEmpty(et_last_name.text.toString().trim { it <= ' ' }) -> {
                //calls the show error snack bar function passing the error message and error type
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_last_name), true)
                false
            }

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

            //when confirm password is not entered
            TextUtils.isEmpty(et_confirm_password.text.toString().trim { it <= ' ' }) -> {
                //calls the show error snack bar function passing the error message and error type
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_confirm_password), true)
                false
            }

            //when password length is less than 6 letters
            et_password.length() < 6 ->{
                //calls the show error snack bar function passing the error message and error type
                showErrorSnackBar(resources.getString(R.string.err_msg_password_length), true)
                false
            }

            //when confirm password is not matched with password
            et_password.text.toString().trim { it <= ' ' } != et_confirm_password.text.toString()
                .trim { it <= ' ' } -> {
                //calls the show error snack bar function passing the error message and error type
                showErrorSnackBar(resources.getString(R.string.err_msg_password_and_confirm_password_mismatch), true)
                false
            }

            !cb_terms_and_condition.isChecked -> {
                //calls the show error snack bar function passing the error message and error type
                showErrorSnackBar(resources.getString(R.string.err_msg_agree_terms_and_condition), true)
                false
            }

            //if validated
            else -> {
                true
            }
        }
    }

    //function to register user
    private fun registerUser() {

        // Check with validate function if the entries are valid or not.
        if (validateRegisterDetails()) {

            //showing the progress bar
            showProgressDialog(resources.getString(R.string.please_wait))

            //getting the email and the password
            val email: String = et_email.text.toString().trim { it <= ' ' }
            val password: String = et_password.text.toString().trim { it <= ' ' }

            //boiler plate with authentication

            // Create an instance and create a register a user with email and password.
            //using this function build in firebase to communicate with firebase
            //passing the email and password
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(
                            OnCompleteListener<AuthResult> { task ->

                                // If the registration is successfully done
                                //returns the task with auth is given
                                if (task.isSuccessful) {

                                    //in the if part the hide progress bar is handled by the firestore class

                                    // Firebase registered user as a object
                                    val firebaseUser: FirebaseUser = task.result!!.user!!

                                    //creating the users model at time of register
                                    //to store in the firebase firestore
                                    val user = User(
                                        firebaseUser.uid,
                                        et_first_name.text.toString().trim { it <= ' ' },
                                        et_last_name.text.toString().trim { it <= ' ' },
                                        et_email.text.toString().trim { it <= ' ' }
                                    )

                                    //calling the firestore class to store the user details
                                    // in the user collections
                                    FirestoreClass().registerUser(this@RegisterActivity, user)

                                    //and show success message
                                    showErrorSnackBar(
                                            "REGISTERED successfully",
                                            false
                                    )
                                } else {
                                    //hiding the progress bar
                                    hideProgressDialog()
                                    // If the registering is not successful then show error message
                                    //which returns from the task
                                    showErrorSnackBar(task.exception!!.message.toString(), true)
                                }
                            })
        }
    }

    //function to display a toast on registered successfully
    //mainly used by the firestore class
    fun userRegisterSuccess(){
        hideProgressDialog()
        Toast.makeText(this@RegisterActivity, "REGISTERED successfully", Toast.LENGTH_SHORT).show()

        val intent = Intent(this@RegisterActivity, ViewPagerActivity::class.java)
        startActivity(intent)
        finish()
    }
}




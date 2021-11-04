package live.nirmalyasaha.me.bookstore.activities.ui.activities

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.dialogue_progress.*
import live.nirmalyasaha.me.bookstore.R

//we will inherite this class and this will inherite app compact activity
open class BaseActivity : AppCompatActivity() {

    private lateinit var mProgressDialog: Dialog
    private var doubleBackPressed = false

    //function to display snackbar for wrong input and right input
    //basically an alert system
    fun showErrorSnackBar(message: String, errorMessage: Boolean){
        //making the snack bar
        val snackBar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT)
        //making the view
        val snackBarView = snackBar.view

        //if error message then display the wrong input
        if(errorMessage){
            snackBarView.setBackgroundColor(ContextCompat.getColor(this@BaseActivity, R.color.colorSnackBarError))
        }
        //else display the right input
        else{
            snackBarView.setBackgroundColor(ContextCompat.getColor(this@BaseActivity, R.color.colorSnackBarSuccess))
        }

        //display the snackbar
        snackBar.show()
    }

    //function to show progress dialogue bar
    fun showProgressDialog(text: String) {
        mProgressDialog = Dialog(this)

        /*Set the screen content from a layout resource.
        The resource will be inflated, adding all top-level views to the screen.*/
        //basically add the xml file
        mProgressDialog.setContentView(R.layout.dialogue_progress)

        //getting the text
        //or changing the text
        mProgressDialog.tv_progress_text.text = text

        //used for not having any next or back press
        //boiler plate
        mProgressDialog.setCancelable(false)
        mProgressDialog.setCanceledOnTouchOutside(false)

        //Start the dialog and display it on screen.
        mProgressDialog.show()
    }

    //function used to hide progress diagolue
    fun hideProgressDialog() {
        mProgressDialog.dismiss()
    }

    //function to exit on double backed pressed
    fun doubleBackPressedExit(){
        //if true then exit from the application
        if(doubleBackPressed){
            super.onBackPressed()
            return
        }

        //if not set it true
        this.doubleBackPressed = true

        //and ask for double backed pressed
        this.showErrorSnackBar(resources.getString(R.string.double_click), false)

        //then change it to false again after 2.5sec
        //so that if the user presses the back button within 2.5sec then the application will exit
        @Suppress("DEPRECATION")
        Handler().postDelayed({doubleBackPressed = false}, 2500)
    }
}
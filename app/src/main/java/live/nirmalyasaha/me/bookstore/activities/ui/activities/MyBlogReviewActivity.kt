package live.nirmalyasaha.me.bookstore.activities.ui.activities

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_my_blog_review.*
import kotlinx.android.synthetic.main.fargment_product.*
import live.nirmalyasaha.me.bookstore.R
import live.nirmalyasaha.me.bookstore.activities.ui.adapter.MyBlogReviewAdapter
import live.nirmalyasaha.me.bookstore.activities.ui.adapter.MyBookListAdapter
import live.nirmalyasaha.me.bookstore.firestore.FirestoreClass
import live.nirmalyasaha.me.bookstore.models.BlogReview
import live.nirmalyasaha.me.bookstore.models.Product

class MyBlogReviewActivity : BaseActivity() {

    private var mProductId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_blog_review)

        //set up the action bar with back icon
        val actionBar = getSupportActionBar()
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24)
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        fab_switch_add_blog.setOnClickListener {
            startActivity(Intent(this@MyBlogReviewActivity, AddBlogReviewActivity::class.java))
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onResume() {
        super.onResume()
        //getting the cart list
        //getCartList()

        //getting the book list
        getMyBlogList()
    }

    private fun getMyBlogList(){
        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().getMyBlogList(this@MyBlogReviewActivity)
    }

    fun deleteBlog(productID: String){

        mProductId = productID

        //displays a alert dialog box
        //and on success delete the book
        //or on reject dismiss the alert dialog box
        showAlertDialogToDeleteProduct(productID)
    }

    fun successGetMyBlogList(productList: ArrayList<BlogReview>){
        //hiding the progress bar
        hideProgressDialog()

        //if there is a product show it in the recycler view
        if(productList.size > 0){
            //setting the recycler view to visible
            rv_my_blog_review_items.visibility = View.VISIBLE
            //setting the text to not visible
            tv_no_my_blog_review_found.visibility = View.GONE

            //boiler plate

            //setting up the recycler view
            //linear layout
            rv_my_blog_review_items.layoutManager = LinearLayoutManager(this@MyBlogReviewActivity)
            rv_my_blog_review_items.setHasFixedSize(true)
            //setting up the adapter
            val adapterBook = MyBlogReviewAdapter(this@MyBlogReviewActivity, productList, this@MyBlogReviewActivity)
            rv_my_blog_review_items.adapter = adapterBook
        }
        else{
            rv_my_blog_review_items.visibility = View.GONE
            tv_no_my_blog_review_found.visibility = View.VISIBLE
        }
    }

    fun successDeleteBlog(){
        //hiding the progress bar
        hideProgressDialog()

        showErrorSnackBar("Blog Deleted Successfully", false)

        //storing the products
        getMyBlogList()
    }

    private fun showAlertDialogToDeleteProduct(productID: String) {

        val builder = AlertDialog.Builder(this@MyBlogReviewActivity)
        //set title for alert dialog
        builder.setTitle(resources.getString(R.string.delete_dialog_title))
        //set message for alert dialog
        builder.setMessage(resources.getString(R.string.delete_dialog_message))
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        //performing positive action
        builder.setPositiveButton(resources.getString(R.string.yes)) { dialogInterface, _ ->

            // Show the progress dialog.
            showProgressDialog(resources.getString(R.string.please_wait))

            // Call the function of Firestore class.
            FirestoreClass().deleteBlog(this@MyBlogReviewActivity, productID)

            dialogInterface.dismiss()
        }

        //performing negative action
        builder.setNegativeButton(resources.getString(R.string.no)) { dialogInterface, _ ->

            dialogInterface.dismiss()
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false)
        alertDialog.show()
    }
}
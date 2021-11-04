package live.nirmalyasaha.me.bookstore.activities.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.activity_users_book.*
import kotlinx.android.synthetic.main.fragment_dashboard.*
import live.nirmalyasaha.me.bookstore.R
import live.nirmalyasaha.me.bookstore.activities.ui.adapter.DashboardBookListAdapter
import live.nirmalyasaha.me.bookstore.activities.ui.adapter.SearchBookAdapter
import live.nirmalyasaha.me.bookstore.firestore.FirestoreClass
import live.nirmalyasaha.me.bookstore.models.Product
import live.nirmalyasaha.me.bookstore.utils.Constants

class UsersBookActivity : BaseActivity() {

    private var mUserId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_users_book)

        val actionBar = getSupportActionBar()
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24)
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        if(intent.hasExtra(Constants.EXTRA_USER_ID)){
            mUserId = intent.getStringExtra(Constants.EXTRA_USER_ID)!!
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onResume() {
        super.onResume()
        //getting all the book list information
        getUserBookList()
    }

    private fun getUserBookList(){
        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().getUserBookList(this@UsersBookActivity, mUserId)
    }

    fun successGetUserBookList(bookListItems: ArrayList<Product>){
        hideProgressDialog()

        //if there is a product show it in the recycler view
        if(bookListItems.size > 0){
            //setting the recycler view to visible
            rv_user_book_items.visibility = View.VISIBLE
            //setting the text to not visible
            rl_not_found_user_book.visibility = View.GONE

            //boiler plate

            //setting up the recycler view
            //linear layout
            rv_user_book_items.layoutManager = LinearLayoutManager(this@UsersBookActivity)
            rv_user_book_items.setHasFixedSize(true)
            //setting up the adapter
            val adapter = SearchBookAdapter(this@UsersBookActivity, bookListItems)
            rv_user_book_items.adapter = adapter
        }
        else{
            rv_user_book_items.visibility = View.GONE
            rl_not_found_user_book.visibility = View.VISIBLE
        }
    }

}
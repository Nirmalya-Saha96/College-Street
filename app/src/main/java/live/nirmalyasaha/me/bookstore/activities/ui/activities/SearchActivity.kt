package live.nirmalyasaha.me.bookstore.activities.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_add_edit_address_crypto.*
import kotlinx.android.synthetic.main.activity_blog_review.*
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.fragment_dashboard.*
import live.nirmalyasaha.me.bookstore.R
import live.nirmalyasaha.me.bookstore.activities.ui.adapter.BlogReviewAdapter
import live.nirmalyasaha.me.bookstore.activities.ui.adapter.DashboardBookListAdapter
import live.nirmalyasaha.me.bookstore.activities.ui.adapter.SearchBookAdapter
import live.nirmalyasaha.me.bookstore.firestore.FirestoreClass
import live.nirmalyasaha.me.bookstore.models.BlogReview
import live.nirmalyasaha.me.bookstore.models.Product
import live.nirmalyasaha.me.bookstore.utils.Constants

class SearchActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        //set up the action bar with back icon
        val actionBar = getSupportActionBar()
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24)
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        if(intent.hasExtra(Constants.EXTRA_EXTRACTED_TEXT)){
            val text = intent.getStringExtra(Constants.EXTRA_EXTRACTED_TEXT)!!
            et_search_text.setText(text)
            if(validateSearchDetails()){
                getAllBookList()
            }
        }

        btn_search.setOnClickListener{
            if(validateSearchDetails()){
                getAllBookList()
            }
        }

        fab_switch_camera.setOnClickListener{
            startActivity(Intent(this@SearchActivity, MLImageToTextActivity::class.java))
        }
    }

    //function to backpress the action bar
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun getAllBookList(){
        showProgressDialog(resources.getString(R.string.please_wait))

        val searchText: String = et_search_text.text.toString().trim { it <= ' ' }

        val searchType: String = when {
            rb_book_title.isChecked ->{
                Constants.BOOK_TITLE
            }
            rb_book_author.isChecked ->{
                Constants.BOOK_AUTHOR
            }
            else -> {
                Constants.REVIEW_BLOG
            }
        }

        if(searchType == Constants.BOOK_TITLE){
            FirestoreClass().getSearchListItemBookTitle(this@SearchActivity, searchText)
        }
        else if(searchType == Constants.BOOK_AUTHOR){
            FirestoreClass().getSearchListItemBookAuthor(this@SearchActivity, searchText)
        }
        else if(searchType == Constants.REVIEW_BLOG){
            FirestoreClass().getSearchListItemBlog(this@SearchActivity, searchText.toLowerCase())
        }
    }

    fun successGetAllBookList(bookList: ArrayList<Product>){
        hideProgressDialog()

        if(bookList.size > 0){
            rv_search_items.visibility = View.VISIBLE
            rl_search.visibility = View.GONE
            rl_not_found_search.visibility = View.GONE

            rv_search_items.layoutManager = LinearLayoutManager(this@SearchActivity)
            rv_search_items.setHasFixedSize(true)
            //setting up the adapter
            val adapter = SearchBookAdapter(this@SearchActivity, bookList)
            rv_search_items.adapter = adapter
        }
        else{
            rv_search_items.visibility = View.GONE
            rl_search.visibility = View.GONE
            rl_not_found_search.visibility = View.VISIBLE
        }
    }

    fun successGetAllBlogList(bookList: ArrayList<BlogReview>){
        hideProgressDialog()

        if(bookList.size > 0){
            rv_search_items.visibility = View.VISIBLE
            rl_search.visibility = View.GONE
            rl_not_found_search.visibility = View.GONE

            rv_search_items.layoutManager = LinearLayoutManager(this@SearchActivity)
            rv_search_items.setHasFixedSize(true)
            //setting up the adapter
            val adapter = BlogReviewAdapter(this@SearchActivity, bookList)
            rv_search_items.adapter = adapter
        }
        else{
            rv_search_items.visibility = View.GONE
            rl_search.visibility = View.GONE
            rl_not_found_search.visibility = View.VISIBLE
        }
    }

    private fun validateSearchDetails(): Boolean {
        return when {
            //when first name is not entered
            TextUtils.isEmpty(et_search_text.text.toString().trim { it <= ' ' }) -> {
                //calls the show error snack bar function passing the error message and error type
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_search), true)
                false
            }

            //if validated
            else -> {
                true
            }
        }
    }
}
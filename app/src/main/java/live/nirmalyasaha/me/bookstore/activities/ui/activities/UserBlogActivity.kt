package live.nirmalyasaha.me.bookstore.activities.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.activity_user_blog.*
import kotlinx.android.synthetic.main.activity_users_book.*
import live.nirmalyasaha.me.bookstore.R
import live.nirmalyasaha.me.bookstore.activities.ui.adapter.BlogReviewAdapter
import live.nirmalyasaha.me.bookstore.activities.ui.adapter.SearchBookAdapter
import live.nirmalyasaha.me.bookstore.firestore.FirestoreClass
import live.nirmalyasaha.me.bookstore.models.BlogReview
import live.nirmalyasaha.me.bookstore.utils.Constants

class UserBlogActivity : BaseActivity() {

    private var mUserId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_blog)

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
        getUserBlogList()
    }

    private fun getUserBlogList(){
        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().getUserBlogReviewList(this@UserBlogActivity, mUserId)
    }

    fun successGetUserBlogList(blogList: ArrayList<BlogReview>){
        hideProgressDialog()

        if(blogList.size > 0){
            //setting the recycler view to visible
            rv_user_blog_items.visibility = View.VISIBLE
            //setting the text to not visible
            rl_not_found_user_blog.visibility = View.GONE

            //boiler plate

            //setting up the recycler view
            //linear layout
            rv_user_blog_items.layoutManager = LinearLayoutManager(this@UserBlogActivity)
            rv_user_blog_items.setHasFixedSize(true)
            //setting up the adapter
            val adapter = BlogReviewAdapter(this@UserBlogActivity, blogList)
            rv_user_blog_items.adapter = adapter
        }
        else{
            rv_user_blog_items.visibility = View.GONE
            rl_not_found_user_blog.visibility = View.VISIBLE
        }
    }
}
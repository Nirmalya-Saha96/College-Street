package live.nirmalyasaha.me.bookstore.activities.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.activity_blog_review.*
import kotlinx.android.synthetic.main.activity_blog_review.fab_switch_search_blog_review
import kotlinx.android.synthetic.main.activity_blog_review.rv_blog_review_items
import kotlinx.android.synthetic.main.activity_blog_review.tv_no_blog_review_found
import kotlinx.android.synthetic.main.fragment_blog_review.*
import live.nirmalyasaha.me.bookstore.R
import live.nirmalyasaha.me.bookstore.activities.ui.adapter.BlogReviewAdapter
import live.nirmalyasaha.me.bookstore.firestore.FirestoreClass
import live.nirmalyasaha.me.bookstore.models.BlogReview

class BlogReviewActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blog_review)

        title = "Book Blogs/Reviews"

        val actionBar = getSupportActionBar()
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24)
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        //click functionality to go to add products activity
        fab_switch_search_blog_review.setOnClickListener {
            startActivity(Intent(this@BlogReviewActivity, SearchActivity::class.java))
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onResume() {
        super.onResume()
        //getting all the book list information
        getBlogListItem()
    }

    private fun getBlogListItem(){
        showProgressDialog(resources.getString(R.string.please_wait))

        //storing all the book details from the firestore class
        FirestoreClass().getBlogListItem(this@BlogReviewActivity)
    }

    fun successBlogListItem(bookListItems: ArrayList<BlogReview>){
        //hiding the progress bar
        hideProgressDialog()

        //if there is a product show it in the recycler view
        if(bookListItems.size > 0){
            //setting the recycler view to visible
            rv_blog_review_items.visibility = View.VISIBLE
            //setting the text to not visible
            tv_no_blog_review_found.visibility = View.GONE

            //boiler plate

            //setting up the recycler view
            //linear layout
            rv_blog_review_items.layoutManager = LinearLayoutManager(this@BlogReviewActivity)
            rv_blog_review_items.setHasFixedSize(true)
            //setting up the adapter
            val adapter = BlogReviewAdapter(this@BlogReviewActivity, bookListItems)
            rv_blog_review_items.adapter = adapter
        }
        else{
            rv_blog_review_items.visibility = View.GONE
            tv_no_blog_review_found.visibility = View.VISIBLE
        }
    }
}
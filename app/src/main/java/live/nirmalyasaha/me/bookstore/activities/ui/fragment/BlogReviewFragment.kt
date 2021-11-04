package live.nirmalyasaha.me.bookstore.activities.ui.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.fragment_blog_review.*
import kotlinx.android.synthetic.main.fragment_dashboard.*
import live.nirmalyasaha.me.bookstore.R
import live.nirmalyasaha.me.bookstore.activities.ui.activities.ProductBookDetailsActivity
import live.nirmalyasaha.me.bookstore.activities.ui.activities.SearchActivity
import live.nirmalyasaha.me.bookstore.activities.ui.adapter.BlogReviewAdapter
import live.nirmalyasaha.me.bookstore.activities.ui.adapter.DashboardBookListAdapter
import live.nirmalyasaha.me.bookstore.firestore.FirestoreClass
import live.nirmalyasaha.me.bookstore.models.BlogReview
import live.nirmalyasaha.me.bookstore.models.Product
import live.nirmalyasaha.me.bookstore.utils.Constants

class BlogReviewFragment : BaseFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_blog_review, container, false)

//        val switchFab: FloatingActionButton = root.findViewById(R.id.fab_switch_search_blog_review)
//
//        //click functionality to go to add products activity
//        switchFab.setOnClickListener {
//            startActivity(Intent(activity, SearchActivity::class.java))
//        }

        return root
    }

//    override fun onResume() {
//        super.onResume()
//        //getting all the book list information
//        getBlogListItem()
//    }
//
//    private fun getBlogListItem(){
//        showProgressDialog(resources.getString(R.string.please_wait))
//
//        //storing all the book details from the firestore class
//        FirestoreClass().getBlogListItem(this@BlogReviewFragment)
//    }
//
//    fun successBlogListItem(bookListItems: ArrayList<BlogReview>){
//        //hiding the progress bar
//        hideProgressDialog()
//
//        //if there is a product show it in the recycler view
//        if(bookListItems.size > 0){
//            //setting the recycler view to visible
//            rv_blog_review_items.visibility = View.VISIBLE
//            //setting the text to not visible
//            tv_no_blog_review_found.visibility = View.GONE
//
//            //boiler plate
//
//            //setting up the recycler view
//            //linear layout
//            rv_blog_review_items.layoutManager = LinearLayoutManager(activity)
//            rv_blog_review_items.setHasFixedSize(true)
//            //setting up the adapter
//            val adapter = BlogReviewAdapter(requireActivity(), bookListItems)
//            rv_blog_review_items.adapter = adapter
//        }
//        else{
//            rv_blog_review_items.visibility = View.GONE
//            tv_no_blog_review_found.visibility = View.VISIBLE
//        }
//    }
}
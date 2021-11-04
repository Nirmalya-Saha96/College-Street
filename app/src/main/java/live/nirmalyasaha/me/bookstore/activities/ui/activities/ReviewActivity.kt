package live.nirmalyasaha.me.bookstore.activities.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_address_list_crypto.*
import kotlinx.android.synthetic.main.activity_review.*
import kotlinx.android.synthetic.main.fargment_product.*
import live.nirmalyasaha.me.bookstore.R
import live.nirmalyasaha.me.bookstore.activities.ui.adapter.MyBookListAdapter
import live.nirmalyasaha.me.bookstore.activities.ui.adapter.ReviewBookAdapter
import live.nirmalyasaha.me.bookstore.firestore.FirestoreClass
import live.nirmalyasaha.me.bookstore.models.Product
import live.nirmalyasaha.me.bookstore.models.Review
import live.nirmalyasaha.me.bookstore.utils.Constants
import live.nirmalyasaha.me.bookstore.utils.SwipeToDeleteCallback

class ReviewActivity : BaseActivity(), View.OnClickListener {

    private var mProductId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review)

        //set up the action bar with back icon
        val actionBar = getSupportActionBar()
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24)
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        if(intent.hasExtra(Constants.EXTRA_PRODUCT_ID)){
            mProductId = intent.getStringExtra(Constants.EXTRA_PRODUCT_ID)!!
            Log.i("Product Id", mProductId)
        }

        //handling click event
        fab_switch_add_review.setOnClickListener(this)
    }

    //function to backpress the action bar
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    //triggers when the activity loades
    override fun onResume() {
        super.onResume()
        //getting the review information
        getReviewBook()
    }

    //implementing click functionality
    override fun onClick(v: View?) {
        if(v != null){
            when(v.id){
                R.id.fab_switch_add_review ->{
                    val intent = Intent(this, AddReviewActivity::class.java)
                    intent.putExtra(Constants.EXTRA_PRODUCT_ID, mProductId)
                    startActivity(intent)
                }
            }
        }
    }

    //function used to get the review book details
    //from the firebase firestore
    private fun getReviewBook(){
        //showing the progress bar
        showProgressDialog(resources.getString(R.string.please_wait))

        //storing the review details from the firebase firestore by passing the product id
        FirestoreClass().getReviewBook(this, mProductId)
    }

    //on successfully loading the review data of the selected book
    //basically handled by the firestore class
    fun successReviewBook(reviewList: ArrayList<Review>){
        //hiding the progress bar
        hideProgressDialog()

        //if there is a product show it in the recycler view
        if(reviewList.size > 0){
            //setting the recycler view to visible
            rv_my_review_items.visibility = View.VISIBLE
            //setting the text to not visible
            tv_no_review_found.visibility = View.GONE

            //boiler plate

            //setting up the recycler view
            //linear layout
            rv_my_review_items.layoutManager = LinearLayoutManager(this)
            rv_my_review_items.setHasFixedSize(true)
            //setting up the adapter
            val adapterReview = ReviewBookAdapter(this, reviewList)
            rv_my_review_items.adapter = adapterReview

            val deleteSwipeHandler = object: SwipeToDeleteCallback(this){
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int){
                    if(FirestoreClass().getCurrentUserID() == reviewList[viewHolder.adapterPosition].user_id){
                        showProgressDialog(resources.getString(R.string.please_wait))

                        FirestoreClass().deleteReviewSwipe(this@ReviewActivity, reviewList[viewHolder.adapterPosition].review_id)
                    }
                    else{
                        showErrorSnackBar("You can only delete your review", false)
                    }
                }
            }

            val deleteItemTouchHelper = ItemTouchHelper(deleteSwipeHandler)
            deleteItemTouchHelper.attachToRecyclerView(rv_my_review_items)
        }
        else{
            rv_my_review_items.visibility = View.GONE
            tv_no_review_found.visibility = View.VISIBLE
        }
    }

    fun successReviewDelete(){
        hideProgressDialog()

        getReviewBook()

        showErrorSnackBar("Review Deleted Successfully", false)
    }
}
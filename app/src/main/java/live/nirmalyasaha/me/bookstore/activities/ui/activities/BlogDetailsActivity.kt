package live.nirmalyasaha.me.bookstore.activities.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_blog_details.*
import kotlinx.android.synthetic.main.activity_dummy_product_details.*
import live.nirmalyasaha.me.bookstore.R
import live.nirmalyasaha.me.bookstore.firestore.FirestoreClass
import live.nirmalyasaha.me.bookstore.models.BlogReview
import live.nirmalyasaha.me.bookstore.models.Likes
import live.nirmalyasaha.me.bookstore.models.Product
import live.nirmalyasaha.me.bookstore.utils.Constants
import live.nirmalyasaha.me.bookstore.utils.GlideLoader
import java.sql.Array

class BlogDetailsActivity : BaseActivity(), View.OnClickListener {

    private var mProductId: String = ""
    private var mUserId: String = ""
    private var mSize: Int = 0
    private var mProductDescription: String = ""
    private var mProductUserName: String = ""
    private var mProductTitle: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blog_details)

        val actionBar = getSupportActionBar()
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24)
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        if(intent.hasExtra(Constants.EXTRA_BLOG_ID)){
            mProductId = intent.getStringExtra(Constants.EXTRA_BLOG_ID)!!
            Log.i("Product Id", mProductId)
        }

        getBlogDetails()

        tv_blog_details_description.setOnClickListener(this)
        tv_blog_details_user_name.setOnClickListener(this)
        tv_blog_details_description_visibility.setOnClickListener(this)
        iv_likes.setOnClickListener(this)
        iv_unlike.setOnClickListener(this)
        btn_submit_send.setOnClickListener(this)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun getBlogDetails(){
        showProgressDialog(resources.getString(R.string.please_wait))
        //getting the book details by passing the book id
        //from the firebase firestore
        FirestoreClass().getBlogDetails(this@BlogDetailsActivity, mProductId)
    }

    private fun getLikesCount(){
        FirestoreClass().getLikesCount(this@BlogDetailsActivity, mProductId)
    }

    private fun addLike(){

        val like_id: String = "${mProductId}${FirestoreClass().getCurrentUserID()}"

        val like = Likes(
                mProductId,
                FirestoreClass().getCurrentUserID()
        )
        FirestoreClass().addLike(this@BlogDetailsActivity, like, like_id)
    }

    private fun removeLike(){

        val like_id: String = "${mProductId}${FirestoreClass().getCurrentUserID()}"

        FirestoreClass().deleteLike(this@BlogDetailsActivity, like_id)
    }

    private fun shareBlog() {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT,"FROM COLLEGE STREET APP \n \t \t \t \t BOOK REVIEW/BLOG \n \t \t \t \t  by ${mProductUserName} \n \t \t \t \t  ${mProductTitle} \n \n \n ${mProductDescription}")
        val chooser = Intent.createChooser(intent,"Share this blog using...")
        startActivity(chooser)
    }

    fun successGetBlogDetails(product: BlogReview){
        GlideLoader(this@BlogDetailsActivity).loadBookImage(product.image, iv_blog_detail_image)
        mProductDescription = product.description
        mUserId = product.user_id
        mProductUserName = product.user_name
        mProductTitle = product.title
        tv_blog_details_user_name.text = product.user_name
        tv_blog_details_title.text = "${product.title}"
        tv_blog_details_description.text = "${product.description} READ MORE"
        tv_blog_details_description_visibility.text = product.description

        getLikesCount()
    }

    fun successGetLikesCount(bookList: ArrayList<Likes>){
        hideProgressDialog()


        if(bookList.size > 0){
            tv_likes.text = bookList.size.toString()
            mSize = bookList.size

            for(i in bookList){
                if(i.user_id == FirestoreClass().getCurrentUserID()){
                    iv_unlike.visibility = View.VISIBLE
                    iv_likes.visibility = View.GONE
                }
            }
        }
        else{
            tv_likes.text = "0"
            mSize = 0
        }
    }

    fun successAddLike(){
        iv_unlike.visibility = View.VISIBLE
        iv_likes.visibility = View.GONE
        mSize = mSize + 1
        tv_likes.text = mSize.toString()
    }

    fun successDeleteLike(){
        iv_likes.visibility = View.VISIBLE
        iv_unlike.visibility = View.GONE
        mSize = mSize + -1
        tv_likes.text = mSize.toString()
    }

    override fun onClick(v: View?) {
        if(v != null){
            when (v.id){
                R.id.tv_blog_details_user_name ->{
                    val intent = Intent(this@BlogDetailsActivity, PorfileDetailsActivity::class.java)
                    intent.putExtra(Constants.EXTRA_USER_ID, mUserId)
                    startActivity(intent)
                }
                R.id.tv_blog_details_description->{
                    tv_blog_details_description_visibility.visibility = View.VISIBLE
                    tv_blog_details_description.visibility = View.GONE
                }
                R.id.tv_blog_details_description_visibility->{
                    tv_blog_details_description.visibility = View.VISIBLE
                    tv_blog_details_description_visibility.visibility = View.GONE
                }
                R.id.iv_likes->{
                    addLike()
                }
                R.id.iv_unlike->{
                    removeLike()
                }
                R.id.btn_submit_send->{
                    shareBlog()
                }
            }
        }
    }
}
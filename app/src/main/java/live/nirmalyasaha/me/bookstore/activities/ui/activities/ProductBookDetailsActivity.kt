package live.nirmalyasaha.me.bookstore.activities.ui.activities

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.room.Room
import kotlinx.android.synthetic.main.activity_dummy_product_details.*
import kotlinx.android.synthetic.main.activity_product_book_details.*
import live.nirmalyasaha.me.bookstore.R
import live.nirmalyasaha.me.bookstore.database.BookDatabase
import live.nirmalyasaha.me.bookstore.database.BookEntity
import live.nirmalyasaha.me.bookstore.firestore.FirestoreClass
import live.nirmalyasaha.me.bookstore.models.Cart
import live.nirmalyasaha.me.bookstore.models.Product
import live.nirmalyasaha.me.bookstore.utils.Constants
import live.nirmalyasaha.me.bookstore.utils.GlideLoader

class ProductBookDetailsActivity : BaseActivity(), View.OnClickListener {

    private var mProductId: String = ""
    private var mUserId: String = ""
    private lateinit var bookEntity: BookEntity
    private lateinit var mProductDetails: Product
    private var mProductOwnerId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dummy_product_details)

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

        if(intent.hasExtra(Constants.EXTRA_PRODUCT_OWNER_ID)){
            mProductOwnerId = intent.getStringExtra(Constants.EXTRA_PRODUCT_OWNER_ID)!!
            Log.i("User Id", mProductOwnerId)
        }

        //if the product is of the owner he cannot add to cart
        if(FirestoreClass().getCurrentUserID() == mProductOwnerId){
            btn_add_to_cart.visibility = View.GONE
            btn_go_to_cart.visibility = View.GONE
        }
        else{
            btn_add_to_cart.visibility = View.VISIBLE
        }

        //getting the book details
        getBookDetails()

        //setting click event
        tv_product_details_user_name.setOnClickListener(this)
        fab_switch_review.setOnClickListener(this)
        btn_submit_add_fav.setOnClickListener(this)
        btn_add_to_cart.setOnClickListener(this)
        btn_go_to_cart.setOnClickListener(this)
    }

    //function to backpress the action bar
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun getBookDetails(){
        //showing the progress bar
        showProgressDialog(resources.getString(R.string.please_wait))
        //getting the book details by passing the book id
        //from the firebase firestore
        FirestoreClass().getBookDetails(this, mProductId)
    }

    //on successfully getting the book details
    //basically used by firestore class
    fun bookDetailsSuccess(product: Product){
        //storing the product details to store in the cart
        mProductDetails = product

        //setting the values
        GlideLoader(this@ProductBookDetailsActivity).loadBookImage(product.image, iv_product_detail_image)
        mUserId = product.user_id
        tv_product_details_user_name.text = product.user_name
        tv_product_details_title.text = "${product.title}"
        tv_product_details_author.text = "${product.author}"
        tv_product_details_price.text = "Rs ${product.price}"
        tv_product_details_description.text = product.description
        tv_product_details_stock_quantity.text = "Stock Quantity: ${product.quantity}"

        //if the product quantity is 0
        if(product.quantity.toInt() == 0){
            //hide the progress bar
            hideProgressDialog()

            //donot add to cart
            btn_add_to_cart.visibility = View.GONE
            tv_product_details_stock_quantity.text = resources.getString(R.string.lbl_out_of_stock)
            tv_product_details_stock_quantity.setTextColor(ContextCompat.getColor(this@ProductBookDetailsActivity, R.color.colorSnackBarError))
        }
        else{
            //this is basically used by the cart
            //checking if the currently logged in user is the owner of the book
            //so that he cannot add to cart
            if(FirestoreClass().getCurrentUserID() == product.user_id){
                //hiding the progress bar
                hideProgressDialog()
            }
            else{
                //check if the book is already added to cart
                FirestoreClass().checkIfItemExistInCart(this, mProductId)
            }
        }

        //sqlite database

        //creating the sqlite database
        //and setting the entity
        bookEntity = BookEntity(
            mProductId,
            mUserId,
            product.title,
            product.author,
            product.price,
            product.image
        )

        //checking if the book is already in the favourites or not
        //handled by db async task
        val checkFav = DBAsyncTask(applicationContext, bookEntity , 1).execute()
        val isFav = checkFav.get()

        //changing the text and color of the button accordingly
        if(isFav){
            btn_submit_add_fav.text = "Remove from wishlist"
            val favColor = ContextCompat.getColor(applicationContext , R.color.colorSnackBarError)
            btn_submit_add_fav.setBackgroundColor(favColor)
        }else{
            btn_submit_add_fav.text = "Add to wishlist"
            val noFavColor = ContextCompat.getColor(applicationContext , R.color.colorSnackBarSuccess)
            btn_submit_add_fav.setBackgroundColor(noFavColor)
        }

        //button click to add or remove favourites
        btn_submit_add_fav.setOnClickListener{
            //if not present in sqlite database
            if(!DBAsyncTask(applicationContext, bookEntity, 1).execute().get()){
                //add book
                val async = DBAsyncTask(applicationContext, bookEntity, 2).execute()
                val result = async.get()
                //if successfully added
                if(result){
                    showErrorSnackBar("Book added to wishlist", false)
                    //change the text and color
                    btn_submit_add_fav.text = "Remove from wishlist"
                    val favColor = ContextCompat.getColor(applicationContext , R.color.colorSnackBarError)
                    btn_submit_add_fav.setBackgroundColor(favColor)
                }
                else{
                    showErrorSnackBar("Some error occured", true)
                }
            }
            //if present in the sqlite database
            else{
                //remove book
                val async = DBAsyncTask(applicationContext, bookEntity, 3).execute()
                val result = async.get()
                //if successfully removed
                if(result){
                    showErrorSnackBar("Book removed from wishlist", false)
                    //change the text and color
                    btn_submit_add_fav.text = "Add to wishlist"
                    val noFavColor = ContextCompat.getColor(applicationContext , R.color.colorSnackBarSuccess)
                    btn_submit_add_fav.setBackgroundColor(noFavColor)
                }
                else{
                    showErrorSnackBar("Some error occured", true)
                }
            }
        }
    }

    //function used to delete the book from sqlite database if not present in the firebase firestore
    //basically used by the firestore class
    //on failure
    fun bookDetailsFailure(){
        hideProgressDialog()
        //if not present in sqlite database
        if(DBAsyncTask2(applicationContext, mProductId, 1).execute().get()){
            //remove book
            val async = DBAsyncTask2(applicationContext, mProductId, 1).execute()
            val result = async.get()
            //if successfully removed
            if(result){
                showErrorSnackBar("Book removed from wishlist", false)
            }
            else{
                showErrorSnackBar("Error while deleting from wishlist", true)
            }
        }
    }

    //function used to add items to cart
    private fun addToCart(){
        //creating the cart object
        val cart = Cart(
                FirestoreClass().getCurrentUserID(),
                mProductOwnerId,
                mProductId,
                mProductDetails.title,
                mProductDetails.author,
                mProductDetails.price,
                mProductDetails.image,
                Constants.DEFAULT_CART_QUANTITY
        )

        //showing the progress bar
        showProgressDialog(resources.getString(R.string.please_wait))
        //hiding the progress bar is handled by the firestore class

        //add items to cart
        FirestoreClass().addCartItems(this, cart)
    }

    //on successfully adding the item to cart
    //basically used by firestore class
    fun addToCartSuccess(){
        //hiding the pregress bar
        hideProgressDialog()
        showErrorSnackBar("Book added to cart", false)

        btn_add_to_cart.visibility = View.GONE
        btn_go_to_cart.visibility = View.VISIBLE
    }

    //on successfully checking the book is present in cart
    //basically used by firestore class
    fun bookExistsInCart(){
        //hiding the progress bar
        hideProgressDialog()
        btn_add_to_cart.visibility = View.GONE
        btn_go_to_cart.visibility = View.VISIBLE
    }

    //click functionality
    override fun onClick(v: View?) {
        if(v != null){
            when (v.id){
                R.id.tv_product_details_user_name ->{
                    val intent = Intent(this, PorfileDetailsActivity::class.java)
                    intent.putExtra(Constants.EXTRA_USER_ID, mUserId)
                    startActivity(intent)
                }
                R.id.fab_switch_review ->{
                    val intent = Intent(this, ReviewActivity::class.java)
                    intent.putExtra(Constants.EXTRA_PRODUCT_ID, mProductId)
                    startActivity(intent)
                }
                R.id.btn_add_to_cart ->{
                    //add to cart
                    addToCart()
                }
                R.id.btn_go_to_cart ->{
                    startActivity(Intent(this@ProductBookDetailsActivity, CartListActivity::class.java))
                }
            }
        }
    }

    //class used to handle async functions
    //basically used to add book to favourites in the sqlite database
    //receiving the book entity and the mode
    class DBAsyncTask(val context: Context, val bookEntity: BookEntity, val mode: Int): AsyncTask<Void, Void, Boolean>(){

        //initialising the database for storing favourite book in sqlite database
        val db = Room.databaseBuilder(context, BookDatabase::class.java, "books-db").fallbackToDestructiveMigration().build()

        override fun doInBackground(vararg params: Void?): Boolean {
            when(mode){
                //used to check wheather the book is present or not
                1->{
                    //getting the book by id
                    val book: BookEntity? = db.bookDao().getBookById(bookEntity.book_id)
                    //closing the database
                    db.close()
                    return book != null
                }
                //used to insert a book
                2->{
                    //inserting the book entity
                    db.bookDao().insertBook(bookEntity)
                    db.close()
                    return true
                }
                //used to delete a book
                3->{
                    //delete a book by its entity
                    db.bookDao().deleteBook(bookEntity)
                    db.close()
                    return true
                }
            }
            return false
        }

    }

    //class used to delete book when deleted by the owner
    //in the firebae firestore
    //as when clicking the book in the wishlist it showing error as the owner has deleted the book and can't be loaded
    //so this is used to delete the book from sqlite database if clicked on the book
    //if the owner has deleted that book
    class DBAsyncTask2(val context: Context, val productId: String, val mode: Int): AsyncTask<Void, Void, Boolean>(){

        //initialising the database for storing favourite book in sqlite database
        val db = Room.databaseBuilder(context, BookDatabase::class.java, "books-db").fallbackToDestructiveMigration().build()

        override fun doInBackground(vararg params: Void?): Boolean {
            when(mode){
                //used to check wheather the book is present or not
                1->{
                    //getting the book by id
                    val book: BookEntity? = db.bookDao().getBookById(productId)
                    if (book != null) {
                        db.bookDao().deleteBook(book)
                    }
                    //closing the database
                    db.close()
                    return true
                }
            }
            return false
        }

    }

    }

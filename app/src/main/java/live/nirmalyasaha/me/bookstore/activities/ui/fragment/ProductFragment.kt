package live.nirmalyasaha.me.bookstore.activities.ui.fragment

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.activity_product_book_details.*
import kotlinx.android.synthetic.main.fargment_product.*
import live.nirmalyasaha.me.bookstore.R
import live.nirmalyasaha.me.bookstore.activities.ui.activities.AddProductActivity
import live.nirmalyasaha.me.bookstore.activities.ui.activities.BlogReviewActivity
import live.nirmalyasaha.me.bookstore.activities.ui.activities.ProductBookDetailsActivity
import live.nirmalyasaha.me.bookstore.activities.ui.adapter.MyBookListAdapter
import live.nirmalyasaha.me.bookstore.database.BookDatabase
import live.nirmalyasaha.me.bookstore.database.BookEntity
import live.nirmalyasaha.me.bookstore.firestore.FirestoreClass
import live.nirmalyasaha.me.bookstore.models.Product
import live.nirmalyasaha.me.bookstore.utils.GlideLoader

class ProductFragment : BaseFragment() {

    private var mProductId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        //homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fargment_product, container, false)

        val switchFab: FloatingActionButton = root.findViewById(R.id.fab_switch)
        val switchFabBlog: FloatingActionButton = root.findViewById(R.id.fab_switch_blog_review_product)

        //click functionality to go to add products activity
        switchFab.setOnClickListener {
            startActivity(Intent(activity, AddProductActivity::class.java))
        }

        switchFabBlog.setOnClickListener {
            startActivity(Intent(activity, BlogReviewActivity::class.java))
        }

        return root
    }

    //loads the book details when the fragment is triggered
    override fun onResume() {
        super.onResume()
        //loading the book details
        getProductBook()
    }

    //function to get all book details of the currently logged in user
    private fun getProductBook(){
        //showing the progress bar
        showProgressDialog(resources.getString(R.string.please_wait))

        //storing the book details from the firebase firestore
        FirestoreClass().getMyBookList(this)
    }

    //delete a book
    //basically used by the adapter class
    fun deleteBook(productID: String){

        mProductId = productID

        //used to get all the information of the book
        //to delete from sqlite database
        FirestoreClass().getBookDetailsFragment(this, mProductId)

        //displays a alert dialog box
        //and on success delete the book
        //or on reject dismiss the alert dialog box
        showAlertDialogToDeleteProduct(productID)
    }

    //on successfully getting the book details
    //basically used by firestore class
    //used to delete the book from sqlite database
    //to remove from wishlist
    fun bookDetailsSuccessFragment(product: Product){
        //sqlite database

        //creating the sqlite database
        //and setting the entity
        val bookEntity = BookEntity(
                mProductId,
                product.user_id,
                product.title,
                product.author,
                product.price,
                product.image
        )
            //if not present in sqlite database
            if(DBAsyncTask(requireContext(), bookEntity, 1).execute().get()){
                //remove book
                val async = ProductFragment.DBAsyncTask(requireContext(), bookEntity, 3).execute()
                val result = async.get()
                //if successfully removed
                if(result){
                    Toast.makeText(requireActivity(), "Book deleted from wishlist", Toast.LENGTH_SHORT).show()
                }
                else{
                    Toast.makeText(requireActivity(), "Error while deleting book from wishlist", Toast.LENGTH_SHORT).show()
                }
            }
    }

    //on successfully loading the book data of the currently logged in user
    //basically handled by the firestore class
    fun successProductBook(productList: ArrayList<Product>){
        //hiding the progress bar
        hideProgressDialog()

        //if there is a product show it in the recycler view
        if(productList.size > 0){
            //setting the recycler view to visible
            rv_my_product_items.visibility = View.VISIBLE
            //setting the text to not visible
            tv_no_products_found.visibility = View.GONE

            //boiler plate

            //setting up the recycler view
            //linear layout
            rv_my_product_items.layoutManager = LinearLayoutManager(activity)
            rv_my_product_items.setHasFixedSize(true)
            //setting up the adapter
            val adapterBook = MyBookListAdapter(requireActivity(), productList, this)
            rv_my_product_items.adapter = adapterBook
        }
        else{
            rv_my_product_items.visibility = View.GONE
            tv_no_products_found.visibility = View.VISIBLE
        }
    }

    //on successfully deleting the book
    //basically used by firestore class
    fun bookDeleteSuccess(){
        //hiding the progress bar
        hideProgressDialog()

        Toast.makeText(requireActivity(), "Book Deleted", Toast.LENGTH_SHORT).show()

        //storing the products
        getProductBook()
    }

    //function used to show alert dialog box
    //and on success delete the book
    private fun showAlertDialogToDeleteProduct(productID: String) {

        val builder = AlertDialog.Builder(requireActivity())
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
            FirestoreClass().deleteBook(this, productID)

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
}
package live.nirmalyasaha.me.bookstore.activities.ui.fragment

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.fragment_wishlist.*
import live.nirmalyasaha.me.bookstore.R
import live.nirmalyasaha.me.bookstore.activities.ui.activities.BlogReviewActivity
import live.nirmalyasaha.me.bookstore.activities.ui.activities.SearchActivity
import live.nirmalyasaha.me.bookstore.activities.ui.adapter.WishlistAdapter
import live.nirmalyasaha.me.bookstore.database.BookDatabase
import live.nirmalyasaha.me.bookstore.database.BookEntity
import live.nirmalyasaha.me.bookstore.firestore.FirestoreClass


//fragment used to return all the wishlist books
class WishlistFragment : BaseFragment() {

    //declaring the variables
    lateinit var rv_my_fav_items: RecyclerView
    lateinit var progressLayout: RelativeLayout
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: WishlistAdapter
    var dbBookList = listOf<BookEntity>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_wishlist, container, false)
        rv_my_fav_items = root.findViewById(R.id.rv_my_fav_items)
        progressLayout = root.findViewById(R.id.rl_fav_items)
        layoutManager = GridLayoutManager(activity as Context,2)

        //getting the wishlist from the sqlite database
        dbBookList = RetrieveFavourites(activity as Context).execute().get()

        progressLayout.visibility = View.VISIBLE

        //if there is no book in the sqlite database
        if(dbBookList.isNotEmpty()){
            progressLayout.visibility = View.GONE
        }

        //this method is used in only sqlite database
        //not in coming from firebse

        //as because there is no success method
        //error returns the recycler view is null

        //if book present
        if(activity != null){

            //boiler plate

            //setting up the recycler view
            //grid layout
            rv_my_fav_items.visibility = View.VISIBLE
            recyclerAdapter = WishlistAdapter(activity as Context, dbBookList)
            //setting up the adapter
            rv_my_fav_items.adapter = recyclerAdapter
            rv_my_fav_items.layoutManager = layoutManager

        }

        val switchFab: FloatingActionButton = root.findViewById(R.id.fab_switch_search_wishlist)
        val switchFabBlog: FloatingActionButton = root.findViewById(R.id.fab_switch_blog_review_wishlist)

        //click functionality to go to add products activity
        switchFab.setOnClickListener {
            startActivity(Intent(activity, SearchActivity::class.java))
        }

        switchFabBlog.setOnClickListener {
            startActivity(Intent(activity, BlogReviewActivity::class.java))
        }

        return root
    }


    //class used to retrive wishlist from sqlite database
    //in a async task
    class RetrieveFavourites(val context: Context): AsyncTask<Void, Void, List<BookEntity>>(){

        override fun doInBackground(vararg params: Void?): List<BookEntity> {
            //setting up the sqlite database
            val db = Room.databaseBuilder(context, BookDatabase::class.java, "books-db").fallbackToDestructiveMigration().build()

            //getting all the books
            return db.bookDao().getAllBooks()
        }

    }

}
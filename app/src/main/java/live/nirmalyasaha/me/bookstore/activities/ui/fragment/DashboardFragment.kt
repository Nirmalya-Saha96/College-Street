package live.nirmalyasaha.me.bookstore.activities.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.fragment_dashboard.*
import live.nirmalyasaha.me.bookstore.R
import live.nirmalyasaha.me.bookstore.activities.ui.activities.*
import live.nirmalyasaha.me.bookstore.activities.ui.adapter.DashboardBookListAdapter
import live.nirmalyasaha.me.bookstore.firestore.FirestoreClass
import live.nirmalyasaha.me.bookstore.models.Product
import live.nirmalyasaha.me.bookstore.utils.Constants


class DashboardFragment : BaseFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        //dashboardViewModel = ViewModelProvider(this).get(DashboardViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)

        val switchFab: FloatingActionButton = root.findViewById(R.id.fab_switch_search)
        val switchFabBlog: FloatingActionButton = root.findViewById(R.id.fab_switch_blog_review)

        //click functionality to go to add products activity
        switchFab.setOnClickListener {
            startActivity(Intent(activity, SearchActivity::class.java))
        }

        switchFabBlog.setOnClickListener {
            startActivity(Intent(activity, BlogReviewActivity::class.java))
        }

        return root
    }

    //triggers when the fragment calls
    override fun onResume() {
        super.onResume()
        //getting all the book list information
        getBookListItem()
    }

    //function used to store all the book list
    private fun getBookListItem(){
        //showing the progress bar
        showProgressDialog(resources.getString(R.string.please_wait))

        //storing all the book details from the firestore class
        FirestoreClass().getBookListItem(this@DashboardFragment)
    }

    //on successfully loaidng all the items of books in the dashboard
    //basically used by firestore class
    fun successBookListItem(bookListItems: ArrayList<Product>){
        //hiding the progress bar
        hideProgressDialog()

        //if there is a product show it in the recycler view
        if(bookListItems.size > 0){
            //setting the recycler view to visible
            rv_dashboard_items.visibility = View.VISIBLE
            //setting the text to not visible
            tv_no_dashboard_items_found.visibility = View.GONE

            //boiler plate

            //setting up the recycler view
            //linear layout
            rv_dashboard_items.layoutManager = LinearLayoutManager(activity)
            rv_dashboard_items.setHasFixedSize(true)
            //setting up the adapter
            val adapter = DashboardBookListAdapter(requireActivity(), bookListItems)
            rv_dashboard_items.adapter = adapter

            //customised on click listner
            //used to go to product book details page when click on the adapter
            adapter.setOnClickListener(object :
                    DashboardBookListAdapter.OnClickListener {
                override fun onClick(position: Int, product: Product) {
                    val intent = Intent(context, ProductBookDetailsActivity::class.java)
                    intent.putExtra(Constants.EXTRA_PRODUCT_ID, product.product_id)
                    intent.putExtra(Constants.EXTRA_PRODUCT_OWNER_ID, product.user_id)
                    startActivity(intent)
                }
            })
        }
        else{
            rv_dashboard_items.visibility = View.GONE
            tv_no_dashboard_items_found.visibility = View.VISIBLE
        }
    }

    //boiler plate

    //to create the options menu at the top
    //with settings icon
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater){
        inflater.inflate(R.menu.dashboard_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    //when selected
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        //if settings then move to settings activity
        //or cart activity
        when(id){
            R.id.action_settings->{
                startActivity(Intent(activity, SettingsActivity::class.java))
                return true
            }
            R.id.action_cart->{
                startActivity(Intent(activity, CartListActivity::class.java))
            }
            R.id.action_messenger->{
                startActivity(Intent(activity, MessengerActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
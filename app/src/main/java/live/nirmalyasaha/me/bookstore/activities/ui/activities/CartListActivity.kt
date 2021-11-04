package live.nirmalyasaha.me.bookstore.activities.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_cart_list.*
import live.nirmalyasaha.me.bookstore.R
import live.nirmalyasaha.me.bookstore.activities.ui.adapter.CartListAdapter
import live.nirmalyasaha.me.bookstore.firestore.FirestoreClass
import live.nirmalyasaha.me.bookstore.models.Cart
import live.nirmalyasaha.me.bookstore.models.Product
import live.nirmalyasaha.me.bookstore.utils.Constants

class CartListActivity : BaseActivity() {

    private lateinit var mBookList: ArrayList<Product>
    private lateinit var mCartListItems: ArrayList<Cart>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart_list)

        //set up the action bar with back icon
        val actionBar = getSupportActionBar()
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24)
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        //click functionality
        btn_checkout.setOnClickListener{
            //goes to the address list activity
            val intent = Intent(this@CartListActivity, AddressListCryptoActivity::class.java)
            intent.putExtra(Constants.EXTRA_SELECT_ADDRESS, true)
            startActivity(intent)
        }
    }

    //when the activity is triggered
    override fun onResume() {
        super.onResume()
        //getting the cart list
        //getCartList()

        //getting the book list
        getBookList()
    }

    //function to backpress the action bar
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    //function used to get the cart list
    private fun getCartList(){
        //showing the progress bar
        //showProgressDialog(resources.getString(R.string.please_wait))

        //getting the cart list from the firebase firestore
        FirestoreClass().getMyCartList(this@CartListActivity)
    }

    //function on successfully loaded the cart list
    //basically used by the firestore class
    fun successCartList(cartList: ArrayList<Cart>){
        //hide the progress bar
        hideProgressDialog()


        //for update and delete automatically once the product is updated or deleted by the owner
        for(cartEle in  cartList){
            var isPresent = false
            for(bookEle in mBookList){
                //if present
                if(cartEle.product_id == bookEle.product_id){
                    isPresent = true
                    //checking if the price is not equal
                    if(cartEle.price != bookEle.price){
                        //then update
                        val cartHashMap = HashMap<String, Any>()
                        cartHashMap[Constants.CART_PRICE] = bookEle.price
                        showProgressDialog(resources.getString(R.string.please_wait))
                        FirestoreClass().updateCart(this, cartEle.id, cartHashMap)
                    }
                }
            }
            //if deleted then delete
            if(!isPresent){
                showProgressDialog(resources.getString(R.string.please_wait))
                FirestoreClass().deleteCartItem(this, cartEle.id)
            }
        }

        //tallying the stock quantity
        //as it was not put earlier
        //and also if updated also displays in the cart
        for(book in mBookList){
            for(cart in cartList){
                if(book.product_id == cart.product_id){
                    cart.stock_quantity = book.quantity
                    cart.price = book.price
                    if(book.quantity.toInt() == 0){
                        cart.cart_quantity = book.quantity
                    }
                }
            }
        }

        mCartListItems = cartList

        if(mCartListItems.size > 0){
            rv_cart_items_list.visibility = View.VISIBLE
            ll_checkout.visibility = View.VISIBLE
            rl_no_items_cart.visibility = View.GONE

            rv_cart_items_list.layoutManager = LinearLayoutManager(this@CartListActivity)
            rv_cart_items_list.setHasFixedSize(true)
            val cartListAdapter = CartListAdapter(this@CartListActivity, mCartListItems, true)
            rv_cart_items_list.adapter = cartListAdapter
            //var tax: Double
            var subtotal: Double = 0.0

            for(ele in mCartListItems){
                val available = ele.stock_quantity.toInt()
                if(available > 0){
                    val price = ele.price.toDouble()
                    val quantity = ele.cart_quantity.toInt()
                    subtotal += (price * quantity)
                }
            }
            val tax = ((subtotal * 18) / 100)
            tv_sub_total.text = "Rs ${subtotal}"
            tv_shipping_charge.text = "Rs ${tax}"

            if(subtotal > 0){
                ll_checkout.visibility = View.VISIBLE

                val total = subtotal + tax
                tv_total_amount.text = "Rs ${total}"
            }
            else{
                ll_checkout.visibility = View.GONE
            }
        }
        else{
            rv_cart_items_list.visibility = View.GONE
            ll_checkout.visibility = View.GONE
            rl_no_items_cart.visibility = View.VISIBLE
        }
    }

    //function used to get all the book list just like dashboard
    //to tally the stock quantity
    private fun getBookList(){
        //showing the progress bar
        showProgressDialog(resources.getString(R.string.please_wait))

        //getting the book list
        FirestoreClass().getAllBookList(this)
    }

    //function on successfully loaded the book list just from the dashboard
    //to tally the stock quantity
    //basically used by the firestore class
    fun successGetBookList(bookList: ArrayList<Product>){
        //hiding the progress bar
        //hideProgressDialog()
        mBookList = bookList
        //getting the cart list
        getCartList()
    }

    //function on successfully deleting the book from cart
    //basically used by firestore class
    fun cartDeleteSuccess(){
        //hiding the progress bar
        hideProgressDialog()
        showErrorSnackBar("Book removed successfully from cart", false)
        //getting the cart list
        getCartList()
    }

    //function on successfully updating the cart with plus and minus cart quantity
    //basically used by firestore class
    fun updateCartSuccess(){
        //hide the progress bar
        hideProgressDialog()
        //getting the cart list
        getCartList()
    }
}
package live.nirmalyasaha.me.bookstore.activities.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Base64
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.fragment_sold_book.*
import live.nirmalyasaha.me.bookstore.R
import live.nirmalyasaha.me.bookstore.activities.ui.activities.BlogReviewActivity
import live.nirmalyasaha.me.bookstore.activities.ui.activities.SearchActivity
import live.nirmalyasaha.me.bookstore.activities.ui.adapter.SoldBookCryptoAdapter
import live.nirmalyasaha.me.bookstore.firestore.FirestoreClass
import live.nirmalyasaha.me.bookstore.models.Cart
import live.nirmalyasaha.me.bookstore.models.Order
import live.nirmalyasaha.me.bookstore.models.SoldBooks
import java.security.NoSuchAlgorithmException
import javax.crypto.Cipher
import javax.crypto.NoSuchPaddingException
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

class SoldBookFragment : BaseFragment() {

    private lateinit var mOrderList: ArrayList<Order>

    private val encryptionKey = byteArrayOf(9, 115, 51, 86, 105, 4, -31, -23, -68, 88, 17, 20, 3, -105, 119, -53)
    private var cipher: Cipher? = null
    private var decipher: Cipher? = null
    private var secretKeySpec: SecretKeySpec? = null

    private val secretKey = "tK5UTui+DPh8lIlBxya5XVsmeDCoUl6vHhdIESMB6sQ="
    private val salt = "QWlGNHNhMTJTQWZ2bGhpV3U=" // base64 decode => AiF4sa12SAfvlhiWu
    private val iv = "bVQzNFNhRkQ1Njc4UUFaWA==" // base64 decode => mT34SaFD5678QAZX

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_sold_book, container, false)

        try {
            cipher = Cipher.getInstance("AES")
            decipher = Cipher.getInstance("AES")
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: NoSuchPaddingException) {
            e.printStackTrace()
        }
        secretKeySpec = SecretKeySpec(encryptionKey, "AES")

        val switchFab: FloatingActionButton = root.findViewById(R.id.fab_switch_search_sold_books)
        val switchFabBlog: FloatingActionButton = root.findViewById(R.id.fab_switch_blog_review_sold_books)

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
    override fun onResume(){
        super.onResume()
        //getting all the order list to tally between the cancelled ones and delete those
        getAllOrderList()
    }

    //function used to get all the order list to tally the cancelled one
    private fun getAllOrderList(){
        //show progress bar
        showProgressDialog("Decrypting")

        //getting all the order list form firestore
        FirestoreClass().getAllOrderList(this@SoldBookFragment)
    }

    //getting all the sold books of currently logged in user
    private fun getSoldBookList(){
        //getting all the sold books of currently logged in user from firebase firestore
        FirestoreClass().getSoldBookList(this@SoldBookFragment)
    }

    //function used on getting the order list from firestore successfully
    fun successGetAllOrderList(orderList: ArrayList<Order>){
        mOrderList = orderList

        //getting the sold book list of the currently logged in user
        getSoldBookList()
    }

    //function on successfully getting the sold books of currently logged in user
    //basically used by firestore class
    fun successGetSoldBookList(soldBookList: ArrayList<SoldBooks>){
        //hiding the progress bar
        hideProgressDialog()

        //boiler plate

        if (soldBookList.size > 0) {

            //deleting all the sold books which is cancelled

            for(i in soldBookList){
                var present = false
                for (j in mOrderList){
                    if(j.id == i.order_id){
                        present = true
                    }
                }
                if(present == false){
                    //show progress bar
                    showProgressDialog("Order Cancelled")

                    FirestoreClass().deleteSoldBookCancelled(this@SoldBookFragment, i.id)
                }
            }

            rv_sold_product_items.visibility = View.VISIBLE
            tv_no_sold_products_found.visibility = View.GONE

            rv_sold_product_items.layoutManager = LinearLayoutManager(activity)
            rv_sold_product_items.setHasFixedSize(true)

            val soldProductsListAdapter = SoldBookCryptoAdapter(requireActivity(), soldBookList)
            rv_sold_product_items.adapter = soldProductsListAdapter
        } else {
            rv_sold_product_items.visibility = View.GONE
            tv_no_sold_products_found.visibility = View.VISIBLE
        }
    }

    //function on successfully deleting the sold book
    //which is cancelled
    //basically used by firestore class
    fun successDeleteCancelOrder(){
        //hide progress bar
        hideProgressDialog()

        //getting all the order list
        getAllOrderList()
    }

    //function used to decrypt the data using cryptography AES technology
    fun decrypt(strToDecrypt : String?) : String? {
        try
        {

            val ivParameterSpec =  IvParameterSpec(Base64.decode(iv, Base64.DEFAULT))

            val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
            val spec =  PBEKeySpec(secretKey.toCharArray(), Base64.decode(salt, Base64.DEFAULT), 10000, 256)
            val tmp = factory.generateSecret(spec);
            val secretKey =  SecretKeySpec(tmp.encoded, "AES")

            val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
            return  String(cipher.doFinal(Base64.decode(strToDecrypt, Base64.DEFAULT)))
        }
        catch (e : Exception) {
            println("Error while decrypting: $e");
        }
        return null
    }
}
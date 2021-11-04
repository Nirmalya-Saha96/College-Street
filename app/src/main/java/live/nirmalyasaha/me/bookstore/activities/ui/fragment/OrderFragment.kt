package live.nirmalyasaha.me.bookstore.activities.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.FtsOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.fragment_order.*
import live.nirmalyasaha.me.bookstore.R
import live.nirmalyasaha.me.bookstore.activities.ui.activities.BlogReviewActivity
import live.nirmalyasaha.me.bookstore.activities.ui.activities.SearchActivity
import live.nirmalyasaha.me.bookstore.activities.ui.adapter.OrderListCryptoAdapter
import live.nirmalyasaha.me.bookstore.firestore.FirestoreClass
import live.nirmalyasaha.me.bookstore.models.Order
import java.security.NoSuchAlgorithmException
import javax.crypto.Cipher
import javax.crypto.NoSuchPaddingException
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec


class OrderFragment : BaseFragment() {

    private val encryptionKey = byteArrayOf(9, 115, 51, 86, 105, 4, -31, -23, -68, 88, 17, 20, 3, -105, 119, -53)
    private var cipher: Cipher? = null
    private var decipher: Cipher? = null
    private var secretKeySpec: SecretKeySpec? = null

    private val secretKey = "tK5UTui+DPh8lIlBxya5XVsmeDCoUl6vHhdIESMB6sQ="
    private val salt = "QWlGNHNhMTJTQWZ2bGhpV3U=" // base64 decode => AiF4sa12SAfvlhiWu
    private val iv = "bVQzNFNhRkQ1Njc4UUFaWA==" // base64 decode => mT34SaFD5678QAZX

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        //notificationsViewModel = ViewModelProvider(this).get(NotificationsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_order, container, false)

        try {
            cipher = Cipher.getInstance("AES")
            decipher = Cipher.getInstance("AES")
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: NoSuchPaddingException) {
            e.printStackTrace()
        }
        secretKeySpec = SecretKeySpec(encryptionKey, "AES")

        val switchFab: FloatingActionButton = root.findViewById(R.id.fab_switch_search_order)
        val switchFabBlog: FloatingActionButton = root.findViewById(R.id.fab_switch_blog_review_order)

        //click functionality to go to add products activity
        switchFab.setOnClickListener {
            startActivity(Intent(activity, SearchActivity::class.java))
        }

        switchFabBlog.setOnClickListener {
            startActivity(Intent(activity, BlogReviewActivity::class.java))
        }

        return root
    }

    //loads the order details when the fragment is triggered
    override fun onResume() {
        super.onResume()
        //getting the order list
        getMyOrderList()
    }

    //getting the order list of currently logged in user
    //and decrypting the address
    private fun getMyOrderList(){
        showProgressDialog("Decrypting")

        //getting the order list
        FirestoreClass().getMyOrderList(this@OrderFragment)
    }

    //on successfully getting the order list of currently logged in user
    //and decrypting the address
    fun successGetOrderList(orderList: ArrayList<Order>){
        //hide progress bar
        hideProgressDialog()

        //if the order is present
        if(orderList.size > 0){
            rv_my_order_items.visibility = View.VISIBLE
            tv_no_orders_found.visibility = View.GONE

            //boiler plate code

            rv_my_order_items.layoutManager = LinearLayoutManager(activity)
            rv_my_order_items.setHasFixedSize(true)
            val myOrderAdapter = OrderListCryptoAdapter(requireActivity(), orderList)
            rv_my_order_items.adapter = myOrderAdapter
        }
        else{
            rv_my_order_items.visibility = View.GONE
            tv_no_orders_found.visibility = View.VISIBLE
        }
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
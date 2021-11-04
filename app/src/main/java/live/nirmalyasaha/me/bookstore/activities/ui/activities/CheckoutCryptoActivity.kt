package live.nirmalyasaha.me.bookstore.activities.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_checkout_crypto.*
import live.nirmalyasaha.me.bookstore.R
import live.nirmalyasaha.me.bookstore.activities.ui.adapter.CartListAdapter
import live.nirmalyasaha.me.bookstore.firestore.FirestoreClass
import live.nirmalyasaha.me.bookstore.models.Address
import live.nirmalyasaha.me.bookstore.models.Cart
import live.nirmalyasaha.me.bookstore.models.Order
import live.nirmalyasaha.me.bookstore.models.Product
import live.nirmalyasaha.me.bookstore.utils.Constants
import java.security.NoSuchAlgorithmException
import java.util.*
import javax.crypto.Cipher
import javax.crypto.NoSuchPaddingException
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec
import kotlin.collections.ArrayList

class CheckoutCryptoActivity : BaseActivity() {

    private var mAddressDetails: Address? = null
    private lateinit var mProductList: ArrayList<Product>
    private lateinit var mCartItemsList: ArrayList<Cart>
    private var mSubTotal: Double = 0.0
    private var mTax: Double = 0.0
    private var mTotalAmount: Double = 0.0
    private var mAddressEncryptedDetails: Address? = null
    private lateinit var mOrderDetails: Order

    private val encryptionKey = byteArrayOf(9, 115, 51, 86, 105, 4, -31, -23, -68, 88, 17, 20, 3, -105, 119, -53)
    private var cipher: Cipher? = null
    private var decipher: Cipher? = null
    private var secretKeySpec: SecretKeySpec? = null

    private val secretKey = "tK5UTui+DPh8lIlBxya5XVsmeDCoUl6vHhdIESMB6sQ="
    private val salt = "QWlGNHNhMTJTQWZ2bGhpV3U=" // base64 decode => AiF4sa12SAfvlhiWu
    private val iv = "bVQzNFNhRkQ1Njc4UUFaWA==" // base64 decode => mT34SaFD5678QAZX

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout_crypto)

        //set up the action bar with back icon
        val actionBar = getSupportActionBar()
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24)
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        //getting the address
        if(intent.hasExtra(Constants.EXTRA_SELECTED_ADDRESS)){
            mAddressDetails = intent.getParcelableExtra<Address>(Constants.EXTRA_SELECTED_ADDRESS)
        }

        try {
            cipher = Cipher.getInstance("AES")
            decipher = Cipher.getInstance("AES")
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: NoSuchPaddingException) {
            e.printStackTrace()
        }
        secretKeySpec = SecretKeySpec(encryptionKey, "AES")

        //getting the details
        if(mAddressDetails != null){
            tv_checkout_address_type.text = mAddressDetails?.type
            tv_checkout_full_name.text = mAddressDetails?.name
            tv_checkout_address.text = "${mAddressDetails!!.type} Address: ${mAddressDetails!!.address}"
            tv_checkout_mobile_number.text = mAddressDetails?.mobile
        }

        //getting the book list
        //to check if the cart has the same fields or not
        getBookList()

        //click functionality
        btn_place_order.setOnClickListener {
            //uploading the order
            placeOrder()
        }
    }

    //function to backpress the action bar
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    //function used to get all the book list from firebase firestore
    //to check if the cart has the same fields or not
    private fun getBookList(){
        //show progress bar
        showProgressDialog(resources.getString(R.string.please_wait))

        //hiding progress bar is handled by firestore class

        //getting the book list from firebase firestore
        FirestoreClass().getAllBookList(this@CheckoutCryptoActivity)
    }

    //function used to get all the cart list of currently logged in user from firebase firestore
    private fun getCartList(){
        //get all the cart list of currently logged in user from firebase firestore
        FirestoreClass().getMyCartList(this@CheckoutCryptoActivity)
    }

    //fucntion used to place the order
    private fun placeOrder(){
        //showing the progress bar
        showProgressDialog("Encrypting")
        if(mAddressDetails != null){
            mAddressEncryptedDetails = mAddressDetails
            //encrypting
            mAddressEncryptedDetails?.address = encrypt(mAddressDetails?.address!!)
            //setting the document id
            val uniqueId: String = UUID.randomUUID().toString()

            //creating the order dta model
            mOrderDetails = Order(
                    FirestoreClass().getCurrentUserID(),
                    mCartItemsList,
                    mAddressEncryptedDetails!!,
                    "Order id: ${System.currentTimeMillis()}",
                    mCartItemsList[0].image,
                    mSubTotal.toString(),
                    mTax.toString(),
                    mTotalAmount.toString(),
                    "pending",
                    System.currentTimeMillis(),
                    uniqueId
            )

            //uploading the order
            FirestoreClass().placeOrder(this, mOrderDetails)
        }
    }

    //on successfully getting all the book list
    //to check if the cart has the same fields or not
    //basically used by firestore class
    fun successGetBookList(productList: ArrayList<Product>){
        mProductList = productList
        //get all the cart list of currently logged in user from firebase firestore
        getCartList()
    }

    //on successfully getting all the cart list of currently logged in user
    //basically used by firestore class
    fun successGetCartList(cartList: ArrayList<Cart>){
        //hide progress bar
        hideProgressDialog()

        for(book in mProductList){
            for(cart in cartList){
                if(book.product_id == cart.product_id){
                    cart.stock_quantity = book.quantity
                }
            }
        }
        mCartItemsList = cartList

        //boiler plate code

        //loading the cart
        rv_cart_list_items.layoutManager = LinearLayoutManager(this@CheckoutCryptoActivity)
        rv_cart_list_items.setHasFixedSize(true)
        //edit mode is off
        val cartAdapter = CartListAdapter(this@CheckoutCryptoActivity, mCartItemsList, false)
        rv_cart_list_items.adapter = cartAdapter

        //setting the price
        for(item in mCartItemsList){
            val availableBook = item.stock_quantity.toInt()
            if(availableBook > 0){
                val price = item.price.toDouble()
                val quantity = item.cart_quantity.toInt()
                mSubTotal += price * quantity
            }
        }
        mTax = ((mSubTotal * 18) / 100)

        tv_checkout_sub_total.text = "Rs $mSubTotal"
        tv_checkout_shipping_charge.text = "Rs ${mTax}"

        if(mSubTotal > 0){
            ll_checkout_place_order.visibility = View.VISIBLE
            mTotalAmount = mSubTotal + mTax
            tv_checkout_total_amount.text = "Rs $mTotalAmount"
        }
        else{
            ll_checkout_place_order.visibility = View.GONE
        }
    }

    //on successfully placing the order
    //basically used by firestore class
    fun successOrderPlace(){
        //updating the product stock quantity
        //and deleting the cart list
        FirestoreClass().updateProductCartDetails(this, mCartItemsList, mOrderDetails)
    }

    //function on successfully updating the product stock quantity
    //and deleting the cart list
    fun successProductCartDetailsUpdate(){
        //hiding the progress bar
        hideProgressDialog()

        //going to the order success activity
        val intent = Intent(this@CheckoutCryptoActivity, OrderSuccessActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    //function used to encrypt the data using cryptography AES technology
    fun encrypt(strToEncrypt: String) : String?
    {
        try
        {
            val ivParameterSpec = IvParameterSpec(Base64.decode(iv, Base64.DEFAULT))

            val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
            val spec =  PBEKeySpec(secretKey.toCharArray(), Base64.decode(salt, Base64.DEFAULT), 10000, 256)
            val tmp = factory.generateSecret(spec)
            val secretKey =  SecretKeySpec(tmp.encoded, "AES")

            val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec)
            return Base64.encodeToString(cipher.doFinal(strToEncrypt.toByteArray(Charsets.UTF_8)), Base64.DEFAULT)
        }
        catch (e: Exception)
        {
            println("Error while encrypting: $e")
        }
        return null
    }
}
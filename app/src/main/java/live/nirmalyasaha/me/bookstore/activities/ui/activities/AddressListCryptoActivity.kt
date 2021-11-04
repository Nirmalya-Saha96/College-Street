package live.nirmalyasaha.me.bookstore.activities.ui.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.api.ResourceProto.resource
import kotlinx.android.synthetic.main.activity_address_list_crypto.*
import live.nirmalyasaha.me.bookstore.R
import live.nirmalyasaha.me.bookstore.activities.ui.adapter.AddressListCryptoAdapter
import live.nirmalyasaha.me.bookstore.firestore.FirestoreClass
import live.nirmalyasaha.me.bookstore.models.Address
import live.nirmalyasaha.me.bookstore.utils.Constants
import live.nirmalyasaha.me.bookstore.utils.SwipeToDeleteCallback
import live.nirmalyasaha.me.bookstore.utils.SwipeToEditCallback
import java.security.NoSuchAlgorithmException
import javax.crypto.Cipher
import javax.crypto.NoSuchPaddingException
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec
import kotlin.collections.ArrayList

class AddressListCryptoActivity : BaseActivity() {

    private var mSelectedAddress: Boolean = false

    private val encryptionKey = byteArrayOf(9, 115, 51, 86, 105, 4, -31, -23, -68, 88, 17, 20, 3, -105, 119, -53)
    private var cipher: Cipher? = null
    private var decipher: Cipher? = null
    private var secretKeySpec: SecretKeySpec? = null

    private val secretKey = "tK5UTui+DPh8lIlBxya5XVsmeDCoUl6vHhdIESMB6sQ="
    private val salt = "QWlGNHNhMTJTQWZ2bGhpV3U=" // base64 decode => AiF4sa12SAfvlhiWu
    private val iv = "bVQzNFNhRkQ1Njc4UUFaWA==" // base64 decode => mT34SaFD5678QAZX

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address_list_crypto)

        //set up the action bar with back icon
        val actionBar = getSupportActionBar()
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24)
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        //getting the intent
        if(intent.hasExtra(Constants.EXTRA_SELECT_ADDRESS)){
            //if the activity comes from cart activity or not
            mSelectedAddress = intent.getBooleanExtra(Constants.EXTRA_SELECT_ADDRESS, false)
        }

        //getting the address list
        getAddressList()

        try {
            cipher = Cipher.getInstance("AES")
            decipher = Cipher.getInstance("AES")
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: NoSuchPaddingException) {
            e.printStackTrace()
        }
        secretKeySpec = SecretKeySpec(encryptionKey, "AES")

        //click functionality
        add_address.setOnClickListener {
            //going to the add address activity
            val intent = Intent(this@AddressListCryptoActivity, AddEditAddressCryptoActivity::class.java)
            startActivityForResult(intent, Constants.ADD_ADDRESS_REQUEST_CODE)
        }

        tv_add_address.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            //set title for alert dialog
            builder.setTitle(R.string.dialogTitle)
            //set message for alert dialog
            builder.setMessage(R.string.dialogMessage)
            builder.setIcon(android.R.drawable.ic_dialog_alert)

            //performing positive action
            builder.setPositiveButton("OK"){dialogInterface, which ->
                Toast.makeText(applicationContext,"Your address is end-to-end encrypted using cryptography AES technology",Toast.LENGTH_LONG).show()
            }
            //performing cancel action
            builder.setNeutralButton("Cancel"){dialogInterface , which ->
                Toast.makeText(applicationContext,"Your address is end-to-end encrypted using cryptography AES technology",Toast.LENGTH_LONG).show()
            }
            //performing negative action
            builder.setNegativeButton("No"){dialogInterface, which ->
                Toast.makeText(applicationContext,"Your address is end-to-end encrypted using cryptography AES technology",Toast.LENGTH_LONG).show()
            }
            // Create the AlertDialog
            val alertDialog: AlertDialog = builder.create()
            // Set other dialog properties
            alertDialog.setCancelable(false)
            alertDialog.show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            //getting the address list
            getAddressList()
        }
    }

    //function to backpress the action bar
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    //function used to get the address of currently logged in user from firebase firestore
    private fun getAddressList(){
        //show the progress bar
        showProgressDialog("Decrypting")
        //getting the address list of currently logged in user from firebase firestore
        FirestoreClass().getAddressList(this)
    }

    //function on successfully getting the address list of currently logged in user
    //basically used by firestore class
    fun successGetAddressList(addressList: ArrayList<Address>){
        //hiding the progress bar
        hideProgressDialog()
        if(addressList.size > 0){
            rv_address_list.visibility = View.VISIBLE
            tv_no_address.visibility = View.GONE

            rv_address_list.layoutManager = LinearLayoutManager(this@AddressListCryptoActivity)
            rv_address_list.setHasFixedSize(true)
            val addressAdapter = AddressListCryptoAdapter(this, addressList, mSelectedAddress)
            rv_address_list.adapter = addressAdapter

            //boiler plate

            //if this activity is not triggered from cart activity
            if(!mSelectedAddress){
                //swip to edit functionality
                val editSwipeHandler = object: SwipeToEditCallback(this){
                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int){
                        val adapter = rv_address_list.adapter as AddressListCryptoAdapter
                        adapter.notifyEditItem(this@AddressListCryptoActivity, viewHolder.adapterPosition)
                    }
                }

                val editItemTouchHelper = ItemTouchHelper(editSwipeHandler)
                editItemTouchHelper.attachToRecyclerView(rv_address_list)

                //swipe to delete functionality
                val deleteSwipeHandler = object: SwipeToDeleteCallback(this){
                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int){
                        showProgressDialog(resources.getString(R.string.please_wait))
                        //deleting the address from firebase firestore
                        FirestoreClass().deleteAddress(this@AddressListCryptoActivity, addressList[viewHolder.adapterPosition].id)
                    }
                }

                val deleteItemTouchHelper = ItemTouchHelper(deleteSwipeHandler)
                deleteItemTouchHelper.attachToRecyclerView(rv_address_list)
            }
        }
        else{
            rv_address_list.visibility = View.GONE
            tv_no_address.visibility = View.VISIBLE
        }
    }

    //function on successfully deleting the address from the firebase firestore
    //basically used by firestore class
    fun successDeleteAddress(){
        //hide progress bar
        hideProgressDialog()

        showErrorSnackBar(resources.getString(R.string.err_your_address_deleted_successfully), false)

        //loading the address list
        getAddressList()
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
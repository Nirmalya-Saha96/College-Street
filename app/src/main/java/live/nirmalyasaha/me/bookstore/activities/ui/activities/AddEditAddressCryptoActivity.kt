package live.nirmalyasaha.me.bookstore.activities.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Base64
import kotlinx.android.synthetic.main.activity_add_edit_address_crypto.*
import kotlinx.android.synthetic.main.activity_address_list_crypto.*
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.activity_register.*
import live.nirmalyasaha.me.bookstore.R
import live.nirmalyasaha.me.bookstore.firestore.FirestoreClass
import live.nirmalyasaha.me.bookstore.models.Address
import live.nirmalyasaha.me.bookstore.utils.Constants
import java.security.NoSuchAlgorithmException
import javax.crypto.Cipher
import javax.crypto.NoSuchPaddingException
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

class AddEditAddressCryptoActivity : BaseActivity() {

    private var mAddressDetails: Address? = null

    private val encryptionKey = byteArrayOf(9, 115, 51, 86, 105, 4, -31, -23, -68, 88, 17, 20, 3, -105, 119, -53)
    private var cipher: Cipher? = null
    private var decipher: Cipher? = null
    private var secretKeySpec: SecretKeySpec? = null

    private val secretKey = "tK5UTui+DPh8lIlBxya5XVsmeDCoUl6vHhdIESMB6sQ="
    private val salt = "QWlGNHNhMTJTQWZ2bGhpV3U=" // base64 decode => AiF4sa12SAfvlhiWu
    private val iv = "bVQzNFNhRkQ1Njc4UUFaWA==" // base64 decode => mT34SaFD5678QAZX

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_address_crypto)

        //set up the action bar with back icon
        val actionBar = getSupportActionBar()
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24)
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        //getting the address to edit through the intent
        if(intent.hasExtra(Constants.EXTRA_ADDRESS_DETAILS)){
            mAddressDetails = intent.getParcelableExtra(Constants.EXTRA_ADDRESS_DETAILS)
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

        //in edit mode
        if (mAddressDetails != null) {
            if (mAddressDetails!!.id.isNotEmpty()) {
                //setting the fields
                btn_submit_address.text = resources.getString(R.string.btn_lbl_update)
                et_full_name.setText(mAddressDetails?.name)
                et_phone_number.setText(mAddressDetails?.mobile)
                et_address.setText(mAddressDetails?.address)
                when (mAddressDetails?.type) {
                    Constants.HOME -> {
                        rb_home.isChecked = true
                    }
                    Constants.OFFICE -> {
                        rb_office.isChecked = true
                    }
                    else -> {
                        rb_other.isChecked = true
                    }
                }
            }
        }

        //upload the address
        btn_submit_address.setOnClickListener {
            uploadAddress()
        }
    }

    //function to backpress the action bar
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    //uploading the address details to firebase firestore
    //also update the address
    private fun uploadAddress(){
        // getting the fields
        val fullName: String = et_full_name.text.toString().trim { it <= ' ' }
        val phoneNumber: String = et_phone_number.text.toString().trim { it <= ' ' }
        val address: String = et_address.text.toString().trim { it <= ' ' }
        val encryptAddress: String? = encrypt(address)

        if (validateAddressDetails()) {
            // Show the progress dialog.
            showProgressDialog("Encrypting")


            val addressType: String = when {
                rb_home.isChecked -> {
                    Constants.HOME
                }
                rb_office.isChecked -> {
                    Constants.OFFICE
                }
                else -> {
                    Constants.OTHER
                }
            }

            val addressModel = Address(
                    FirestoreClass().getCurrentUserID(),
                    fullName,
                    phoneNumber,
                    encryptAddress,
                    addressType
            )

            //update
            if(mAddressDetails != null && mAddressDetails!!.id.isNotEmpty()){
                FirestoreClass().updateAddressDetails(this, addressModel, mAddressDetails!!.id)
            }
            //add
            else{
                FirestoreClass().addAddressCrypto(this@AddEditAddressCryptoActivity, addressModel)
            }

        }
    }

    //function on successfully add the address
    //basically used by firestore class
    fun addEditAddressSuccess(){
        //hiding the progress bar
        hideProgressDialog()

        showErrorSnackBar("Address added sucessfully", false)
        setResult(RESULT_OK)
    }

    //function on successfully update the address
    //on swipe
    //basically used by firestore class
    fun updateAddressSuccess(){
        //hiding the progress bar
        hideProgressDialog()

        showErrorSnackBar("Address updated sucessfully", false)
        setResult(RESULT_OK)
    }

    //function used to validate all fields
    private fun validateAddressDetails(): Boolean {
        return when {

            //if the full name is not given
            TextUtils.isEmpty(et_full_name.text.toString().trim { it <= ' ' }) -> {
                //calls the show error snack bar function passing the error message and error type
                showErrorSnackBar(resources.getString(R.string.err_msg_please_enter_full_name), true)
                false
            }

            //if the phone number is not given
            TextUtils.isEmpty(et_phone_number.text.toString().trim { it <= ' ' }) -> {
                //calls the show error snack bar function passing the error message and error type
                showErrorSnackBar(resources.getString(R.string.err_msg_please_enter_phone_number), true)
                false
            }

            //when phone number length is less than 10 letters
            et_phone_number.length() < 6 ->{
                //calls the show error snack bar function passing the error message and error type
                showErrorSnackBar("Phone number should be 10 digits", true)
                false
            }

            //if the address is not given
            TextUtils.isEmpty(et_address.text.toString().trim { it <= ' ' }) -> {
                //calls the show error snack bar function passing the error message and error type
                showErrorSnackBar(resources.getString(R.string.err_msg_please_enter_address), true)
                false
            }

            else -> {
                true
            }
        }
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
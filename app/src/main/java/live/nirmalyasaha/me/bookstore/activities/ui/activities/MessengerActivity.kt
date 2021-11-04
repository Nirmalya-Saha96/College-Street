package live.nirmalyasaha.me.bookstore.activities.ui.activities

import android.app.PendingIntent.getActivity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_messenger.*
import kotlinx.android.synthetic.main.item_user_new_message.view.*
import live.nirmalyasaha.me.bookstore.R
import live.nirmalyasaha.me.bookstore.activities.ui.adapter.UnreadMessageAdapter
import live.nirmalyasaha.me.bookstore.firestore.FirestoreClass
import live.nirmalyasaha.me.bookstore.models.Message
import live.nirmalyasaha.me.bookstore.models.User
import live.nirmalyasaha.me.bookstore.utils.Constants
import live.nirmalyasaha.me.bookstore.utils.GlideLoader
import java.security.NoSuchAlgorithmException
import javax.crypto.Cipher
import javax.crypto.NoSuchPaddingException
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

class MessengerActivity : BaseActivity() {

    private var mUnreadList: ArrayList<Message> = ArrayList()

    private val encryptionKey = byteArrayOf(9, 115, 51, 86, 105, 4, -31, -23, -68, 88, 17, 20, 3, -105, 119, -53)
    private var cipher: Cipher? = null
    private var decipher: Cipher? = null
    private var secretKeySpec: SecretKeySpec? = null

    private val secretKey = "tK5UTui+DPh8lIlBxya5XVsmeDCoUl6vHhdIESMB6sQ="
    private val salt = "QWlGNHNhMTJTQWZ2bGhpV3U=" // base64 decode => AiF4sa12SAfvlhiWu
    private val iv = "bVQzNFNhRkQ1Njc4UUFaWA==" // base64 decode => mT34SaFD5678QAZX

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messenger)

        //set up the action bar with back icon
        val actionBar = getSupportActionBar()
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24)
            actionBar.setDisplayHomeAsUpEnabled(true)
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

        //click functionality
        fab_user.setOnClickListener {
            val intent = Intent(this@MessengerActivity, NewMessengerActivity::class.java)
            startActivity(intent)
        }

        fab_bot.setOnClickListener {
            val intent = Intent(this@MessengerActivity, ChatBotActivity::class.java)
            startActivity(intent)
        }

        getAllUnreadMessages()

    }

    //function to backpress the action bar
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun getAllUnreadMessages(){
        showProgressDialog("Decrypting")

        FirestoreClass().getAllUnreadMessages(this@MessengerActivity)
    }

    fun getUser(userId: String){
        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().getUser(this@MessengerActivity, userId)
    }

    fun successGetAllUnreadMessage(unreadList: ArrayList<Message>){
        hideProgressDialog()

        if(unreadList.size > 0 && mUnreadList.size != unreadList.size){
            rv_my_unread_chat_messages.visibility = View.VISIBLE
            rl_unread_chat_messages.visibility = View.GONE

            rv_my_unread_chat_messages.layoutManager = LinearLayoutManager(this)
            rv_my_unread_chat_messages.setHasFixedSize(true)

            val adapter = UnreadMessageAdapter(this, unreadList, mUnreadList)
            rv_my_unread_chat_messages.adapter = adapter

            mUnreadList = unreadList
            Log.i("Unread","${mUnreadList}")
        }
        else{
            rv_my_unread_chat_messages.visibility = View.GONE
            rl_unread_chat_messages.visibility = View.VISIBLE
        }
    }

    fun successGetUser(user: User){
        hideProgressDialog()

        val intent = Intent(this@MessengerActivity, ChatCryptoActivity::class.java)
        intent.putExtra(Constants.EXTRA_USER, user)
        startActivity(intent)
    }

    fun decrypt(strToDecrypt: String?) : String? {
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
        catch (e: Exception) {
            println("Error while decrypting: $e");
        }
        return null
    }
}
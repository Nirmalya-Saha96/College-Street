package live.nirmalyasaha.me.bookstore.activities.ui.activities



import android.os.Bundle
import android.text.TextUtils
import android.util.Base64
import android.util.Log
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_add_edit_address_crypto.*
import kotlinx.android.synthetic.main.activity_chat_crypto.*
import kotlinx.android.synthetic.main.item_chat_from_row.view.*
import kotlinx.android.synthetic.main.item_chat_to_row.view.*
import live.nirmalyasaha.me.bookstore.R
import live.nirmalyasaha.me.bookstore.firestore.FirestoreClass
import live.nirmalyasaha.me.bookstore.models.Message
import live.nirmalyasaha.me.bookstore.models.User
import live.nirmalyasaha.me.bookstore.models.chats
import live.nirmalyasaha.me.bookstore.utils.Constants
import java.security.NoSuchAlgorithmException
import java.util.*
import javax.crypto.Cipher
import javax.crypto.NoSuchPaddingException
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

class ChatCryptoActivity : BaseActivity() {

    private var mUser: User = User()
    val adapter = GroupAdapter<ViewHolder>()
    private var c: Int = 0
    private var messageList: ArrayList<Message> = ArrayList()
    private lateinit var mDBRef: DatabaseReference

    private val encryptionKey = byteArrayOf(9, 115, 51, 86, 105, 4, -31, -23, -68, 88, 17, 20, 3, -105, 119, -53)
    private var cipher: Cipher? = null
    private var decipher: Cipher? = null
    private var secretKeySpec: SecretKeySpec? = null

    private val secretKey = "tK5UTui+DPh8lIlBxya5XVsmeDCoUl6vHhdIESMB6sQ="
    private val salt = "QWlGNHNhMTJTQWZ2bGhpV3U=" // base64 decode => AiF4sa12SAfvlhiWu
    private val iv = "bVQzNFNhRkQ1Njc4UUFaWA==" // base64 decode => mT34SaFD5678QAZX


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_crypto)

        //getting the user passed through intent
        if (intent.hasExtra(Constants.EXTRA_USER)) {
            mUser = intent.getParcelableExtra<User>(Constants.EXTRA_USER)!!
            supportActionBar?.title = mUser.firstName
        }

        mDBRef = FirebaseDatabase.getInstance().getReference()

        try {
            cipher = Cipher.getInstance("AES")
            decipher = Cipher.getInstance("AES")
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: NoSuchPaddingException) {
            e.printStackTrace()
        }
        secretKeySpec = SecretKeySpec(encryptionKey, "AES")

        //set up the action bar with back icon
        val actionBar = getSupportActionBar()
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24)
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        send_button_chat_log.setOnClickListener {
            if (validateAddressDetails()) {
                sendMessage()
            }
        }
        
        listenForMessages()
    }

    //function to backpress the action bar
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun sendMessage() {
        val text = edittext_chat_log.text.toString()
        val encryptText = encrypt(text)
        val from_id = FirestoreClass().getCurrentUserID()
        val to_id = mUser.id

        val chat = Message(
                from_id,
                to_id,
                encryptText,
                System.currentTimeMillis()
        )

        FirestoreClass().sendMessage(this@ChatCryptoActivity, chat)
    }

    private fun listenForMessages() {
        val from_id = FirestoreClass().getCurrentUserID()
        val to_id = mUser.id
        recyclerview_chat_log.adapter = adapter
        recyclerview_chat_log.scrollToPosition(adapter.itemCount - 1)
        val ref = FirebaseDatabase.getInstance().getReference("/messages")

        ref.addChildEventListener(object: ChildEventListener {

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(Message::class.java)

                if (chatMessage != null) {
                    val decryptTxt = decrypt(chatMessage.text!!)

                    if(from_id == chatMessage.fromId || from_id == chatMessage.toId){
                            if(to_id == chatMessage.fromId || to_id == chatMessage.toId){
                                if (chatMessage.fromId == from_id) {

                                    adapter.add(ChatFromItems(decryptTxt))
                                    recyclerview_chat_log.scrollToPosition(adapter.itemCount - 1)
                                } else {

                                    adapter.add(ChatToItems(decryptTxt))
                                    recyclerview_chat_log.scrollToPosition(adapter.itemCount - 1)
                                }
                            }
                        }
                }

            }

            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }

        })

    }

    fun success() {
        recyclerview_chat_log.scrollToPosition(adapter.itemCount - 1)

        val text = edittext_chat_log.text.toString()
        val encryptText = encrypt(text)
        val from_id = FirestoreClass().getCurrentUserID()
        val to_id = mUser.id

        val reference = FirebaseDatabase.getInstance().getReference("/messages").push()

        val chatMessage = Message( from_id, to_id, encryptText, System.currentTimeMillis())
        reference.setValue(chatMessage)
                .addOnSuccessListener {
                    Log.d("sent_chat_message", "Saved our chat message")
                    edittext_chat_log.text?.clear()
                }
    }

//    fun successGetAllMessages(chatList: ArrayList<Message>) {
//        //hideProgressDialog()
//        if (chatList.size > 0 && c == 0) {
//            recyclerview_chat_log.adapter = adapter
//            recyclerview_chat_log.scrollToPosition(adapter.itemCount - 1)
//            c = chatList.size
//            for (i in chatList) {
//                val decryptText = decrypt(i.text)
//                if (i.fromId == FirestoreClass().getCurrentUserID()) {
//                    adapter.add(ChatFromItem(decryptText, i.text, 1))
//                    recyclerview_chat_log.scrollToPosition(adapter.itemCount - 1)
//                } else {
//                    adapter.add(ChatFromItem(decryptText, i.text, 2))
//                    recyclerview_chat_log.scrollToPosition(adapter.itemCount - 1)
//                }
//            }
//
//            adapter.setOnItemClickListener { item, view ->
//                val userItem = item as ChatFromItem
//
//                val builder = AlertDialog.Builder(this)
//                //set title for alert dialog
//                builder.setTitle("End-To-End encrypted using AES technology")
//                //set message for alert dialog
//                builder.setMessage("Encrypted: ${userItem.txt}")
//                builder.setIcon(android.R.drawable.ic_dialog_alert)
//
//                //performing positive action
//                builder.setPositiveButton("OK") { dialogInterface, which ->
//                    Toast.makeText(applicationContext, "Encrypted: ${userItem.txt}", Toast.LENGTH_LONG).show()
//                }
//                //performing cancel action
//                builder.setNeutralButton("Cancel") { dialogInterface, which ->
//                    Toast.makeText(applicationContext, "Encrypted: ${userItem.txt}", Toast.LENGTH_LONG).show()
//                }
//                //performing negative action
//                builder.setNegativeButton("No") { dialogInterface, which ->
//                    Toast.makeText(applicationContext, "Encrypted: ${userItem.txt}", Toast.LENGTH_LONG).show()
//                }
//                // Create the AlertDialog
//                val alertDialog: AlertDialog = builder.create()
//                // Set other dialog properties
//                alertDialog.setCancelable(false)
//                alertDialog.show()
//
//            }
//        } else if (chatList.size > 0 && c > 0) {
//            recyclerview_chat_log.adapter = adapter
//            recyclerview_chat_log.scrollToPosition(adapter.itemCount - 1)
//            for (i in chatList) {
//                var present = false
//                for (j in messageList) {
//                    if (j.id == i.id) {
//                        present = true
//                    }
//                }
//                if (present == false) {
//                    val decryptText = decrypt(i.text)
//                    if (i.fromId == FirestoreClass().getCurrentUserID()) {
//                        adapter.add(ChatFromItem(decryptText, i.text, 1))
//                        recyclerview_chat_log.scrollToPosition(adapter.itemCount - 1)
//                    } else {
//                        adapter.add(ChatFromItem(decryptText, i.text, 2))
//                        recyclerview_chat_log.scrollToPosition(adapter.itemCount - 1)
//                    }
//                }
//            }
//
//            adapter.setOnItemClickListener { item, view ->
//                val userItem = item as ChatFromItem
//
//                val builder = AlertDialog.Builder(this)
//                //set title for alert dialog
//                builder.setTitle("End-To-End encrypted using AES technology")
//                //set message for alert dialog
//                builder.setMessage("Encrypted: ${userItem.txt}")
//                builder.setIcon(android.R.drawable.ic_dialog_alert)
//
//                //performing positive action
//                builder.setPositiveButton("OK") { dialogInterface, which ->
//                    Toast.makeText(applicationContext, "Encrypted: ${userItem.txt}", Toast.LENGTH_LONG).show()
//                }
//                //performing cancel action
//                builder.setNeutralButton("Cancel") { dialogInterface, which ->
//                    Toast.makeText(applicationContext, "Encrypted: ${userItem.txt}", Toast.LENGTH_LONG).show()
//                }
//                //performing negative action
//                builder.setNegativeButton("No") { dialogInterface, which ->
//                    Toast.makeText(applicationContext, "Encrypted: ${userItem.txt}", Toast.LENGTH_LONG).show()
//                }
//                // Create the AlertDialog
//                val alertDialog: AlertDialog = builder.create()
//                // Set other dialog properties
//                alertDialog.setCancelable(false)
//                alertDialog.show()
//
//            }
//        }
//        messageList = chatList
//    }

    //function used to validate all fields
    private fun validateAddressDetails(): Boolean {
        return when {

            //if the full name is not given
            TextUtils.isEmpty(edittext_chat_log.text.toString().trim { it <= ' ' }) -> {
                //calls the show error snack bar function passing the error message and error type
                showErrorSnackBar("Please enter text message", true)
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

    class ChatFromItems(val text: String?): Item<ViewHolder>() {
        override fun bind(viewHolder: ViewHolder, position: Int) {
            viewHolder.itemView.textView_from_row.text = text
        }

        override fun getLayout(): Int {
            return R.layout.item_chat_from_row
        }
    }

    class ChatToItems(val text: String?): Item<ViewHolder>() {
        override fun bind(viewHolder: ViewHolder, position: Int) {
            viewHolder.itemView.textView_to_row.text = text
        }

        override fun getLayout(): Int {
            return R.layout.item_chat_to_row
        }
    }
}
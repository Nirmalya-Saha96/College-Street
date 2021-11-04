package live.nirmalyasaha.me.bookstore.activities.ui.activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_chat_bot.*
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.coroutines.*
import live.nirmalyasaha.me.bookstore.R
import live.nirmalyasaha.me.bookstore.activities.ui.adapter.MessagingAdapter
import live.nirmalyasaha.me.bookstore.models.BotMessage
import live.nirmalyasaha.me.bookstore.utils.BotResponse
import live.nirmalyasaha.me.bookstore.utils.Constants
import live.nirmalyasaha.me.bookstore.utils.Time

class ChatBotActivity : BaseActivity(), View.OnClickListener  {

    private lateinit var adapter: MessagingAdapter
    private val botList = listOf("sri", "nirmalyo", "nrs", "Nirmalya")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_bot)

        val actionBar = getSupportActionBar()
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24)
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        setRecyclerView()

        val random = (0..3).random()
        welcomeBotMessage("Hi! Welcome to College Street App. I am ${botList[random]}, your bot assigned for today, how may I help?")

        btn_send.setOnClickListener(this)
        et_message.setOnClickListener(this)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onStart() {
        super.onStart()
        //In case there are messages, scroll to bottom when re-opening app
        GlobalScope.launch {
            delay(100)
            withContext(Dispatchers.Main) {
                rv_messages.scrollToPosition(adapter.itemCount - 1)
            }
        }
    }

    private fun setRecyclerView() {
        adapter = MessagingAdapter()
        rv_messages.adapter = adapter
        rv_messages.layoutManager = LinearLayoutManager(applicationContext)
    }

    private fun sendMessageByUser() {
        val message = et_message.text.toString()
        val timeStamp = Time.timeStamp()

        if (message.isNotEmpty()) {
            et_message.setText("")

            adapter.insertMessage(BotMessage(message, Constants.SEND_ID, timeStamp))
            rv_messages.scrollToPosition(adapter.itemCount - 1)

            botResponse(message)
        }
    }

    private fun botResponse(message: String) {
        val timeStamp = Time.timeStamp()

        GlobalScope.launch {
            //Fake response delay
            delay(1500)

            withContext(Dispatchers.Main) {
                //Gets the response
                val response = BotResponse.basicResponses(message)

                //Inserts our message into the adapter
                adapter.insertMessage(BotMessage(response, Constants.RECEIVE_ID, timeStamp))

                //Scrolls us to the position of the latest message
                rv_messages.scrollToPosition(adapter.itemCount - 1)

                //Starts Google
                when (response) {
                    Constants.OPEN_GOOGLE -> {
                        val site = Intent(Intent.ACTION_VIEW)
                        site.data = Uri.parse("https://www.google.com/")
                        startActivity(site)
                    }
                    Constants.OPEN_SEARCH -> {
                        val site = Intent(Intent.ACTION_VIEW)
                        val searchTerm: String? = message.substringAfterLast("search")
                        site.data = Uri.parse("https://www.google.com/search?&q=$searchTerm")
                        startActivity(site)
                    }
                    Constants.LINKEDIN ->{
                        val site = Intent(Intent.ACTION_VIEW)
                        site.data = Uri.parse("https://www.linkedin.com/in/nirmalya-saha-625993201/")
                        startActivity(site)
                    }
                    Constants.PORTFOLIO ->{
                        val site = Intent(Intent.ACTION_VIEW)
                        site.data = Uri.parse("http://me.nirmalyasaha.live")
                        startActivity(site)
                    }
                    Constants.QUIT ->{
                        finish()
                    }
                }
            }
        }
    }

    private fun welcomeBotMessage(message: String) {

        GlobalScope.launch {
            delay(1000)
            withContext(Dispatchers.Main) {
                val timeStamp = Time.timeStamp()
                adapter.insertMessage(BotMessage(message, Constants.RECEIVE_ID, timeStamp))

                rv_messages.scrollToPosition(adapter.itemCount - 1)
            }
        }
    }

    override fun onClick(v: View?) {
        if(v != null){
            when (v.id){
                //when logout
                R.id.btn_send->{
                    if(validateMessageDetails()){
                        sendMessageByUser()
                    }
                }
                R.id.et_message->{
                    GlobalScope.launch {
                        delay(100)

                        withContext(Dispatchers.Main) {
                            rv_messages.scrollToPosition(adapter.itemCount - 1)

                        }
                    }
                }
            }
        }
    }

    private fun validateMessageDetails(): Boolean {
        return when {
            //when first name is not entered
            TextUtils.isEmpty(et_message.text.toString().trim { it <= ' ' }) -> {
                //calls the show error snack bar function passing the error message and error type
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_send), true)
                false
            }

            //if validated
            else -> {
                true
            }
        }
    }
}
package live.nirmalyasaha.me.bookstore.activities.ui.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_new_messenger.*
import kotlinx.android.synthetic.main.item_booklist_layout.view.*
import kotlinx.android.synthetic.main.item_user_new_message.view.*
import live.nirmalyasaha.me.bookstore.R
import live.nirmalyasaha.me.bookstore.firestore.FirestoreClass
import live.nirmalyasaha.me.bookstore.models.User
import live.nirmalyasaha.me.bookstore.utils.Constants
import live.nirmalyasaha.me.bookstore.utils.GlideLoader

class NewMessengerActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_messenger)

        //set up the action bar with back icon
        val actionBar = getSupportActionBar()
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24)
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        //getting all users
        getAllUsers()
    }

    //function to backpress the action bar
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    //class which handles adapter and groupie library
    class UserItem(val context: Context, val itemUser: User): Item<ViewHolder>(){
        override fun getLayout(): Int {
            return R.layout.item_user_new_message
        }

        override fun bind(viewHolder: ViewHolder, position: Int) {
            viewHolder.itemView.username_textview_new_message.text = "${itemUser.firstName} ${itemUser.lastName}"
            viewHolder.itemView.tv_email_new_message.text = itemUser.email
            GlideLoader(context).loadBookImage(itemUser.image, viewHolder.itemView.iv_new_message)
        }

    }

    //function used to get all the user
    private fun getAllUsers(){
        //showing the progress bar
        showProgressDialog(resources.getString(R.string.please_wait))

        //getting all the user
        FirestoreClass().getAllUsers(this@NewMessengerActivity)
    }

    //function on successfully getting all the users
    //basically used by firestore class
    fun successGetAllUsers(usersList: ArrayList<User>){
        //hiding the progress bar
        hideProgressDialog()

        if(usersList.size > 0){
            //using groupie library for recycler view
            val adapter = GroupAdapter<ViewHolder>()

            for(i in usersList){
                adapter.add(UserItem(this@NewMessengerActivity,i))
            }

            //going to the chat log activity
            adapter.setOnItemClickListener{item, view->
                val userItem = item as UserItem

                val intent = Intent(view.context, ChatCryptoActivity::class.java)
                intent.putExtra(Constants.EXTRA_USER, userItem.itemUser)
                startActivity(intent)
                finish()
            }

            rv_new_message.adapter = adapter
        }
    }
}
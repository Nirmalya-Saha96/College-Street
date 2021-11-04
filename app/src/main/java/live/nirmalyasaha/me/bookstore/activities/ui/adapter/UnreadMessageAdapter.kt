package live.nirmalyasaha.me.bookstore.activities.ui.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_review_book_layout.view.*
import kotlinx.android.synthetic.main.item_user_new_message.view.*
import live.nirmalyasaha.me.bookstore.R
import live.nirmalyasaha.me.bookstore.activities.ui.activities.MessengerActivity
import live.nirmalyasaha.me.bookstore.activities.ui.activities.PorfileDetailsActivity
import live.nirmalyasaha.me.bookstore.models.Message
import live.nirmalyasaha.me.bookstore.models.Review
import live.nirmalyasaha.me.bookstore.utils.Constants
import live.nirmalyasaha.me.bookstore.utils.GlideLoader

class UnreadMessageAdapter (private val context: Context, private val list: ArrayList<Message>, private val mList: ArrayList<Message>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return UnreadMessageAdapter.MyVeiwHolder(LayoutInflater.from(context).inflate(R.layout.item_user_new_message, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        var present = false
        for(i in mList){
            if(i.id == model.id){
                present = true
            }
        }
        if(present == false){
            if(holder is UnreadMessageAdapter.MyVeiwHolder){
                holder.itemView.username_textview_new_message.text = model.text
                holder.itemView.tv_email_new_message.visibility = View.GONE
                holder.itemView.iv_new_message.visibility = View.GONE
            }

            holder.itemView.setOnClickListener {
                when(context){
                    is MessengerActivity->{
                        context.getUser(model.fromId)
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class MyVeiwHolder(view: View): RecyclerView.ViewHolder(view)
}
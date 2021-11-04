package live.nirmalyasaha.me.bookstore.activities.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_bot_message.view.*
import live.nirmalyasaha.me.bookstore.R
import live.nirmalyasaha.me.bookstore.models.BotMessage
import live.nirmalyasaha.me.bookstore.utils.Constants

class MessagingAdapter: RecyclerView.Adapter<MessagingAdapter.MessageViewHolder>() {

    var messagesList = mutableListOf<BotMessage>()

    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnClickListener {

                //Remove message on the item clicked
                messagesList.removeAt(adapterPosition)
                notifyItemRemoved(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        return MessageViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_bot_message, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return messagesList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val currentMessage = messagesList[position]

        when (currentMessage.id) {
            Constants.SEND_ID -> {
                holder.itemView.tv_message.visibility = View.VISIBLE
                holder.itemView.tv_message.text = currentMessage.message
                holder.itemView.tv_bot_message.visibility = View.GONE
            }
            Constants.RECEIVE_ID -> {
                holder.itemView.tv_bot_message.visibility = View.VISIBLE
                holder.itemView.tv_bot_message.text = currentMessage.message
                holder.itemView.tv_message.visibility = View.GONE
            }
        }
    }

    fun insertMessage(message: BotMessage) {
        this.messagesList.add(message)
        notifyItemInserted(messagesList.size)
    }

}
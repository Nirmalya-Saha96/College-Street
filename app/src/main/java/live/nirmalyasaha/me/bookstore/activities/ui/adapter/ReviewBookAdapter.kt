package live.nirmalyasaha.me.bookstore.activities.ui.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_review_book_layout.view.*
import live.nirmalyasaha.me.bookstore.R
import live.nirmalyasaha.me.bookstore.activities.ui.activities.PorfileDetailsActivity
import live.nirmalyasaha.me.bookstore.activities.ui.activities.ProductBookDetailsActivity
import live.nirmalyasaha.me.bookstore.models.Review
import live.nirmalyasaha.me.bookstore.utils.Constants
import live.nirmalyasaha.me.bookstore.utils.GlideLoader


class ReviewBookAdapter(private val context: Context, private val list: ArrayList<Review>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ReviewBookAdapter.MyVeiwHolder(LayoutInflater.from(context).inflate(R.layout.item_review_book_layout, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        if(holder is ReviewBookAdapter.MyVeiwHolder){
            GlideLoader(context).loadBookImage(model.image, holder.itemView.iv_item_user_image)
            holder.itemView.tv_item_user_name.text = model.user_name
            holder.itemView.tv_item_review_description.text = model.text
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(context, PorfileDetailsActivity::class.java)
            intent.putExtra(Constants.EXTRA_USER_ID, model.user_id)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class MyVeiwHolder(view: View): RecyclerView.ViewHolder(view)
}
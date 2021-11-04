package live.nirmalyasaha.me.bookstore.activities.ui.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_dashboard_review_layout.view.*
import kotlinx.android.synthetic.main.item_review_book_layout.view.*
import live.nirmalyasaha.me.bookstore.R
import live.nirmalyasaha.me.bookstore.activities.ui.activities.PorfileDetailsActivity
import live.nirmalyasaha.me.bookstore.activities.ui.activities.ProductBookDetailsActivity
import live.nirmalyasaha.me.bookstore.models.Product
import live.nirmalyasaha.me.bookstore.models.Review
import live.nirmalyasaha.me.bookstore.utils.Constants
import live.nirmalyasaha.me.bookstore.utils.GlideLoader

class SearchBookAdapter (private val context: Context, private var list: ArrayList<Product>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyVeiwHolder(LayoutInflater.from(context).inflate(R.layout.item_dashboard_review_layout, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        if(holder is MyVeiwHolder){
            GlideLoader(context).loadBookImage(
                    model.image,
                    holder.itemView.iv_review_item_image
            )
            GlideLoader(context).loadBookImage(
                    model.image,
                    holder.itemView.review_user_image
            )
            holder.itemView.tv_review_item_title.text = model.title
            holder.itemView.tv_review_item_auhtor.text = model.author
            holder.itemView.tv_review_item_price.text = "Rs ${model.price}"
            holder.itemView.review_user_name_title.text = model.user_name
            holder.itemView.tv_review_item_description.text = model.description
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(context, ProductBookDetailsActivity::class.java)
                    intent.putExtra(Constants.EXTRA_PRODUCT_ID, model.product_id)
                    intent.putExtra(Constants.EXTRA_PRODUCT_OWNER_ID, model.user_id)
                    context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class MyVeiwHolder(view: View): RecyclerView.ViewHolder(view)
}
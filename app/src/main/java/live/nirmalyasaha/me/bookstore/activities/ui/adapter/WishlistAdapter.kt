package live.nirmalyasaha.me.bookstore.activities.ui.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_fav_book_layout.view.*
import kotlinx.android.synthetic.main.item_review_book_layout.view.*
import live.nirmalyasaha.me.bookstore.R
import live.nirmalyasaha.me.bookstore.activities.ui.activities.ProductBookDetailsActivity
import live.nirmalyasaha.me.bookstore.database.BookEntity
import live.nirmalyasaha.me.bookstore.utils.Constants
import live.nirmalyasaha.me.bookstore.utils.GlideLoader

class WishlistAdapter(private val context: Context, val bookList: List<BookEntity>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return WishlistAdapter.MyVeiwHolder(LayoutInflater.from(context).inflate(R.layout.item_fav_book_layout, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = bookList[position]
        if(holder is WishlistAdapter.MyVeiwHolder){
            GlideLoader(context).loadBookImage(model.bookImage, holder.itemView.iv_fav_item_image)
            holder.itemView.tv_fav_item_title.text = model.bookTitle
            holder.itemView.tv_fav_item_auhtor.text = model.bookAuthor
            holder.itemView.tv_fav_item_price.text = "Rs ${model.bookPrice}"
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(context, ProductBookDetailsActivity::class.java)
            intent.putExtra(Constants.EXTRA_PRODUCT_ID, model.book_id)
            intent.putExtra(Constants.EXTRA_PRODUCT_OWNER_ID, model.userId)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return bookList.size
    }

    class MyVeiwHolder(view: View): RecyclerView.ViewHolder(view)
}
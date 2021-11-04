package live.nirmalyasaha.me.bookstore.activities.ui.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_booklist_layout.view.*
import live.nirmalyasaha.me.bookstore.R
import live.nirmalyasaha.me.bookstore.activities.ui.activities.ProductBookDetailsActivity
import live.nirmalyasaha.me.bookstore.activities.ui.activities.UpdateProductActivity
import live.nirmalyasaha.me.bookstore.activities.ui.fragment.ProductFragment
import live.nirmalyasaha.me.bookstore.models.Product
import live.nirmalyasaha.me.bookstore.utils.Constants
import live.nirmalyasaha.me.bookstore.utils.GlideLoader

open class MyBookListAdapter (private val context: Context, private val list: ArrayList<Product>, private val fragment: ProductFragment): RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyVeiwHolder(LayoutInflater.from(context).inflate(R.layout.item_booklist_layout, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        if(holder is MyVeiwHolder){
            GlideLoader(context).loadBookImage(model.image, holder.itemView.iv_item_image)
            holder.itemView.tv_item_name.text = model.title
            holder.itemView.tv_item_author.text = model.author
            holder.itemView.tv_item_price.text = "Rs ${model.price}"

            holder.itemView.ib_delete_product.setOnClickListener {
                fragment.deleteBook(model.product_id)
            }

            holder.itemView.ib_edit_product.setOnClickListener {
                val intent = Intent(context, UpdateProductActivity::class.java)
                intent.putExtra(Constants.PRODUCT_EXTRA_DETAILS, model)
                context.startActivity(intent)
            }

            holder.itemView.setOnClickListener {
                val intent = Intent(context, ProductBookDetailsActivity::class.java)
                intent.putExtra(Constants.EXTRA_PRODUCT_ID, model.product_id)
                intent.putExtra(Constants.EXTRA_PRODUCT_OWNER_ID, model.user_id)
                context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class MyVeiwHolder(view: View): RecyclerView.ViewHolder(view)

}
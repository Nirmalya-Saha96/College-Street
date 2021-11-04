package live.nirmalyasaha.me.bookstore.activities.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_book_dashboard_layout.view.*
import kotlinx.android.synthetic.main.item_dashboard_review_layout.view.*
import live.nirmalyasaha.me.bookstore.R
import live.nirmalyasaha.me.bookstore.models.Product
import live.nirmalyasaha.me.bookstore.utils.GlideLoader

open class DashboardBookListAdapter (private val context: Context, private var list: ArrayList<Product>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
                LayoutInflater.from(context).inflate(
                        R.layout.item_dashboard_review_layout,
                        parent,
                        false
                )
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is MyViewHolder) {

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

            holder.itemView.setOnClickListener {
                if (onClickListener != null) {
                    onClickListener!!.onClick(position, model)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener {
        fun onClick(position: Int, product: Product)
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}
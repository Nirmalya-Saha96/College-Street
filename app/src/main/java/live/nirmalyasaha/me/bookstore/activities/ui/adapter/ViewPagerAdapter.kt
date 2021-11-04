package live.nirmalyasaha.me.bookstore.activities.ui.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_cart_layout.view.*
import kotlinx.android.synthetic.main.item_view_pager.view.*
import live.nirmalyasaha.me.bookstore.R
import live.nirmalyasaha.me.bookstore.activities.ui.activities.RegisterActivity
import live.nirmalyasaha.me.bookstore.utils.GlideLoader

class ViewPagerAdapter (private val context: Context, private var title: List<String>, private var details: List<String>, private var images: List<Int>): RecyclerView.Adapter<ViewPagerAdapter.Pager2ViewHolder>(){

    inner class Pager2ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val itemTitle: TextView = itemView.findViewById(R.id.tv_Title)
        val itemAbout: TextView = itemView.findViewById(R.id.tv_About)
        val itemImage: ImageView = itemView.findViewById(R.id.iv_Image)
        val itemButton: TextView = itemView.findViewById(R.id.tv_next)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewPagerAdapter.Pager2ViewHolder {
        return Pager2ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_view_pager, parent, false))
    }

    override fun onBindViewHolder(holder: ViewPagerAdapter.Pager2ViewHolder, position: Int) {
        holder.itemTitle.text = title[position]
        holder.itemAbout.text = details[position]
        GlideLoader(context).loadBookImage(images[position], holder.itemImage)

        if(position == 6){
            holder.itemButton.visibility = View.VISIBLE
            holder.itemButton.setOnClickListener{
                val intent = Intent(context, RegisterActivity::class.java)
                context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        return title.size
    }

}
package live.nirmalyasaha.me.bookstore.activities.ui.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_book_dashboard_layout.view.*
import kotlinx.android.synthetic.main.item_booklist_layout.view.*
import live.nirmalyasaha.me.bookstore.R
import live.nirmalyasaha.me.bookstore.activities.ui.activities.SoldBookDetailsActivity
import live.nirmalyasaha.me.bookstore.models.SoldBooks
import live.nirmalyasaha.me.bookstore.utils.Constants
import live.nirmalyasaha.me.bookstore.utils.GlideLoader
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class SoldBookCryptoAdapter (private val context: Context, private var list: ArrayList<SoldBooks>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_book_dashboard_layout, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is MyViewHolder) {

            GlideLoader(context).loadBookImage(model.image, holder.itemView.imgBookImage)

            holder.itemView.txtBookName.text = model.title
            holder.itemView.txtBookAuthor.text = "Sold Quantity: ${model.sold_quantity}"
            holder.itemView.txtBookPrice.text = "Rs ${model.total_amount}"

            val dateFormat = "dd MMM yyyy HH:mm"
            // Create a DateFormatter object for displaying date in specified format.
            val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())

            // Create a calendar object that will convert the date and time value in milliseconds to date.
            val calendar: Calendar = Calendar.getInstance()
            calendar.timeInMillis = model.order_date
            holder.itemView.txtUserName.text = formatter.format(calendar.time)

            holder.itemView.txtUserName.setTextColor(ContextCompat.getColor(context, R.color.colorSnackBarSuccess))


            holder.itemView.setOnClickListener {
                val intent = Intent(context, SoldBookDetailsActivity::class.java)
                intent.putExtra(Constants.EXTRA_SOLD_BOOK_DETAILS, model)
                context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}
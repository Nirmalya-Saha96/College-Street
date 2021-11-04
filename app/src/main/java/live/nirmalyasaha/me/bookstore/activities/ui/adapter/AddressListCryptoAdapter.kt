package live.nirmalyasaha.me.bookstore.activities.ui.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_address_layout.view.*
import live.nirmalyasaha.me.bookstore.R
import live.nirmalyasaha.me.bookstore.activities.ui.activities.AddEditAddressCryptoActivity
import live.nirmalyasaha.me.bookstore.activities.ui.activities.CheckoutCryptoActivity
import live.nirmalyasaha.me.bookstore.models.Address
import live.nirmalyasaha.me.bookstore.utils.Constants

class AddressListCryptoAdapter (private val context: Context, private var list: ArrayList<Address>, private var selectedAddress: Boolean): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_address_layout, parent, false))
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is MyViewHolder) {
            holder.itemView.tv_address_full_name.text = model.name
            holder.itemView.tv_address_type.text = model.type
            holder.itemView.tv_address_mobile_number.text = model.mobile
            holder.itemView.tv_address_details.text = "${model.type} Address: ${model.address}"

            //if the activity is triggered from cart activity
            if (selectedAddress) {
                holder.itemView.setOnClickListener {
                    //going to the checkout activity
                    val intent = Intent(context, CheckoutCryptoActivity::class.java)
                    intent.putExtra(Constants.EXTRA_SELECTED_ADDRESS, model)
                    context.startActivity(intent)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun notifyEditItem(activity: Activity, position: Int) {
        val intent = Intent(context, AddEditAddressCryptoActivity::class.java)
        intent.putExtra(Constants.EXTRA_ADDRESS_DETAILS, list[position])

        activity.startActivityForResult(intent, Constants.ADD_ADDRESS_REQUEST_CODE)

        // Notify any registered observers that the item at position has changed.
        notifyItemChanged(position)
    }

    private class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}
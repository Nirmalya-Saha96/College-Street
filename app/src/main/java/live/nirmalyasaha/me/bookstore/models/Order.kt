package live.nirmalyasaha.me.bookstore.models
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

//specifying what we need to store in the order collection
//just like mongoDB

//we use parcelable to convet the order to object
//use a plugin

@Parcelize
data class Order (
        val user_id: String = "",
        val items: ArrayList<Cart> = ArrayList(),
        val address: Address = Address(),
        val title: String = "",
        val image: String = "",
        val sub_total: String = "",
        val tax: String = "",
        val total_amount: String = "",
        var status: String = "",
        val date: Long = 0L,
        var id: String = ""
): Parcelable
package live.nirmalyasaha.me.bookstore.models
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

//specifying what we need to store in the sold_books collection
//just like mongoDB

//we use parcelable to convet the sold_book to object
//use a plugin

@Parcelize
data class SoldBooks (
    val user_id: String = "",
    val receipt_id: String = "",
    val title: String = "",
    val price: String = "",
    val sold_quantity: String = "",
    val image: String = "",
    val order_id: String = "",
    val order_date: Long = 0L,
    val sub_total_amount: String = "",
    val tax: String = "",
    val total_amount: String = "",
    val address: Address = Address(),
    var id: String = ""
):Parcelable
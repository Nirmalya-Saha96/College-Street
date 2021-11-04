package live.nirmalyasaha.me.bookstore.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

//specifying what we need to store in the products collection
//just like mongoDB

//we use parcelable to convet the product to object
//use a plugin

@Parcelize
data class Product (
     val user_id: String = "",
     val user_name: String = "",
     val title: String = "",
     val author: String = "",
     val price: String = "",
     val description: String = "",
     val quantity: String = "",
     val image: String = "",
     var product_id: String = ""
): Parcelable
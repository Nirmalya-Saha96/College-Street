package live.nirmalyasaha.me.bookstore.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

//specifying what we need to store in the cart collection
//just like mongoDB

//we use parcelable to convet the cart to object
//use a plugin
@Parcelize
data class Cart (
      val user_id: String = "",
      val product_owner_id: String = "",
      val product_id: String = "",
      val title: String = "",
      val author: String = "",
      var price: String = "",
      val image: String = "",
      var cart_quantity: String = "",
      var stock_quantity: String = "",
      var id: String = ""
): Parcelable
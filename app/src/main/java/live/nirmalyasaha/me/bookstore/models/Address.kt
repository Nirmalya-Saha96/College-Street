package live.nirmalyasaha.me.bookstore.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

//specifying what we need to store in the address collection
//just like mongoDB

//we use parcelable to convet the address to object
//use a plugin
@Parcelize
data class Address (
      val user_id: String = "",
      val name: String = "",
      val mobile: String = "",
      var address: String? = "",
      val type: String = "",
      var id: String = ""
): Parcelable
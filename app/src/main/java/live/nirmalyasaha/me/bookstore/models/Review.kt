package live.nirmalyasaha.me.bookstore.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

//specifying what we need to store in the review collection
//just like mongoDB

//we use parcelable to convet the review to object
//use a plugin

@Parcelize
data class Review (
        val user_id: String = "",
        val user_name: String = "",
        val product_id: String = "",
        val text: String = "",
        val image: String = "",
        var review_id: String = ""
): Parcelable
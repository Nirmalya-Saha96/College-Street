package live.nirmalyasaha.me.bookstore.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


//specifying what we need to store in the users collection
//just like mongoDB

//we use parcelable to convet the user to object
//use a plugin

@Parcelize
data class User(
    val id: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val image: String = "",
    val mobile: Long = 0,
    val gender: String = "",
    val website: String = "",
    val profesion: String = "",
    val hobby: String = "",
    val profileCompleted: Int = 0
): Parcelable
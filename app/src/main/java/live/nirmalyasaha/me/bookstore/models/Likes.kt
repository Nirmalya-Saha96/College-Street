package live.nirmalyasaha.me.bookstore.models
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

//specifying what we need to store in the likes collection
//just like mongoDB

//we use parcelable to convet the likes to object
//use a plugin
@Parcelize
data class Likes (
    val blog_id: String = "",
    val user_id: String = "",
    var id: String = ""
): Parcelable
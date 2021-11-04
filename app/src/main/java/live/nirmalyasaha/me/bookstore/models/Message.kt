package live.nirmalyasaha.me.bookstore.models
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

//specifying what we need to store in the message collection
//just like mongoDB

//we use parcelable to convet the message to object
//use a plugin
@Parcelize
data class Message(
    val fromId: String = "",
    val toId: String = "",
    var text: String? = "",
    var timestamp: Long = 0L,
    var id: String = ""
): Parcelable
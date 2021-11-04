package live.nirmalyasaha.me.bookstore.models
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

//specifying what we need to store in the blogReview collection
//just like mongoDB

//we use parcelable to convet the blogReview to object
//use a plugin

@Parcelize
data class BlogReview (
        val user_id: String = "",
        val user_name: String = "",
        val title: String = "",
        val searchText: String = "",
        val description: String = "",
        val image: String = "",
        var id: String = ""
): Parcelable
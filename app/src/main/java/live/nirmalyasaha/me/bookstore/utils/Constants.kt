package live.nirmalyasaha.me.bookstore.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap

//stores all our constants
object Constants {
    //holds the user collection
    const val USERS: String = "user"
    const val PRODUCTS_BOOK: String = "product_book"
    const val REVIEWS: String = "reviews"
    const val CART_ITEMS: String = "cart_items"
    const val ADDRESS: String = "address"
    const val ORDER: String = "order"
    const val SOLD_BOOKS: String = "sold_books"
    const val MESSAGE: String = "message"
    const val BLOG_REVIEW: String = "blog"
    const val LIKE: String = "likes"

    //shared preferences
    const val BOOKSTORE_PREFERENCES: String = "BookstorePref"
    const val LOGGED_IN_USERNAME: String = "logged_in_username"

    //parcelable
    const val USER_EXTRA_DETAILS: String = "user_extra_details"

    //code
    const val READ_STORAGE_PERMISSION_CODE = 2
    const val IMAGE_REQUEST_CODE = 5
    const val  ADD_ADDRESS_REQUEST_CODE: Int = 121
    const val REQUEST_IMAGE_CAPTURE: Int = 1

    //tab options
    const val MALE: String = "male"
    const val FEMALE: String = "female"
    const val STUDENT: String = "student"
    const val EMPLOYED: String = "employed"

    //profile
    const val MOBILE: String = "mobile"
    const val GENDER: String = "gender"
    const val PROFESION: String = "profesion"
    const val WEBSITE: String = "website"
    const val HOBBY: String = "hobby"
    const val FIRST_NAME: String = "firstName"
    const val LAST_NAME: String = "lastName"
    const val IMAGE: String = "image"
    const val PROFILE_COMPLETED: String = "profileCompleted"

    //product
    const val PRODUCT_TITLE: String = "title"
    const val PRODUCT_AUTHOR: String = "author"
    const val PRODUCT_PRICE: String = "price"
    const val PRODUCT_DECS: String = "description"
    const val PRODUCT_QUANTITY: String = "quantity"
    const val STOCK_QUANTITY: String = "quantity"

    //cart
    const val DEFAULT_CART_QUANTITY: String = "1"
    const val PRODUCT_CART_ID: String = "product_id"
    const val CART_QUANTITY: String = "cart_quantity"
    const val CART_PRICE: String = "price"

    //address
    const val HOME: String = "Home"
    const val OFFICE: String = "Office"
    const val OTHER: String = "Other"
    const val EXTRA_ADDRESS_DETAILS: String = "AddressDetails"
    const val EXTRA_SELECTED_ADDRESS: String = "extra_selected_address"

    //sold book
    const val PENDING: String = "Pending"
    const val TRANSIT: String = "In Transit"
    const val DELIVERED: String = "Delivered"
    const val STATUS: String = "status"

    //message
    const val FROM_ID: String = "fromId"
    const val TO_ID: String = "toId"
    const val TIMESTAMPS: String = "timestamp"

    //search
    const val BOOK_TITLE: String = "Book Title"
    const val BOOK_AUTHOR: String = "Book Author"
    const val REVIEW_BLOG: String = "Review / Blog"
    const val TITLE: String = "title"
    const val AUTHOR: String = "author"
    const val BLOG_SEARCH: String = "searchText"

    //likes
    const val USER_id: String = "blog_id"

    //bot
    const val SEND_ID = "SEND_ID"
    const val RECEIVE_ID = "RECEIVE_ID"

    const val OPEN_GOOGLE = "Opening Google..."
    const val OPEN_SEARCH = "Searching..."
    const val LINKEDIN = "Opening creator's linkedin profile..."
    const val PORTFOLIO = "Opening creator's portfolio website..."
    const val QUIT = "Quiting..."

    //image location
    const val BOOK_IMAGE: String = "Book_Image"
    const val USER_PROFILE_IMAGE: String = "User_Profile_Image"
    const val BLOG_IMAGE: String = "Blog Image"

    const val USER_ID: String = "user_id"
    const val EXTRA_PRODUCT_ID: String = "extra_product_id"
    const val EXTRA_USER_ID: String = "extra_user_id"
    const val EXTRA_PRODUCT_OWNER_ID: String = "extra_product_owner_id"
    const val PRODUCT_ID: String = "product_id"
    const val BOOK_ENTITY: String = "book_entity"
    const val PRODUCT_EXTRA_DETAILS: String = "extra_product_details"
    const val EXTRA_SELECT_ADDRESS: String = "extra_select_address"
    const val EXTRA_ORDER_DETAILS: String = "extra_order_details"
    const val EXTRA_SOLD_BOOK_DETAILS: String = "extra_sold_book_details"
    const val EXTRA_USER: String = "extra_user"
    const val BLOG_REVIEW_EXTRA_DETAILS: String = "extra_blog_review_details"
    const val EXTRA_BLOG_ID: String = "extra_blog_id"
    const val EXTRA_EXTRACTED_TEXT: String = "extra_extracted_text"

    //function used to show a chooser
    fun showImageChooser(activity: Activity){

        //boiler plate

        //choosing the image by an intent
        val imageIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        //launching the activity
        activity.startActivityForResult(imageIntent, IMAGE_REQUEST_CODE)
    }

    //function used to give the file type extension used to store in the firebase storage
    //basically used for image uploading
    //returns the extension
    fun getFileExtension(activity: Activity, uri: Uri?): String?{
        //Mime type gives two way map mine types to file extension and vice versa
        //get singleton give the singleton instance of mime type
        //get ex returns the registered extension for the given mime type
        //con..re..get return the mime type pf the given content uri
        return MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }
}
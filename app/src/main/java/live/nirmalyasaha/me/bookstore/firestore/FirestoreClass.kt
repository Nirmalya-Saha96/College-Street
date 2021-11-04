package live.nirmalyasaha.me.bookstore.firestore

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import live.nirmalyasaha.me.bookstore.activities.ui.activities.*
import live.nirmalyasaha.me.bookstore.activities.ui.fragment.*
import live.nirmalyasaha.me.bookstore.models.*
import live.nirmalyasaha.me.bookstore.utils.Constants

class FirestoreClass {

    //getting the instance of the firebase firestore
    private val mFirestore = FirebaseFirestore.getInstance()

    //function to store the users details from the register activity to the user collections
    //using the User model
    fun registerUser(activity: RegisterActivity, userInfo: User){
        //user is the collection
        //if created once it will not be created again
        mFirestore.collection(Constants.USERS)
            //setting the doccument id as the unique user id coming from authentication and then user model
            .document(userInfo.id)
            //set the field to merge so that we can add later
            //and now set the entries coming from register activity
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                //calls the function register activity
                //to display the toast and hide the progress bar
                activity.userRegisterSuccess()
            }
            .addOnFailureListener { e->
                //hides the progress bar
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while registering the user",e)
            }
    }

    //function to return the current user id from the authentication
    fun getCurrentUserID(): String {
        //getting the current user
        val currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserID = ""
        //if logged in
        if(currentUser != null){
            //store the current user id
            currentUserID = currentUser.uid
        }
        return currentUserID
    }

    //for getting the currently logged in user details
    fun getUserDetails(activity: Activity){
        //sending the collection name where we wanted data
        mFirestore.collection(Constants.USERS)
             //getting the fields
             //of the current user
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener{document ->
                Log.i(activity.javaClass.simpleName, document.toString())
                //convert the document to user object
                //data model
                val user = document.toObject(User::class.java)!!


                //creating a shared preference object
                val sharedPreferences = activity.getSharedPreferences(Constants.BOOKSTORE_PREFERENCES, Context.MODE_PRIVATE)
                //creating the editor to store value in shared preference
                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                editor.putString(Constants.LOGGED_IN_USERNAME, "${user.firstName} ${user.lastName}")
                editor.apply()


                //passing the result to the login activity
                when(activity){
                    is LoginActivity ->{
                        //passing result
                        activity.userLoggedInSuccess(user)
                    }
                    is SettingsActivity ->{
                        activity.getUserDetailsSuccess(user)
                    }
                    is AddReviewActivity ->{
                        activity.getLoadUserSuccess(user)
                    }
                }
            }
            .addOnFailureListener{e->
                when (activity){
                    is LoginActivity ->{
                        activity.hideProgressDialog()
                    }
                    is SettingsActivity->{
                        activity.hideProgressDialog()
                    }
                    is AddReviewActivity ->{
                        activity.hideProgressDialog()
                    }
                }
                Log.e(activity.javaClass.simpleName ,"Error while getting user details",e)
            }
    }

    //function used to update user profile details
    fun updateUserProfileData(activity: Activity, userHashMap: HashMap<String, Any>){
        //updating the user informations
        mFirestore.collection(Constants.USERS)
                .document(getCurrentUserID())
                .update(userHashMap)
                .addOnSuccessListener {
                    //when profile activity
                    when (activity){
                        is ProfileActivity ->{
                            //hiding the progress bar
                            //and starting the main activity
                            activity.userProfileUpdateSuccess()
                        }
                    }
                }
                .addOnFailureListener{e->
                    when (activity){
                        is ProfileActivity ->{
                            //hiding the progress bar
                            activity.hideProgressDialog()
                        }
                    }
                    Log.e(activity.javaClass.simpleName, "Error while updating user details", e)
                }
    }

    //function used to upload image to cloud storage
    fun uploadImageToCloudStorage(activity: Activity, imageFileURI: Uri?, imageType: String){

        //boiler plate

        //setting the path of the storage
        //reference
        val storageRef: StorageReference = FirebaseStorage.getInstance().reference.child(
            imageType + System.currentTimeMillis() + "." + Constants.getFileExtension(activity, imageFileURI)
        )

        //storing in the firebase storage
        storageRef.putFile(imageFileURI!!).addOnSuccessListener{taskSnapshot->
            //successfully uploaded image
            Log.e("Firebase Image Url", taskSnapshot.metadata!!.reference!!.downloadUrl.toString())

            //getting the image link
            taskSnapshot.metadata!!.reference!!.downloadUrl
                .addOnSuccessListener{uri->
                    Log.e("Download Image Uri", uri.toString())

                    //is profile activity
                    when(activity){
                        is ProfileActivity ->{
                            //sending the image uri
                            activity.imageUploadSuccess(uri.toString())
                        }
                        is AddProductActivity->{
                            //sending the image uri
                            activity.imageUploadSuccess(uri.toString())
                        }
                        is UpdateProductActivity->{
                            activity.imageUploadSuccess(uri.toString())
                        }
                        is AddBlogReviewActivity ->{
                            activity.imageUploadSuccess(uri.toString())
                        }
                        is EditMyBlogReviewActivity->{
                            activity.imageUploadSuccess(uri.toString())
                        }
                    }
                }
        }
            .addOnFailureListener{exception->
                when (activity){
                    is ProfileActivity ->{
                        //hiding the progress bar
                        activity.hideProgressDialog()
                    }
                    is AddProductActivity->{
                        //hiding the progress bar
                        activity.hideProgressDialog()
                    }
                    is UpdateProductActivity->{
                        //hiding the progress bar
                        activity.hideProgressDialog()
                    }
                    is AddBlogReviewActivity ->{
                        //hiding the progress bar
                        activity.hideProgressDialog()
                    }
                    is EditMyBlogReviewActivity->{
                        activity.hideProgressDialog()
                    }
                }

                Log.e(activity.javaClass.simpleName, exception.message, exception)
            }
    }

    //function to store the product book details from the add product activity to the product_book collections
    //using the Product model
    fun uploadProductBookDetails(activity: AddProductActivity, productInfo: Product){
        //product_book is the collection
        //if created once it will not be created again
        mFirestore.collection(Constants.PRODUCTS_BOOK)
                .document()
                //set the field to merge so that we can add later
                //and now set the entries coming from add product activity
                .set(productInfo, SetOptions.merge())
                .addOnSuccessListener {
                    //calls the function product Book Upload Success
                    //to display the toast and hide the progress bar
                    activity.productBookUploadSuccess()
                }
                .addOnFailureListener { e->
                    //hides the progress bar
                    activity.hideProgressDialog()
                    Log.e(activity.javaClass.simpleName, "Error while uploading book info", e)
                }
    }

    //function used to update user profile details
    fun updateProductData(activity: Activity, productId: String, productHashMap: HashMap<String, Any>){
        //updating the user informations
        mFirestore.collection(Constants.PRODUCTS_BOOK)
            .document(productId)
            .update(productHashMap)
            .addOnSuccessListener {
                //when profile activity
                when (activity){
                    is UpdateProductActivity ->{
                        //hiding the progress bar
                        //and starting the main activity
                        activity.productUpdateSuccess()
                    }
                }
            }
            .addOnFailureListener{e->
                when (activity){
                    is UpdateProductActivity ->{
                        //hiding the progress bar
                        activity.hideProgressDialog()
                    }
                }
                Log.e(activity.javaClass.simpleName, "Error while updating product details", e)
            }
    }

    //function to get all details of the products book added by the currently logged in user
    //by receiving a list of books
    fun getMyBookList(fragment: Fragment){
        mFirestore.collection(Constants.PRODUCTS_BOOK)
            //getting the book list where the currently logged in user id matches with the user id given in product_book collection
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .get()
            .addOnSuccessListener { document->
                Log.e("Product Book List", document.documents.toString())
                //creating an array list to store the book list
                val bookList: ArrayList<Product> = ArrayList()
                for(i in document.documents){
                    //making an object of every book
                    val book = i.toObject(Product::class.java)
                    //assigning the product id as the document id
                    //as it was not assigned earlier
                    book!!.product_id = i.id
                    //adding the book to the book list
                    bookList.add(book)
                }

                when(fragment){
                    //when product fragment
                    is ProductFragment ->{
                        //calling the success methode
                        //which hides the progress bar
                        fragment.successProductBook(bookList)
                    }
                }
            }
            .addOnFailureListener {  e->
                when(fragment){
                    //when product fragment
                    is ProductFragment ->{
                        //hiding the progress bar
                        fragment.hideProgressDialog()
                        Log.e(fragment.javaClass.simpleName, "Error while loading book info", e)
                    }
                }

            }
    }

    //function used to delete a book
    fun deleteBook(fragment: ProductFragment, productID: String){
        mFirestore.collection(Constants.PRODUCTS_BOOK)
                .document(productID)
                .delete()
                .addOnSuccessListener {
                    fragment.bookDeleteSuccess()
                }
                .addOnFailureListener{ e->
                    //hiding the progress bar
                    fragment.hideProgressDialog()

                    Log.e(fragment.requireActivity().javaClass.simpleName,"Error while deleting book",  e)
                }
    }

    //function used to get the book details when clicked
    //specific book
    fun getBookDetails(activity: ProductBookDetailsActivity, productID: String){
        mFirestore.collection(Constants.PRODUCTS_BOOK)
            .document(productID)
            .get()
            .addOnSuccessListener { document ->
                //creating a product object from the document
                val book = document.toObject(Product::class.java)
                //if the book is not null
                //then passing the book details to the activity
                if(book != null){
                    activity.bookDetailsSuccess(book)
                }
                else{
                    //deletes the book if not found in the firestore from the sqlite database
                    activity.bookDetailsFailure()
                }
            }
            .addOnFailureListener{ e->
                //hiding the progress bar
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while getting the book details.", e)
            }
    }

    //function used to get the book details when clicked
    //specific book
    fun getBookDetailsFragment(fragment: ProductFragment, productID: String){
        mFirestore.collection(Constants.PRODUCTS_BOOK)
                .document(productID)
                .get()
                .addOnSuccessListener { document ->
                    //creating a product object from the document
                    val book = document.toObject(Product::class.java)
                    //if the book is not null
                    //then passing the book details to the activity
                    if(book != null){
                        fragment.bookDetailsSuccessFragment(book)
                    }
                }
                .addOnFailureListener{ e->
                    //hiding the progress bar
                    fragment.hideProgressDialog()
                    Log.e(fragment.javaClass.simpleName, "Error while getting the book details.", e)
                }
    }

    //function to get all details of the products book
    //by receiving a list of books
    fun getBookListItem(fragment: DashboardFragment){
        mFirestore.collection(Constants.PRODUCTS_BOOK)
                .get()
                .addOnSuccessListener { document->
                    Log.e(fragment.javaClass.simpleName, document.documents.toString())

                    //creating an array list to store the book list
                    val booksList: ArrayList<Product> = ArrayList()

                    for(i in document.documents){
                        //making an object of every book
                        val book = i.toObject(Product::class.java)!!
                        //assigning the product id as the document id
                        //as it was not assigned earlier
                        book.product_id  = i.id
                        //adding the book to the book list
                        booksList.add(book)
                    }

                    //on success methode
                    //passing all the book list
                    fragment.successBookListItem(booksList)
                }
                .addOnFailureListener { e->
                    //hiding the progress bar
                    fragment.hideProgressDialog()
                    Log.e(fragment.javaClass.simpleName, "Error while displaying all the books", e)
                }
    }

    //function used to get the profile details when clicked
    //specific profile
    fun getProfileDetails(activity: PorfileDetailsActivity, userID: String){
        mFirestore.collection(Constants.USERS)
            .document(userID)
            .get()
            .addOnSuccessListener { document ->
                //creating a product object from the document
                val user = document.toObject(User::class.java)
                //if the user is not null
                //then passing the user details to the activity
                if(user != null){
                    activity.getProfileDetailsSuccess(user)
                }
            }
            .addOnFailureListener{ e->
                //hiding the progress bar
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while getting the profile details.", e)
            }
    }

    //function to store the review book details from the add review activity to the reviews collections
    //using the Review model
    fun uploadReviewBookDetails(activity: AddReviewActivity, reviewInfo: Review){
        //review is the collection
        //if created once it will not be created again
        mFirestore.collection(Constants.REVIEWS)
            .document()
            //set the field to merge so that we can add later
            //and now set the entries coming from add review activity
            .set(reviewInfo, SetOptions.merge())
            .addOnSuccessListener {
                //calls the function product Review Upload Success
                //to display the toast and hide the progress bar
                activity.reviewUploadSuccess()
            }
            .addOnFailureListener { e->
                //hides the progress bar
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while uploading review info", e)
            }
    }

    //function used to get all the review details of the selected product
    //by passing the product id
    fun getReviewBook(activity: ReviewActivity, productId: String){
        mFirestore.collection(Constants.REVIEWS)
                //getting the review list where the product_id matches with the product id given in reviews collection
                .whereEqualTo(Constants.PRODUCT_ID, productId)
                .get()
                .addOnSuccessListener { document->
                    Log.e("Review Book List", document.documents.toString())
                    //creating an array list to store the review list
                    val reviewList: ArrayList<Review> = ArrayList()
                    for(i in document.documents){
                        //making an object of every review
                        val review = i.toObject(Review::class.java)
                        //assigning the review id as the document id
                        //as it was not assigned earlier
                        review!!.review_id = i.id
                        //adding the review to the review list
                        reviewList.add(review)
                    }

                    //passing the review list on success
                    activity.successReviewBook(reviewList)
                    }

                .addOnFailureListener {  e->
                            //hiding the progress bar
                            activity.hideProgressDialog()
                            Log.e(activity.javaClass.simpleName, "Error while loading review info", e)
                }

    }

    fun deleteReviewSwipe(activity: ReviewActivity, reviewId: String){
        mFirestore.collection(Constants.REVIEWS)
            .document(reviewId)
            .delete()
            .addOnSuccessListener{
                activity.successReviewDelete()
            }
            .addOnFailureListener{e->
                //hiding the progress bar
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while deleting address", e)
            }
    }

    //function used to add items to cart
    fun addCartItems(activity: ProductBookDetailsActivity, addToCart: Cart){
        mFirestore.collection(Constants.CART_ITEMS)
                .document()
                .set(addToCart, SetOptions.merge())
                .addOnSuccessListener {
                    // Here call a function of base activity for transferring the result to it.
                    activity.addToCartSuccess()
                }
                .addOnFailureListener {e->
                    //hide the progress bar
                    activity.hideProgressDialog()

                    Log.e(activity.javaClass.simpleName, "Error while creating the document for cart item.", e)
                }

    }

    //function used to check a book is present in the cart
    fun checkIfItemExistInCart(activity: ProductBookDetailsActivity, productId: String) {
        mFirestore.collection(Constants.CART_ITEMS)
                //where the user is the currently logged in user
                //the product is the selected product
                .whereEqualTo(Constants.USER_ID, getCurrentUserID())
                .whereEqualTo(Constants.PRODUCT_ID, productId)
                .get()
                .addOnSuccessListener { document ->
                    Log.e(activity.javaClass.simpleName, document.documents.toString())

                    // If the document size is greater than 1 it means the product is already added to the cart.
                    if (document.documents.size > 0) {
                        activity.bookExistsInCart()
                    } else {
                        activity.hideProgressDialog()
                    }
                }
                .addOnFailureListener { e ->
                    // Hide the progress dialog if there is an error.
                    activity.hideProgressDialog()

                    Log.e(activity.javaClass.simpleName, "Error while checking the existing cart list.", e)
                }
    }

    //function used to get all the cart items of the currently logged in user
    fun getMyCartList(activity: Activity){
        mFirestore.collection(Constants.CART_ITEMS)
            //getting the cart list where the user id matches with the currently logged in user given in cart collection
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                Log.i(activity.javaClass.simpleName, document.documents.toString())
                //creating an array list to store the cart list
                val cartList: ArrayList<Cart> = ArrayList()

                for(i in document.documents){
                    //making an object of every cart
                    val cart = i.toObject(Cart::class.java)!!
                    //assigning the cart id as the document id
                    //as it was not assigned earlier
                    cart.id = i.id
                    //adding the cart to the cart list
                    cartList.add(cart)
                }

                //when cart list activity
                when(activity){
                    is CartListActivity ->{
                        //passing the cart list
                        activity.successCartList(cartList)
                    }
                    is CheckoutCryptoActivity->{
                        activity.successGetCartList(cartList)
                    }
                }
            }
            .addOnFailureListener { e->
                Log.e(activity.javaClass.simpleName, "Error while getting the cart list", e)

                when(activity){
                    is CartListActivity ->{
                        //hide the pregress bar
                        activity.hideProgressDialog()
                    }
                    is CheckoutCryptoActivity->{
                        //hiding the progress bar
                        activity.hideProgressDialog()
                    }
                }
            }
    }

    //function used to get all book details
    //basically used by the cart list activity to tally all the stock quantity
    //and to remove the deleted books
    fun getAllBookList(activity: Activity){
        mFirestore.collection(Constants.PRODUCTS_BOOK)
                .get()
                .addOnSuccessListener { document ->
                    Log.i("Product List", document.documents.toString())
                    //creating an array list to store the book list
                    val bookList: ArrayList<Product> = ArrayList()
                    for(i in document.documents){
                        //making an object of every book
                        val book = i.toObject(Product::class.java)
                        //assigning the book id as the document id
                        //as it was not assigned earlier
                        book!!.product_id = i.id
                        //adding the book to the book list
                        bookList.add(book)
                    }

                    when(activity){
                        is CartListActivity->{
                            //passing the book list
                            activity.successGetBookList(bookList)
                        }
                        is CheckoutCryptoActivity->{
                            //passing the book list
                            activity.successGetBookList(bookList)
                        }
                    }
                }
                .addOnFailureListener { e->
                    when(activity){
                        is CartListActivity->{
                            //hiding the progress bar
                            activity.hideProgressDialog()
                        }
                        is CheckoutCryptoActivity->{
                            //hiding the progress bar
                            activity.hideProgressDialog()
                        }
                    }
                    Log.e("Get book list", "Error while getting the book list")
                }
    }

    //function used to delete the cart items
    fun deleteCartItem(context: Context, cart_id: String){
        mFirestore.collection(Constants.CART_ITEMS)
                .document(cart_id)
                .delete()
                .addOnSuccessListener{
                    when(context){
                        is CartListActivity ->{
                            context.cartDeleteSuccess()
                        }
                    }
                }
                .addOnFailureListener { e->
                    // Hide the progress dialog if there is any error.
                    when (context) {
                        is CartListActivity -> {
                            context.hideProgressDialog()
                        }
                    }
                    Log.e(context.javaClass.simpleName, "Error while removing the item from the cart list.", e)
                }
    }

    //function used to update the cart list
    //basically the cart quantity by minus and plus
    fun updateCart(context: Context, cart_id: String, cartHashMap: HashMap<String, Any>){
        mFirestore.collection(Constants.CART_ITEMS)
                .document(cart_id)
                .update(cartHashMap)
                .addOnSuccessListener{
                    when (context){
                        is CartListActivity ->{
                            context.updateCartSuccess()
                        }
                    }
                }
                .addOnFailureListener { e->
                    // Hide the progress dialog if there is any error.
                    when (context) {
                        is CartListActivity -> {
                            context.hideProgressDialog()
                        }
                    }
                    Log.e(context.javaClass.simpleName, "Error while updating the item from the cart list.", e)
                }
    }

    //function used to add address in firebase firestore
    //includes crypto
    fun  addAddressCrypto(activity: AddEditAddressCryptoActivity, addressInfo: Address){
        mFirestore.collection(Constants.ADDRESS)
                .document()
                .set(addressInfo, SetOptions.merge())
                .addOnSuccessListener{
                    activity.addEditAddressSuccess()
                }
                .addOnFailureListener { e->
                    //hiding the progress bar
                    activity.hideProgressDialog()
                    Log.e("Add Address", "Error while adding the address", e)
                }
    }

    //function used to get the address of currently logged in user
    fun getAddressList(activity: AddressListCryptoActivity){
        mFirestore.collection(Constants.ADDRESS)
                .whereEqualTo(Constants.USER_ID, getCurrentUserID())
                .get()
                .addOnSuccessListener{document->
                    Log.i(activity.javaClass.simpleName, document.documents.toString())
                    //creating an array list to store the address list
                    val addressList: ArrayList<Address> = ArrayList()
                    for(i in document.documents){
                        //making an object of every address
                        val address = i.toObject(Address::class.java)!!
                        //assigning the address id as the document id
                        //as it was not assigned earlier
                        address.id = i.id
                        //decrypting
                        address.address = activity.decrypt(address.address)
                        //adding the address to the address list
                        addressList.add(address)
                    }

                    //passing the address list
                    activity.successGetAddressList(addressList)
                }
                .addOnFailureListener{e->
                    //hiding the progress bar
                    activity.hideProgressDialog()
                    Log.e("Get address list", "Error while getting the address list", e)
                }
    }

    //function used to update the address
    fun updateAddressDetails(activity: AddEditAddressCryptoActivity, addressInfo: Address, addressId: String){
        mFirestore.collection(Constants.ADDRESS)
                //getting the perticular id to be updated
                .document(addressId)
                .set(addressInfo, SetOptions.merge())
                .addOnSuccessListener{
                    activity.updateAddressSuccess()
                }
                .addOnFailureListener { e->
                    //hiding the progress bar
                    activity.hideProgressDialog()
                    Log.e(activity.javaClass.simpleName, "Error while updating address", e)
                }
    }

    //function used to delete the address on swiped
    fun deleteAddress(activity: AddressListCryptoActivity, addressId: String){
        mFirestore.collection(Constants.ADDRESS)
                .document(addressId)
                .delete()
                .addOnSuccessListener{
                    activity.successDeleteAddress()
                }
                .addOnFailureListener{e->
                    //hiding the progress bar
                    activity.hideProgressDialog()
                    Log.e(activity.javaClass.simpleName, "Error while deleting address", e)
                }
    }

    //function to upload the order
    //with encrypted address
    fun placeOrder(activity: CheckoutCryptoActivity, order: Order){
        mFirestore.collection(Constants.ORDER)
                .document(order.id)
                .set(order, SetOptions.merge())
                .addOnSuccessListener{
                    activity.successOrderPlace()
                }
                .addOnFailureListener{e->
                    //hiding the progress bar
                    activity.hideProgressDialog()
                    Log.e(activity.javaClass.simpleName, "Error while placing order", e)
                }
    }

    //function used to update the quantity of products
    //and delete the cart
    //creating the sold book data model
    //once the order has placed successfully
    fun updateProductCartDetails(activity: CheckoutCryptoActivity, cartList: ArrayList<Cart>, order: Order){
        val writeBatch = mFirestore.batch()
        for(cartItem in cartList){
            //val productHashMap = HashMap<String, Any>()

            //productHashMap[Constants.STOCK_QUANTITY] = (cartItem.stock_quantity.toInt() - cartItem.cart_quantity.toInt()).toString()

            val soldBook = SoldBooks(
                cartItem.product_owner_id,
                getCurrentUserID(),
                cartItem.title,
                cartItem.price,
                cartItem.cart_quantity,
                cartItem.image,
                order.id,
                order.date,
                order.sub_total,
                order.tax,
                order.total_amount,
                order.address
            )

            val documentRef = mFirestore.collection(Constants.SOLD_BOOKS)
                    .document(cartItem.product_id)

            writeBatch.set(documentRef, soldBook)
        }

        for(cartItem in cartList){
            val productHashMap = HashMap<String, Any>()

            productHashMap[Constants.STOCK_QUANTITY] = (cartItem.stock_quantity.toInt() - cartItem.cart_quantity.toInt()).toString()

            val documentRef = mFirestore.collection(Constants.PRODUCTS_BOOK)
                .document(cartItem.product_id)

            writeBatch.update(documentRef, productHashMap)
        }

        for(cartItem in cartList){
            val documentRef = mFirestore.collection(Constants.CART_ITEMS)
                    .document(cartItem.id)
            writeBatch.delete(documentRef)
        }

        writeBatch.commit().addOnSuccessListener {
            activity.successProductCartDetailsUpdate()
        }
                .addOnFailureListener { e->
                    //hiding the progress bar
                    activity.hideProgressDialog()
                    Log.e(activity.javaClass.simpleName, "Error while updating all details after placing order", e)
                }
    }

    //function used to get all the order list of the currently logged in user
    //and decrypting the address
    fun getMyOrderList(fragment: OrderFragment){
        mFirestore.collection(Constants.ORDER)
                .whereEqualTo(Constants.USER_ID, getCurrentUserID())
                .get()
                .addOnSuccessListener { document->
                    val orderList: ArrayList<Order> = ArrayList()

                    for(i in document.documents){
                        val order = i.toObject(Order::class.java)!!
                        order.address.address = fragment.decrypt(order.address.address)
                        orderList.add(order)
                    }

                    fragment.successGetOrderList(orderList)
                }
                .addOnFailureListener { e->
                    //hide the progress bar
                    fragment.hideProgressDialog()
                    Log.e(fragment.javaClass.simpleName, "Error while getting the order list", e)
                }
    }

    //function used to get all the sold products of currently logged in user
    //and decrypting the address using cryptography
    fun getSoldBookList(fragment: SoldBookFragment){
        mFirestore.collection(Constants.SOLD_BOOKS)
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .get()
            .addOnSuccessListener { document->
                val list: ArrayList<SoldBooks> = ArrayList()
                for(i in document.documents){
                    val soldBook = i.toObject(SoldBooks::class.java)!!
                    soldBook.id = i.id
                    soldBook.address.address = fragment.decrypt(soldBook.address.address)
                    list.add(soldBook)
                }

                fragment.successGetSoldBookList(list)
            }
            .addOnFailureListener { e->
                //hide the progress bar
                fragment.hideProgressDialog()
                Log.e(fragment.javaClass.simpleName, "Error while getting the sold books list", e)
            }
    }

    //function used to update status
    fun updateStatus(activity: SoldBookDetailsActivity, orderId: String, soldBookStatusHashMap: HashMap<String, Any>){
        //updating the user informations
        mFirestore.collection(Constants.ORDER)
                .document(orderId)
                .update(soldBookStatusHashMap)
                .addOnSuccessListener {
                    activity.successStatusUpdate()
                }
                .addOnFailureListener{e->
                    //hiding the progress bar
                    activity.hideProgressDialog()
                    Log.e(activity.javaClass.simpleName, "Error while updating user details", e)
                }
    }

    //function used to cancel an order
    fun cancelOrder(activity: OrderDetailsActivity, orderID: String){
        mFirestore.collection(Constants.ORDER)
                .document(orderID)
                .delete()
                .addOnSuccessListener {
                    activity.orderCancelSuccess()
                }
                .addOnFailureListener{ e->
                    //hiding the progress bar
                    activity.hideProgressDialog()

                    Log.e(activity.javaClass.simpleName, "Error while cancelling order", e)
                }
    }

    //function used to get all the order list of the currently logged in user
    //and decrypting the address
    fun getAllOrderList(fragment: SoldBookFragment){
        mFirestore.collection(Constants.ORDER)
                .get()
                .addOnSuccessListener { document->
                    val orderList: ArrayList<Order> = ArrayList()

                    for(i in document.documents){
                        val order = i.toObject(Order::class.java)!!
                        orderList.add(order)
                    }

                    fragment.successGetAllOrderList(orderList)
                }
                .addOnFailureListener { e->
                    //hide the progress bar
                    fragment.hideProgressDialog()
                    Log.e(fragment.javaClass.simpleName, "Error while getting the order list", e)
                }
    }

    //function used to delete the sold book which is cancelled
    fun deleteSoldBookCancelled(fragment: SoldBookFragment, bookId: String){
        mFirestore.collection(Constants.SOLD_BOOKS)
                .document(bookId)
                .delete()
                .addOnSuccessListener {
                    fragment.successDeleteCancelOrder()
                }
                .addOnFailureListener{ e->
                    //hiding the progress bar
                    fragment.hideProgressDialog()

                    Log.e(fragment.javaClass.simpleName, "Error while deleting the sold book", e)
                }
    }

    //function used to get all the users
    fun getAllUsers(activity: NewMessengerActivity){
        mFirestore.collection(Constants.USERS)
            .get()
            .addOnSuccessListener { document->
                //creating an array list to store the user list
                val usersList: ArrayList<User> = ArrayList()

                for(i in document.documents){
                    //making an object of every book
                    val user = i.toObject(User::class.java)!!
                    usersList.add(user)
                }

                //on success methode
                //passing all the user list
                activity.successGetAllUsers(usersList)
            }
            .addOnFailureListener { e->
                //hiding the progress bar
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while getting all the users list", e)
            }
    }

    //function used to send message
    //crypto
    fun sendMessage(activity: ChatCryptoActivity, chat: Message){
        mFirestore.collection(Constants.MESSAGE)
            .document()
            .set(chat, SetOptions.merge())
            .addOnSuccessListener {
                Log.i("Message", "Sent")
                activity.success()
            }
            .addOnFailureListener { e->
                Log.e(activity.javaClass.simpleName, "Error while sending messages", e)
            }
    }

//    fun getAllChatList(activity: Activity, fromId: String, toId: String){
//        mFirestore.collection(Constants.MESSAGE)
//                .orderBy(Constants.TIMESTAMPS)
//                .get()
//                .addOnSuccessListener{document->
//                    val chatList: ArrayList<Message> = ArrayList()
//                    for(i in document.documents){
//                        val chat = i.toObject(Message::class.java)!!
//                        chat.id = i.id
//                        if(fromId == chat.fromId || fromId == chat.toId){
//                            if(toId == chat.fromId || toId == chat.toId){
//                                chatList.add(chat)
//                            }
//                        }
//                    }
//                    when(activity){
//                        is ChatCryptoActivity->{
//                            activity.successGetAllMessages(chatList)
//                        }
//                    }
//                }
//                .addOnFailureListener{e->
//                    when(activity){
//                        is ChatCryptoActivity->{
//                            //activity.hideProgressDialog()
//                            Log.e(activity.javaClass.simpleName, "Error while getting chat list")
//                        }
//                    }
//                }
//    }

    fun getAllUnreadMessages(activity: MessengerActivity){
        mFirestore.collection(Constants.MESSAGE)
                .orderBy(Constants.TIMESTAMPS, Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener{document->
                    val unreadList: ArrayList<Message> = ArrayList()
                    for(i in document.documents){
                        val unread = i.toObject(Message::class.java)!!
                        unread.id = i.id
                        if(unread.toId == getCurrentUserID()){
                            unread.text = activity.decrypt(unread.text)
                            unreadList.add(unread)
                        }
                    }
                    activity.successGetAllUnreadMessage(unreadList)
                }
                .addOnFailureListener{e->
                    activity.hideProgressDialog()
                    Log.e(activity.javaClass.simpleName, "Error while getting unread list")
                }
    }

    fun getUser(activity: MessengerActivity, userId: String){
        mFirestore.collection(Constants.USERS)
                .document(userId)
                .get()
                .addOnSuccessListener { document ->
                    val user = document.toObject(User::class.java)!!
                    activity.successGetUser(user)
                }
                .addOnFailureListener{e->
                    activity.hideProgressDialog()
                    Log.e(activity.javaClass.simpleName, "Error while getting user")
                }
    }

    fun getSearchListItemBookTitle(activity: SearchActivity, bookTitle: String){
        mFirestore.collection(Constants.PRODUCTS_BOOK)
                .whereGreaterThanOrEqualTo(Constants.TITLE, bookTitle)
                .get()
                .addOnSuccessListener { document->
                    Log.e(activity.javaClass.simpleName, document.documents.toString())

                    //creating an array list to store the book list
                    val booksList: ArrayList<Product> = ArrayList()

                    for(i in document.documents){
                        //making an object of every book
                        val book = i.toObject(Product::class.java)!!
                        //assigning the product id as the document id
                        //as it was not assigned earlier
                        book.product_id  = i.id
                        //adding the book to the book list
                        booksList.add(book)
                    }

                    //on success methode
                    //passing all the book list
                    activity.successGetAllBookList(booksList)
                }
                .addOnFailureListener { e->
                    //hiding the progress bar
                    activity.hideProgressDialog()
                    Log.e(activity.javaClass.simpleName, "Error while displaying all the books", e)
                }
    }

    fun getSearchListItemBookAuthor(activity: SearchActivity, bookAuthor: String){
        mFirestore.collection(Constants.PRODUCTS_BOOK)
                .whereGreaterThanOrEqualTo(Constants.AUTHOR, bookAuthor)
                .get()
                .addOnSuccessListener { document->
                    Log.e(activity.javaClass.simpleName, document.documents.toString())

                    //creating an array list to store the book list
                    val booksList: ArrayList<Product> = ArrayList()

                    for(i in document.documents){
                        //making an object of every book
                        val book = i.toObject(Product::class.java)!!
                        //assigning the product id as the document id
                        //as it was not assigned earlier
                        book.product_id  = i.id
                        //adding the book to the book list
                        booksList.add(book)
                    }

                    //on success methode
                    //passing all the book list
                    activity.successGetAllBookList(booksList)
                }
                .addOnFailureListener { e->
                    //hiding the progress bar
                    activity.hideProgressDialog()
                    Log.e(activity.javaClass.simpleName, "Error while displaying all the books", e)
                }
    }

    fun getSearchListItemBlog(activity: SearchActivity, blog: String){
        mFirestore.collection(Constants.BLOG_REVIEW)
                .whereGreaterThanOrEqualTo(Constants.BLOG_SEARCH, blog)
                .get()
                .addOnSuccessListener { document->
                    Log.e(activity.javaClass.simpleName, document.documents.toString())

                    //creating an array list to store the book list
                    val booksList: ArrayList<BlogReview> = ArrayList()

                    for(i in document.documents){
                        //making an object of every book
                        val book = i.toObject(BlogReview::class.java)!!
                        //assigning the product id as the document id
                        //as it was not assigned earlier
                        book.id  = i.id
                        //adding the book to the book list
                        booksList.add(book)
                    }

                    //on success methode
                    //passing all the book list
                    activity.successGetAllBlogList(booksList)
                }
                .addOnFailureListener { e->
                    //hiding the progress bar
                    activity.hideProgressDialog()
                    Log.e(activity.javaClass.simpleName, "Error while displaying all the books", e)
                }
    }

    fun uploadBlogReviewDetails(activity: AddBlogReviewActivity, blogInfo: BlogReview){
        //product_book is the collection
        //if created once it will not be created again
        mFirestore.collection(Constants.BLOG_REVIEW)
            .document()
            //set the field to merge so that we can add later
            //and now set the entries coming from add product activity
            .set(blogInfo, SetOptions.merge())
            .addOnSuccessListener {
                //calls the function product Book Upload Success
                //to display the toast and hide the progress bar
                activity.successUploadBlogDetails()
            }
            .addOnFailureListener { e->
                //hides the progress bar
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while uploading book info", e)
            }
    }

    fun getMyBlogList(activity: Activity){
        mFirestore.collection(Constants.BLOG_REVIEW)
                //getting the book list where the currently logged in user id matches with the user id given in product_book collection
                .whereEqualTo(Constants.USER_ID, getCurrentUserID())
                .get()
                .addOnSuccessListener { document->
                    //creating an array list to store the book list
                    val bookList: ArrayList<BlogReview> = ArrayList()
                    for(i in document.documents){
                        //making an object of every book
                        val book = i.toObject(BlogReview::class.java)!!
                        //assigning the product id as the document id
                        //as it was not assigned earlier
                        book.id = i.id
                        //adding the book to the book list
                        bookList.add(book)
                    }

                    when(activity){
                        //when product fragment
                        is MyBlogReviewActivity ->{
                            //calling the success methode
                            //which hides the progress bar
                            activity.successGetMyBlogList(bookList)
                        }
                    }
                }
                .addOnFailureListener {  e->
                    when(activity){
                        //when product fragment
                        is MyBlogReviewActivity ->{
                            //hiding the progress bar
                            activity.hideProgressDialog()
                            Log.e(activity.javaClass.simpleName, "Error while loading blog info", e)
                        }
                    }

                }
    }

    fun deleteBlog(activity: MyBlogReviewActivity, productID: String){
        mFirestore.collection(Constants.BLOG_REVIEW)
                .document(productID)
                .delete()
                .addOnSuccessListener {
                    activity.successDeleteBlog()
                }
                .addOnFailureListener{ e->
                    //hiding the progress bar
                    activity.hideProgressDialog()

                    Log.e(activity.javaClass.simpleName,"Error while deleting blog",  e)
                }
    }

    fun updateBlogDetails(activity: Activity, blogId: String, productHashMap: HashMap<String, Any>){
        //updating the user informations
        mFirestore.collection(Constants.BLOG_REVIEW)
            .document(blogId)
            .update(productHashMap)
            .addOnSuccessListener {
                //when profile activity
                when (activity){
                    is EditMyBlogReviewActivity ->{
                        //hiding the progress bar
                        //and starting the main activity
                        activity.successUpdateBlogDetails()
                    }
                }
            }
            .addOnFailureListener{e->
                when (activity){
                    is EditMyBlogReviewActivity ->{
                        //hiding the progress bar
                        activity.hideProgressDialog()
                    }
                }
                Log.e(activity.javaClass.simpleName, "Error while updating blog details", e)
            }
    }

    fun getBlogListItem(activity: BlogReviewActivity){
        mFirestore.collection(Constants.BLOG_REVIEW)
            .get()
            .addOnSuccessListener { document->

                //creating an array list to store the book list
                val booksList: ArrayList<BlogReview> = ArrayList()

                for(i in document.documents){
                    //making an object of every book
                    val book = i.toObject(BlogReview::class.java)!!
                    //assigning the product id as the document id
                    //as it was not assigned earlier
                    book.id  = i.id
                    //adding the book to the book list
                    booksList.add(book)
                }

                //on success methode
                //passing all the book list
                activity.successBlogListItem(booksList)
            }
            .addOnFailureListener { e->
                //hiding the progress bar
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while displaying all the blogs", e)
            }
    }

    fun getBlogDetails(activity: BlogDetailsActivity, productID: String){
        mFirestore.collection(Constants.BLOG_REVIEW)
                .document(productID)
                .get()
                .addOnSuccessListener { document ->
                    //creating a product object from the document
                    val book = document.toObject(BlogReview::class.java)
                    //if the book is not null
                    //then passing the book details to the activity
                    if(book != null){
                        activity.successGetBlogDetails(book)
                    }
                }
                .addOnFailureListener{ e->
                    //hiding the progress bar
                    activity.hideProgressDialog()
                    Log.e(activity.javaClass.simpleName, "Error while getting the blog details.", e)
                }
    }

    fun getLikesCount(activity: BlogDetailsActivity, blogId: String){
        mFirestore.collection(Constants.LIKE)
                .whereEqualTo(Constants.USER_id, blogId)
                .get()
                .addOnSuccessListener{document->
                    val bookList: ArrayList<Likes> = ArrayList()
                    for(i in document.documents){
                        //making an object of every book
                        val book = i.toObject(Likes::class.java)!!
                        //assigning the product id as the document id
                        //as it was not assigned earlier
                        book.id = i.id
                        //adding the book to the book list
                        bookList.add(book)
                    }

                    activity.successGetLikesCount(bookList)
                }
                .addOnFailureListener { e->
                    activity.hideProgressDialog()
                    Log.e(activity.javaClass.simpleName, "Error while getting the blog details.", e)
                }
    }

    fun addLike(activity: BlogDetailsActivity, likeInfo: Likes, id: String){
        //product_book is the collection
        //if created once it will not be created again
        mFirestore.collection(Constants.LIKE)
                .document(id)
                //set the field to merge so that we can add later
                //and now set the entries coming from add product activity
                .set(likeInfo, SetOptions.merge())
                .addOnSuccessListener {
                    //calls the function product Book Upload Success
                    //to display the toast and hide the progress bar
                    activity.successAddLike()
                }
                .addOnFailureListener { e->
                    //hides the progress bar
                    Log.e(activity.javaClass.simpleName, "Error while adding like", e)
                }
    }

    fun deleteLike(activity: BlogDetailsActivity, productID: String){
        mFirestore.collection(Constants.LIKE)
                .document(productID)
                .delete()
                .addOnSuccessListener {
                    activity.successDeleteLike()
                }
                .addOnFailureListener{ e->
                    //hiding the progress bar

                    Log.e(activity.javaClass.simpleName,"Error while deleting like",  e)
                }
    }

    fun getUserBookList(activity: UsersBookActivity, userId: String){
        mFirestore.collection(Constants.PRODUCTS_BOOK)
            //getting the book list where the currently logged in user id matches with the user id given in product_book collection
            .whereEqualTo(Constants.USER_ID, userId)
            .get()
            .addOnSuccessListener { document->
                Log.e("Product Book List", document.documents.toString())
                //creating an array list to store the book list
                val bookList: ArrayList<Product> = ArrayList()
                for(i in document.documents){
                    //making an object of every book
                    val book = i.toObject(Product::class.java)
                    //assigning the product id as the document id
                    //as it was not assigned earlier
                    book!!.product_id = i.id
                    //adding the book to the book list
                    bookList.add(book)
                }
                activity.successGetUserBookList(bookList)
            }
            .addOnFailureListener {  e->
                activity.hideProgressDialog()

                Log.e(activity.javaClass.simpleName, "Error while getting users book", e)
            }
    }

    fun getUserBlogReviewList(activity: UserBlogActivity, userId: String){
        mFirestore.collection(Constants.BLOG_REVIEW)
            .whereEqualTo(Constants.USER_ID, userId)
            .get()
            .addOnSuccessListener{document->
                val blogList: ArrayList<BlogReview> = ArrayList()
                for(i in document.documents){
                    val blog = i.toObject(BlogReview::class.java)!!
                    blog.id = i.id
                    blogList.add(blog)
                }
                activity.successGetUserBlogList(blogList)
            }
            .addOnFailureListener{e->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while getting blog list", e)
            }
    }
}
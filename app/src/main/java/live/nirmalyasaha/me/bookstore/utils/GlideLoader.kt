package live.nirmalyasaha.me.bookstore.utils

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import live.nirmalyasaha.me.bookstore.R
import java.io.IOException

class GlideLoader(val context: Context) {

    //function used to load the image
    fun loadUserImage(image: Any, imageView: ImageView){
        try{

            //boiler plate

            Glide.with(context).load(image).centerCrop().placeholder(R.drawable.ic_user_placeholder).into(imageView)
        }
        catch(e: IOException){
            e.printStackTrace()
        }
    }

    fun loadUserImageString(imageURL: String, imageView: ImageView){
        try{

            //boiler plate

            Glide.with(context).load(imageURL).centerCrop().placeholder(R.drawable.ic_user_placeholder).into(imageView)
        }
        catch(e: IOException){
            e.printStackTrace()
        }
    }

    //function used to load the image
    fun loadBookImage(image: Any, imageView: ImageView){
        try{

            //boiler plate

            Glide.with(context).load(image).centerCrop().into(imageView)
        }
        catch(e: IOException){
            e.printStackTrace()
        }
    }

}
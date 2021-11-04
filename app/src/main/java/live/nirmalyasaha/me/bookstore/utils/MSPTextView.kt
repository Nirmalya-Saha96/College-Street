package live.nirmalyasaha.me.bookstore.utils

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

//it inherites from app compact text view and apply the font
class MSPTextView(context: Context, attrs: AttributeSet): AppCompatTextView(context, attrs) {
    init {
        //call the function to apply the fonts to the components
        applyFont()
    }

    private fun applyFont(){
        //this is used to get the file from the assets folder and set it to the title textView
        val typeface: Typeface =
                Typeface.createFromAsset(context.assets, "Montserrat-Regular.ttf")
        setTypeface(typeface)
    }
}
package live.nirmalyasaha.me.bookstore.activities.ui.activities

import android.graphics.Bitmap
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.android.synthetic.main.activity_generate_q_r_code.*
import live.nirmalyasaha.me.bookstore.R
import live.nirmalyasaha.me.bookstore.utils.Constants

class GenerateQRCodeActivity : BaseActivity() {

    private var mOrderId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generate_q_r_code)

        val actionBar = getSupportActionBar()
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24)
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        if(intent.hasExtra(Constants.EXTRA_ORDER_ID)){
            mOrderId = intent.getStringExtra(Constants.EXTRA_ORDER_ID)!!
        }

        generateQRCode()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun generateQRCode(){
        val data = mOrderId

        if(data.isEmpty()){
            showErrorSnackBar("Order id is empty", true)
        }
        else{
            val writer = QRCodeWriter()
            try{
                val bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, 512, 512)
                val width = bitMatrix.width
                val height = bitMatrix.height
                val bep = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
                for(x in 0 until width){
                    for (y in 0 until height){
                        bep.setPixel(x, y, if(bitMatrix[x,y]) Color.BLACK else Color.WHITE)
                    }
                }
                iv_qr_code.setImageBitmap(bep)
            }
            catch(e: WriterException){
                e.printStackTrace()
            }
        }
    }
}
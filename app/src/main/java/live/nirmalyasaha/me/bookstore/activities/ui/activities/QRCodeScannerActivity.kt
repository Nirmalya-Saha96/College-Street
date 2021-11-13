package live.nirmalyasaha.me.bookstore.activities.ui.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import live.nirmalyasaha.me.bookstore.R
import live.nirmalyasaha.me.bookstore.firestore.FirestoreClass
import live.nirmalyasaha.me.bookstore.utils.Constants
import java.util.HashMap

class QRCodeScannerActivity : BaseActivity() {

    private lateinit var mCodeScanner: CodeScanner
    private var mOrderId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_q_r_code_scanner)

        val actionBar = getSupportActionBar()
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24)
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        if(intent.hasExtra(Constants.EXTRA_ORDER_ID)){
            mOrderId = intent.getStringExtra(Constants.EXTRA_ORDER_ID)!!
        }

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 123)
        }
        else{
            scanne()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onResume() {
        super.onResume()
        if(::mCodeScanner.isInitialized){
            mCodeScanner?.startPreview()
        }
    }

    override fun onPause(){
        if(::mCodeScanner.isInitialized){
            mCodeScanner?.releaseResources()
        }
        super.onPause()
    }

    private fun scanne(){
        val scannerView: CodeScannerView = findViewById(R.id.scanner)
        mCodeScanner = CodeScanner(this, scannerView)
        mCodeScanner.camera = CodeScanner.CAMERA_BACK
        mCodeScanner.formats = CodeScanner.ALL_FORMATS
        mCodeScanner.autoFocusMode = AutoFocusMode.SAFE
        mCodeScanner.scanMode = ScanMode.SINGLE
        mCodeScanner.isAutoFocusEnabled = true
        mCodeScanner.isFlashEnabled = false
        mCodeScanner.decodeCallback = DecodeCallback {
            runOnUiThread {
                if(it.text == mOrderId){
                    showErrorSnackBar("Scan Result: ${it.text}", false)

                    showProgressDialog("Updating Status")

                    //creating the sold book status hash map
                    val soldBookStatusHashMap = HashMap<String, Any>()

                    soldBookStatusHashMap[Constants.STATUS] = Constants.DELIVERED

                    //updating the status in order collection
                    FirestoreClass().updateStatusDelivery(this@QRCodeScannerActivity, mOrderId, soldBookStatusHashMap)
                }
                else{
                    showErrorSnackBar("Not Matched", true)
                }
            }
        }
        mCodeScanner.errorCallback = ErrorCallback {
            runOnUiThread {
                showErrorSnackBar("Error: ${it.message}", true)
            }
        }
        scannerView.setOnClickListener {
            mCodeScanner.startPreview()
        }
    }

    fun successStatusUpdate(){
        hideProgressDialog()

        val intent = Intent(this@QRCodeScannerActivity, DeliverSuccessActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 123){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                showErrorSnackBar("Permissions granted", false)
                scanne()
            }
            else{
                showErrorSnackBar("Permissions denied", true)
            }
        }
    }
}
package live.nirmalyasaha.me.bookstore.activities.ui.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.android.synthetic.main.activity_m_l_image_to_text.*
import kotlinx.android.synthetic.main.activity_profile.*
import live.nirmalyasaha.me.bookstore.R
import live.nirmalyasaha.me.bookstore.utils.Constants

class MLImageToTextActivity : BaseActivity() {

    private var imageBitmap: Bitmap? = null
    private var mCheckSnap: Boolean = false
    private var mCheckExtract: Boolean = false
    private var mExtractedText: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_m_l_image_to_text)

        val actionBar = getSupportActionBar()
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24)
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        button.setOnClickListener {
            dispactImage()
        }

        button2.setOnClickListener {
            if(mCheckSnap == true){
                detextText()
            }
            else{
                showErrorSnackBar("Please take a snap of the image where you want to extract the text", true)
            }
        }

        button3.setOnClickListener {

            mExtractedText = tv_ml_text!!.text.toString().trim() {it<=' '}

            if(mCheckExtract == true){
                val intent = Intent(this@MLImageToTextActivity, SearchActivity::class.java)
                intent.putExtra(Constants.EXTRA_EXTRACTED_TEXT, mExtractedText)
                startActivity(intent)
                finish()
            }
            else{
                showErrorSnackBar("Please extract text from the snap", true)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun dispactImage(){
        val imageIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if(imageIntent.resolveActivity(packageManager) != null){
            startActivityForResult(imageIntent , Constants.REQUEST_IMAGE_CAPTURE)
        }
    }

    private fun detextText(){
        //val  image = FirebaseVisionImage.fromBitmap(imageBitmap!!)
        val image = InputImage.fromBitmap(imageBitmap!!, 0)
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        val result = recognizer.process(image)
            .addOnSuccessListener { visionText ->
                processText(visionText)
            }
            .addOnFailureListener { e ->
                showErrorSnackBar("SomeThink went wrong", false)
            }
    }

    private fun processText(text: Text){
        val blocks = text.textBlocks
        if(blocks.size == 0){
            showErrorSnackBar("No text extracted", true)
            return
        }

        for( block in text.textBlocks){
            val txt = block.getText()
            tv_ml_text!!.textSize = 16f
            tv_ml_text!!.setText(txt)
        }

        mExtractedText = tv_ml_text!!.text.toString().trim() {it<=' '}

        mCheckExtract = true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == Constants.REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK){
            val extras = data!!.extras
            imageBitmap = extras!!.get("data") as Bitmap
            iv_ml_image!!.setImageBitmap(imageBitmap)

            mCheckSnap = true
        }
    }
}
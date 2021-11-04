package live.nirmalyasaha.me.bookstore.activities.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_order_success.*
import live.nirmalyasaha.me.bookstore.R

class OrderSuccessActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_success)

        //click functionality
        btnOk.setOnClickListener {
            val intent = Intent(this@OrderSuccessActivity, DashboardActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
package live.nirmalyasaha.me.bookstore.activities.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_deliver_success.*
import kotlinx.android.synthetic.main.activity_order_success.*
import kotlinx.android.synthetic.main.activity_order_success.btnOk
import live.nirmalyasaha.me.bookstore.R

class DeliverSuccessActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deliver_success)

        btn_Ok.setOnClickListener {
            val intent = Intent(this@DeliverSuccessActivity, DashboardActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
package live.nirmalyasaha.me.bookstore.activities.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.auth.FirebaseAuth
import live.nirmalyasaha.me.bookstore.R
import live.nirmalyasaha.me.bookstore.activities.ui.activities.BaseActivity

class DashboardActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

            //setting up the app bar background color
            //to all fragments
            supportActionBar!!.setBackgroundDrawable(ContextCompat.getDrawable(this@DashboardActivity, R.drawable.button_background))

            val navController = findNavController(R.id.nav_host_fragment)
            // Passing each menu ID as a set of Ids because each
            // menu should be considered as top level destinations.
            val appBarConfiguration = AppBarConfiguration(setOf(
                R.id.navigation_dashboard, R.id.navigation_product, R.id.navigation_wishlist, R.id.navigation_order, R.id.navigation_sold_books))
            setupActionBarWithNavController(navController, appBarConfiguration)
            navView.setupWithNavController(navController)

    }

    //function used to exit the application on double backed pressed
    //main center for fragments
    override fun onBackPressed() {
        doubleBackPressedExit()
    }
}
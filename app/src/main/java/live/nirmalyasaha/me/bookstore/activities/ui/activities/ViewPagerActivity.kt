package live.nirmalyasaha.me.bookstore.activities.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import kotlinx.android.synthetic.main.activity_view_pager.*
import live.nirmalyasaha.me.bookstore.R
import live.nirmalyasaha.me.bookstore.activities.ui.adapter.ViewPagerAdapter
import me.relex.circleindicator.CircleIndicator3

class ViewPagerActivity : AppCompatActivity() {

    private var titleList = mutableListOf<String>()
    private var descList = mutableListOf<String>()
    private var imageList = mutableListOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_pager)

        dummyAddToViewPager()

        view_pager.adapter = ViewPagerAdapter(this, titleList, descList, imageList)
        view_pager.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        val indicator = findViewById<CircleIndicator3>(R.id.indicator)
        indicator.setViewPager(view_pager)
    }

    private fun addToList(title: String, desc: String, image: Int){
        titleList.add(title)
        descList.add(desc)
        imageList.add(image)
    }

    private fun dummyAddToViewPager(){
        addToList("College Street", "A B-2-B market place for second-hand books", R.drawable.college_street)
        addToList("Advance Features", "Buying and selling of books, Book Review/Blogs \n with Like and Unlike, add, update ,delete, review items/comments, share \n and wishlist, cart, order cancel, user profile, delivery status update support", R.drawable.advancedfeatures)
        addToList("Secured Environment", "Personal details like address are secured with Cryptography AES technology", R.drawable.secured)
        addToList("End-To-End Encryption", "Personalised chat functionality with end-to-end encrypted \n and Trained Bot facilities", R.drawable.encryption)
        addToList("Search Engine Ml", "Advance search engine with filters and Machine Learning Image-To-Text Processing \n to search by taking a snap of any book", R.drawable.search_view_pager)
        addToList("Google Maps", "Get the exact location from the buyer's address in maps", R.drawable.google_maps)
        addToList("Advance UI/UX", "Advance ui-ux, with auto delete & update as any item gets removed", R.drawable.advance_ui_ux)
    }
}
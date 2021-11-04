package live.nirmalyasaha.me.bookstore.activities.ui.fragment

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.dialogue_progress.*
import live.nirmalyasaha.me.bookstore.R

open class BaseFragment : Fragment() {

    private lateinit var mProgressDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_base, container, false)
    }

    //function to show progress dialogue bar
    fun showProgressDialog(text: String) {
        mProgressDialog = Dialog(requireActivity())

        /*Set the screen content from a layout resource.
        The resource will be inflated, adding all top-level views to the screen.*/
        //basically add the xml file
        mProgressDialog.setContentView(R.layout.dialogue_progress)

        //getting the text
        //or changing the text
        mProgressDialog.tv_progress_text.text = text

        //used for not having any next or back press
        //boiler plate
        mProgressDialog.setCancelable(false)
        mProgressDialog.setCanceledOnTouchOutside(false)

        //Start the dialog and display it on screen.
        mProgressDialog.show()
    }

    //function used to hide progress diagolue
    fun hideProgressDialog() {
        mProgressDialog.dismiss()
    }
}
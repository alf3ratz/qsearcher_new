package course.ru.qsearcher.activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import course.ru.qsearcher.R
import kotlinx.android.synthetic.main.dialog_for_categories.view.*

class CustomDialog(act:SearchActivity) : DialogFragment() {
    var categories: ArrayList<String> = ArrayList()
    var checker = false
    var activity = act
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        categories = arrayListOf()
        val root: View = inflater.inflate(R.layout.dialog_for_categories, container, false)
        root.acceptButton.setOnClickListener {
            if (root.exhibitionBox.isChecked) {
                categories.add("exhibition")
            }
            if (root.entertainmentBox.isChecked) {
                categories.add("entertainment")
            }
            if (root.educationBox.isChecked) {
                categories.add("education")
            }
            if (root.concertBox.isChecked) {
                categories.add("concert")
            }
            activity.eventsWithSelectedCategories(categories)
            checker = true
            dismiss()
        }
        return root
        //return super.onCreateView(inflater, container, savedInstanceState)
    }

}
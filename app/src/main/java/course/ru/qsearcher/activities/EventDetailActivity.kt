package course.ru.qsearcher.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import course.ru.qsearcher.R
import course.ru.qsearcher.adapters.ImageSliderAdapter
import course.ru.qsearcher.databinding.FragmentEventDetailBinding
import course.ru.qsearcher.databinding.ActivityEventDetailBinding
import course.ru.qsearcher.responses.EventResponse
import course.ru.qsearcher.viewmodels.MostPopularEventsViewModel

class EventDetailActivity : AppCompatActivity() {
    private var eventViewModel: MostPopularEventsViewModel? = null;

    // private var eventDetailFragmentBinding: FragmentEventDetailBinding? = null
    private var eventDetailActivityBinding: ActivityEventDetailBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        eventDetailActivityBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_event_detail);
        doInitialization(savedInstanceState)
    }

    private fun doInitialization(savedInstanceState: Bundle?) {
        eventViewModel = ViewModelProvider(this).get(MostPopularEventsViewModel::class.java);

        getEvents(savedInstanceState)
    }

    private fun getEvents(savedInstanceState: Bundle?) {
        eventDetailActivityBinding?.isLoading = true;
        var eventId: Int = intent.getIntExtra("id", -1);//getStringExtra("id");
        var title = intent.getStringExtra("title");
        var images = intent.getStringArrayListExtra("images")
        eventViewModel?.getMostPopularEvents(34)
            ?.observe(this, Observer { eventResponse: EventResponse? ->
                run {
                    eventDetailActivityBinding?.isLoading = false;
                    images?.let { loadImageSlider(it) }
//                    if (eventResponse?.events?.get(0) != null) {
//                        Log.i("", "первый ивент не равен налл")
//                        if (eventResponse?.events?.get(0)?.images != null) {
//                            Log.i("", "картинки первого ивента не равны налл")
////                            var temp: Array<String> =
////                                emptyArray<String>()//Array<String>(eventResponse?.events?.get(0)?.images!!.size)
////                            for (elem in eventResponse?.events?.get(0)?.images!!) {
////                                temp += elem.toString()
////                            }
//                            loadImageSlider(eventResponse?.events?.get(0)?.images!!)
//                            //if(eventResponse.getEventDetails().getPictures!=null)
//                        }
//                    }else{
//                        Log.i("", "первый ивент  равен налл")
//                    }
                }
            }
            );
    }

    private fun loadImageSlider(sliderImages: ArrayList<String>) {
        eventDetailActivityBinding?.sliderViewPager?.offscreenPageLimit = 1
        eventDetailActivityBinding?.sliderViewPager?.adapter = ImageSliderAdapter(sliderImages)
        eventDetailActivityBinding?.sliderViewPager?.visibility = View.VISIBLE
        eventDetailActivityBinding?.viewFadingEdge?.visibility = View.VISIBLE
    }
}
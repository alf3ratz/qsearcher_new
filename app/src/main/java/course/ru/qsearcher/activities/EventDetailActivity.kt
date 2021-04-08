package course.ru.qsearcher.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
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
        eventDetailActivityBinding?.imageBack?.setOnClickListener { onBackPressed() }
        getEvents(savedInstanceState)
    }

    private fun getEvents(savedInstanceState: Bundle?) {
        eventDetailActivityBinding?.isLoading = true;
        var eventId: Int = intent.getIntExtra("id", 1);//getStringExtra("id");
        var title = intent.getStringExtra("title");
        val images_temp = intent.getStringArrayListExtra("images")
        var images :ArrayList<String> = ArrayList(  )
        for (i in 1 until images_temp!!.size){
            images.plusAssign(images_temp[i])
        }
        eventDetailActivityBinding?.eventImageURL = images_temp[0];
        eventDetailActivityBinding?.imageEvent!!.visibility = View.VISIBLE
        eventViewModel?.getMostPopularEvents(34)
            ?.observe(this, Observer { eventResponse: EventResponse? ->
                run {
                    eventDetailActivityBinding?.isLoading = false;
                    if(images.size==0)
                        images = images_temp
                    loadImageSlider(images)
                }
            }
            );
    }

    private fun loadImageSlider(sliderImages: ArrayList<String>) {
        eventDetailActivityBinding?.sliderViewPager?.offscreenPageLimit = 1
        eventDetailActivityBinding?.sliderViewPager?.adapter = ImageSliderAdapter(sliderImages)
        eventDetailActivityBinding?.sliderViewPager?.visibility = View.VISIBLE
        eventDetailActivityBinding?.viewFadingEdge?.visibility = View.VISIBLE
        Log.i("картинка", "размер слайдер имеджс" + sliderImages.size)
        setupSliderIndicators(sliderImages.size)
        Log.i("картинка", "вышел из сетап слайдер индикаторс")
        eventDetailActivityBinding?.sliderViewPager?.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                Log.i("картинка", "колбек, позиция" + position)
                super.onPageSelected(position)
                setCurrentSliderIndicator(position)
            }
        })
    }

    private fun setupSliderIndicators(count: Int) {
        Log.i("картинка", "вошел в сетап слайдер индикаторс, count= " + count.toString())
        val indicators: MutableList<ImageView> = mutableListOf<ImageView>().toMutableList()
        for (i in 0..count - 1) {
            indicators += ImageView(applicationContext)
        }
        var layoutParams: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(8, 0, 8, 0)
        Log.i("картинка", "размер списка индикаторов " + indicators.size)

        for (i in 0..(count - 1)) {
            Log.i("картинка", "ставит индикатор")
            indicators[i] = ImageView(applicationContext)
            indicators[i].setImageDrawable(
                ContextCompat.getDrawable(
                    applicationContext, R.drawable.background_slider_indicator_inactive
                )
            )
            indicators[i].layoutParams = layoutParams
            eventDetailActivityBinding?.layoutSliderIndicators?.addView(indicators[i]);
        }
        eventDetailActivityBinding?.layoutSliderIndicators?.visibility = View.VISIBLE;
        setCurrentSliderIndicator(0);
        //Log.i("картинка", "сет курент индикатор")
    }

    private fun setCurrentSliderIndicator(position: Int) {
        val childCount: Int? = eventDetailActivityBinding?.layoutSliderIndicators?.childCount
        //if (childCount == 0)
        Log.i("картинка", "Кол-во детей: " + childCount.toString())
        for (i in 0..childCount!! - 1) {
            var imageView: ImageView =
                eventDetailActivityBinding?.layoutSliderIndicators?.getChildAt(i) as ImageView
            if (i == position) {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.background_slider_indicator_active
                    )
                )
                Log.i("картинка", "должен поменять1")
            } else {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.background_slider_indicator_inactive
                    )
                )
                Log.i("картинка", "должен поменять1")
            }

        }
    }
}

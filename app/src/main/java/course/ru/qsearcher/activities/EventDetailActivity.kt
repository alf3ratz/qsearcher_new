package course.ru.qsearcher.activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import course.ru.qsearcher.R
import course.ru.qsearcher.adapters.ImageSliderAdapter
import course.ru.qsearcher.databinding.FragmentEventDetailBinding
import course.ru.qsearcher.databinding.ActivityEventDetailBinding
import course.ru.qsearcher.model.Event
import course.ru.qsearcher.responses.EventResponse
import course.ru.qsearcher.viewmodels.MostPopularEventsViewModel
import java.util.*
import kotlin.collections.ArrayList

class EventDetailActivity : AppCompatActivity() {
    private var eventViewModel: MostPopularEventsViewModel? = null;
    private var eventDetailActivityBinding: ActivityEventDetailBinding? = null
    private lateinit var event: Event

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        eventDetailActivityBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_event_detail);
        doInitialization(savedInstanceState)
    }

    private fun doInitialization(savedInstanceState: Bundle?) {
        eventViewModel = ViewModelProvider(this).get(MostPopularEventsViewModel::class.java);
        eventDetailActivityBinding?.imageBack?.setOnClickListener { onBackPressed() }
        event = intent.getSerializableExtra("event") as Event;
        getEvents(savedInstanceState)
    }

    private fun getEvents(savedInstanceState: Bundle?) {
        eventDetailActivityBinding?.isLoading = true;
//        var eventId: Int = intent.getIntExtra("id", 1);
//        val images_temp = intent.getStringArrayListExtra("images")

        var eventId: Int = event.id
        val images_temp: ArrayList<String>? = event.imagesAsString
        var images: ArrayList<String> = ArrayList()

        for (i in 1 until images_temp!!.size) {
            images.plusAssign(images_temp[i])
        }
        eventDetailActivityBinding?.eventImageURL = images_temp[0];
        eventDetailActivityBinding?.imageEvent!!.visibility = View.VISIBLE
        eventDetailActivityBinding?.eventDescription = HtmlCompat.fromHtml(
            /*intent.getStringExtra("bodyText")*/
            event.bodyText.toString(), HtmlCompat.FROM_HTML_MODE_LEGACY
        ).toString()
        eventDetailActivityBinding?.rating = event.rating//intent.getStringExtra("rating")!!
        eventDetailActivityBinding?.textReadMore?.visibility = View.VISIBLE
        eventDetailActivityBinding?.textReadMore?.setOnClickListener {
            if (eventDetailActivityBinding?.textReadMore?.text == getString(R.string.read_more)) {
                eventDetailActivityBinding?.textDescription?.maxLines = Integer.MAX_VALUE
                eventDetailActivityBinding?.textDescription?.ellipsize = null
                eventDetailActivityBinding?.textReadMore?.text = getString(R.string.read_less)
            } else {
                eventDetailActivityBinding?.textDescription?.maxLines = 4
                eventDetailActivityBinding?.textDescription?.ellipsize =
                    TextUtils.TruncateAt.END
                eventDetailActivityBinding?.textReadMore?.setText(R.string.read_more)//text = R.string.read_more.toString()
            }
        }
        eventViewModel?.getMostPopularEvents(34)
            ?.observe(this, Observer { eventResponse: EventResponse? ->
                run {
                    eventDetailActivityBinding?.isLoading = false;
                    if (images.size == 0)
                        images = images_temp
                    loadImageSlider(images)
                }
            }
            )
        eventDetailActivityBinding?.viewDriver1?.visibility = View.VISIBLE
        eventDetailActivityBinding?.layoutMisc?.visibility = View.VISIBLE
        eventDetailActivityBinding?.viewDriver2?.visibility = View.VISIBLE
        eventDetailActivityBinding?.textRating?.visibility = View.VISIBLE
        eventDetailActivityBinding?.textDescription?.visibility = View.VISIBLE
        eventDetailActivityBinding?.buttonWebsite?.visibility = View.VISIBLE
        eventDetailActivityBinding?.buttonWebsite?.setOnClickListener {
            val intentInner: Intent = Intent(Intent.ACTION_VIEW)
            intentInner.data = Uri.parse(event.siteUrl/*intent.getStringExtra("siteUrl")*/)
            startActivity(intentInner)
        }
        loadBasicEventDetails()
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

    private fun loadBasicEventDetails() {
        eventDetailActivityBinding?.eventName = event.name//intent.getStringExtra("title")
        eventDetailActivityBinding?.eventShortName = event.shortTitle//intent.getStringExtra("shortTitle")
        eventDetailActivityBinding?.textName?.visibility = View.VISIBLE
        eventDetailActivityBinding?.textShortName?.visibility = View.VISIBLE
    }
}

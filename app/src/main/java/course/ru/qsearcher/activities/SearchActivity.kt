package course.ru.qsearcher.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import course.ru.qsearcher.R
import course.ru.qsearcher.adapters.EventsAdapter
import course.ru.qsearcher.databinding.ActivitySearchBinding
import course.ru.qsearcher.listeners.EventListener
import course.ru.qsearcher.model.Event
import course.ru.qsearcher.responses.EventResponse
import course.ru.qsearcher.viewmodels.EventsViewModel
import java.util.*
import kotlin.collections.ArrayList


class SearchActivity : AppCompatActivity(), EventListener {
    private var activitySearchBinding: ActivitySearchBinding? = null
    private lateinit var viewModel: EventsViewModel
    private var events: ArrayList<Event> = ArrayList()
    private var eventsAdapter: EventsAdapter? = null
    private var timer: Timer? = null
    private var categories: ArrayList<String> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activitySearchBinding = DataBindingUtil.setContentView(this, R.layout.activity_search)
        initialize()
    }


    fun eventsWithSelectedCategories(categories: ArrayList<String>) {
        val query = categories.joinToString().replace(" ", "")
        viewModel.eventsWithSelectedCategories(query).observe(this, { t: EventResponse? ->
            if (t != null) {
                if (t.events != null) {
                    val oldCount: Int = events.size
                    for (elem in t.events!!) {
                        elem.name = elem.name!![0].toUpperCase() + elem.name!!.substring(
                            1,
                            elem.name!!.length
                        )
                    }
                    events.addAll(t.events!!)
                    eventsAdapter?.notifyDataSetChanged()
                    eventsAdapter?.notifyItemRangeChanged(
                        oldCount,
                        events.size / 1000
                    )
                } else {
                    Toast.makeText(
                        applicationContext,
                        "Не удалось загрузить события",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
    }

    private fun initialize() {
        activitySearchBinding?.imageBack?.setOnClickListener { onBackPressed() }
        activitySearchBinding?.categoriesButton?.setOnClickListener {
            val dialog = CustomDialog(this)
            dialog.show(supportFragmentManager, "customDialog")
            categories = dialog.categories
            if (categories.size > 0)
                eventsWithSelectedCategories(categories)
        }
        activitySearchBinding?.imageSearch?.setOnClickListener {
            if (categories.size > 0) {
                eventsWithSelectedCategories(categories)
            } else {
                searchEvent(activitySearchBinding?.inputSearch?.text?.trim().toString())
            }
        }

        activitySearchBinding?.eventsRecyclerView?.setHasFixedSize(true)
        viewModel =
            ViewModelProvider(this).get(EventsViewModel::class.java)
        eventsAdapter = EventsAdapter(events, this)
        activitySearchBinding?.apply {
            activitySearchBinding?.eventsRecyclerView?.adapter = eventsAdapter
            invalidateAll()
        }
        activitySearchBinding?.inputSearch?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (timer != null)
                    timer?.cancel()
            }

            @SuppressLint("DefaultLocale")
            override fun afterTextChanged(p0: Editable?) {
                if (p0.toString().trim().isNotEmpty()) {
                    timer = Timer()
                    timer?.schedule(object : TimerTask() {
                        override fun run() {
                            Handler(Looper.getMainLooper()).post {
                                var str = p0.toString()
                                str = str.toLowerCase()

                                searchEvent(str)
                            }
                        }
                    }, 800)
                } else {
                    eventsAdapter?.notifyDataSetChanged()
                }
            }
        })
        activitySearchBinding?.eventsRecyclerView?.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
            }
        })
        activitySearchBinding?.inputSearch?.requestFocus()
    }

    @SuppressLint("DefaultLocale")
    private fun searchEvent(query: String) {
        val oldCount = events.size
        for (event in MainActivity.staticEvents) {
            if (event.name!!.toLowerCase().contains(query)) {
                if (!events.contains(event))
                    events.add(event)
            }
        }
        eventsAdapter?.notifyDataSetChanged()
        eventsAdapter?.notifyItemRangeChanged(oldCount, events.size)
    }


    override fun onEventClicked(event: Event) {
        val intent = Intent(applicationContext, EventDetailActivity::class.java)
        val images: ArrayList<String> = arrayListOf()
        for (elem in event.images!!) {
            images.plusAssign(elem.toString())
        }
        event.imagesAsString = images
        intent.putExtra("event", event)
        startActivity(intent)
    }
}
package course.ru.qsearcher.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
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
    private lateinit var viewModel: EventsViewModel//SearchViewModel? = null
    private var events: ArrayList<Event> = ArrayList<Event>();
    private var eventsAdapter: EventsAdapter? = null
    private var currentPage: Int = 1;
    private var totalAvailablePages: Int = 1;
    private var timer: Timer? = null
    var categories: ArrayList<String> = ArrayList()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activitySearchBinding = DataBindingUtil.setContentView(this, R.layout.activity_search)
        doInitialization()
    }


     fun eventsWithSelectedCategories(categories: ArrayList<String>) {
        val query = categories.joinToString().replace(" ", "")
        viewModel.eventsWithSelectedCategories(query).observe(this, Observer { t: EventResponse? ->
            //toggleLoading()
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

    private fun doInitialization() {
        activitySearchBinding?.imageBack?.setOnClickListener { onBackPressed() }
        activitySearchBinding?.categoriesButton?.setOnClickListener {
            val dialog = CustomDialog(this)
            dialog.show(supportFragmentManager, "customDialog")
            categories = dialog.categories
            if (categories.size > 0)
                eventsWithSelectedCategories(categories)
        }
        activitySearchBinding?.imageSearch?.setOnClickListener {
            Log.i(
                "searchAct",
                "нажал" + activitySearchBinding?.inputSearch?.text?.trim().toString()
            )
            if (categories.size > 0) {
                eventsWithSelectedCategories(categories)
            } else {
                searchEvent(activitySearchBinding?.inputSearch?.text?.trim().toString())
            }
        }

        activitySearchBinding?.eventsRecyclerView?.setHasFixedSize(true)
        viewModel =
            ViewModelProvider(this).get(/*SearchViewModel::class.java*/EventsViewModel::class.java)
        //events = MainActivity.staticEvents.slice((1..6)) as ArrayList<Event>
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
//                var str = p0.toString()
//                searchEvent(str.toLowerCase())
            }

            override fun afterTextChanged(p0: Editable?) {
                if (p0.toString().trim().isNotEmpty()) {
                    var str = p0.toString()
                    str = str.toLowerCase()
                    searchEvent(str)
                    timer = Timer()
                    timer?.schedule(object : TimerTask() {
                        override fun run() {
                            Handler(Looper.getMainLooper()).post {
                                currentPage = 1;
                                totalAvailablePages = 1
                                var str = p0.toString()
                                str = str.toLowerCase()

                                searchEvent(str)
                            }
                        }
                    }, 800)
                } else {
                    Log.i("searchAct", "очистил")
                    //events.clear()
                    eventsAdapter?.notifyDataSetChanged()
                }
            }
        })
        activitySearchBinding?.eventsRecyclerView?.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!activitySearchBinding?.eventsRecyclerView?.canScrollVertically(1)!!) {
                    if (activitySearchBinding?.inputSearch?.text.toString()
                            .isNotEmpty() && activitySearchBinding?.inputSearch?.text.toString()
                            .isNotBlank()
                    ) {
//                        searchEvent(
//                            activitySearchBinding?.inputSearch?.text.toString().trim().toLowerCase()
//                        )
//                        activitySearchBinding?.inputSearch?.setText("")
                    }
                }
            }
        })
        activitySearchBinding?.inputSearch?.requestFocus()
        //toggleLoading()

    }

    private fun searchEvent(query: String) {
        //toggleLoading()
        val oldCount = events.size
        var eventsTemp = ArrayList<Event>()
        for (event in MainActivity.staticEvents) {
            if (event.name!!.toLowerCase().contains(query)) {
                if (!events.contains(event))
                    events.add(event)
            }
        }
        //events = eventsTemp
        eventsAdapter?.notifyDataSetChanged()
        eventsAdapter?.notifyItemRangeChanged(oldCount, events.size)
//        viewModel?.searchEvent(query)?.observe(this, Observer {
//            if (it != null) {
////                totalAvailablePages = it.totalPages!!.toInt()
//                if (it.events != null) {
//                    val oldCount: Int = events.size
//                    events.addAll(it.events!!)
//                    eventsAdapter?.notifyItemRangeChanged(oldCount, events.size)
//                }
//            }
//        })
    }

    private fun toggleLoading() {
        if (currentPage == 1) {
            activitySearchBinding?.isLoading =
                !(activitySearchBinding?.isLoading != null && activitySearchBinding?.isLoading!!)
        } else {
            activitySearchBinding?.isLoadingMore =
                !(activitySearchBinding?.isLoadingMore != null && activitySearchBinding?.isLoadingMore!!)
        }
    }

    override fun onEventClicked(event: Event) {
        val intent = Intent(applicationContext, EventDetailActivity::class.java)
        val images: ArrayList<String> = arrayListOf<String>()
        for (elem in event.images!!) {
            images.plusAssign(elem.toString())
        }
        event.imagesAsString = images
        intent.putExtra("event", event)
        startActivity(intent)
    }
}
package course.ru.qsearcher.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import course.ru.qsearcher.DataBinderMapperImpl
import course.ru.qsearcher.R
import course.ru.qsearcher.adapters.EventsAdapter
import course.ru.qsearcher.databinding.ActivitySearchBinding
import course.ru.qsearcher.listeners.EventListener
import course.ru.qsearcher.model.Event
import course.ru.qsearcher.viewmodels.SearchViewModel
import java.util.*
import kotlin.collections.ArrayList

class SearchActivity : AppCompatActivity(), EventListener {
    private var activitySearchBinding: ActivitySearchBinding? = null
    private var viewModel: SearchViewModel? = null
    private var events: ArrayList<Event> = ArrayList<Event>();
    private var eventsAdapter: EventsAdapter? = null
    private var currentPage: Int = 1;
    private var totalAvailablePages: Int = 1;
    private var timer: Timer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        activitySearchBinding = DataBindingUtil.setContentView(this, R.layout.activity_search)
        doInitialization()
    }


    private fun doInitialization() {
        activitySearchBinding?.imageBack?.setOnClickListener { onBackPressed() }
        activitySearchBinding?.eventsRecyclerView?.setHasFixedSize(true)
        viewModel = ViewModelProvider(this).get(SearchViewModel::class.java)
        eventsAdapter = EventsAdapter(events, this)
        activitySearchBinding?.eventsRecyclerView?.adapter = eventsAdapter
        activitySearchBinding?.inputSearch?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //TODO("Not yet implemented")
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (timer != null)
                    timer?.cancel()
            }

            override fun afterTextChanged(p0: Editable?) {
                if (p0.toString().trim().isNotEmpty()) {
                    timer = Timer()
                    timer?.schedule(object : TimerTask() {
                        override fun run() {
                            Handler(Looper.getMainLooper()).post {
                                currentPage = 1;
                                totalAvailablePages = 1
                                var str = p0.toString()
                                str = str.replace(" ","%20")
                                searchEvent(str)
                            }
                        }

                    },800)
                }else{
                    events.clear()
                    eventsAdapter?.notifyDataSetChanged()
                }
            }

        })
        activitySearchBinding?.eventsRecyclerView?.addOnScrollListener(object: RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if(!activitySearchBinding?.eventsRecyclerView?.canScrollVertically(1)!!){
                        if(activitySearchBinding?.inputSearch?.text.toString().isEmpty()){
                            if(currentPage<totalAvailablePages){
                                currentPage+=1
                                searchEvent(activitySearchBinding?.inputSearch?.text.toString().replace(" ","%20"))
                            }
                        }
                }
            }
        })
        activitySearchBinding?.inputSearch?.requestFocus()
        //toggleLoading()

    }

    private fun searchEvent(query: String) {
        toggleLoading()
        viewModel?.searchEvent(query)?.observe(this, Observer {
            if (it != null) {
                //totalAvailablePages = it.totalPages!!.toInt()
                if (it.events != null) {
                    val oldCount: Int = events.size
                    events.addAll(it.events!!)
                    eventsAdapter?.notifyItemRangeChanged(oldCount, events.size)
                }
            }
        })
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
        intent.putExtra("event", event)
        startActivity(intent)
    }
}
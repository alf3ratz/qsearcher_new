package course.ru.qsearcher.activities

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import course.ru.qsearcher.EventsAdapter
import course.ru.qsearcher.R
import course.ru.qsearcher.databinding.ActivityMainBinding
import course.ru.qsearcher.model.Event
import course.ru.qsearcher.responses.EventResponse
import course.ru.qsearcher.viewmodels.MostPopularEventsViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: MostPopularEventsViewModel
    private lateinit var activityMainBinding: ActivityMainBinding

    private var events: ArrayList<Event> = ArrayList()
    private lateinit var eventsAdapter: EventsAdapter
    private var currentPage: Int = 1;
    private var totalAvailablePages: Int = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        doInitialization()
    }

    private fun doInitialization() {
        activityMainBinding?.eventsRecyclerView?.setHasFixedSize(true)
        viewModel = ViewModelProvider(this).get(MostPopularEventsViewModel::class.java)
        var activity: MostPopularEventsViewModel

        eventsAdapter = EventsAdapter(events)
        activityMainBinding.apply {
            activityMainBinding.eventsRecyclerView.adapter = eventsAdapter
            invalidateAll()
        }

        activityMainBinding.eventsRecyclerView.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!activityMainBinding.eventsRecyclerView.canScrollVertically(1)) {
                    if (currentPage <= totalAvailablePages) {
                        currentPage++;
                        getMostPopularEvents()
                    }
                }
            }
        })
        getMostPopularEvents()
    }

    @SuppressLint("ShowToast")
    private fun getMostPopularEvents() {
        toggleLoading()
        //var temp: ArrayList<Event> = ArrayList()
        viewModel.getMostPopularEvents(currentPage).observe(this, Observer { t: EventResponse? ->
            toggleLoading()
            Log.i("response", "вошел в лямбду")
            if (t != null) {
                totalAvailablePages = t.page!!
                Log.i("response", "если респонс не налл")
                if (t.events != null) {
                    val oldCount: Int = events.size
                    Log.i("response", "если список событий не налл")
                    events.addAll(t.events!!)
                    eventsAdapter.notifyDataSetChanged()
                    //eventsAdapter.notifyItemRangeChanged(oldCount, events.size / 1000)//проблема с выводом - показывает после выхода из экрана
                } else {
                    Toast.makeText(applicationContext, "Smth went wrong", Toast.LENGTH_SHORT)
                    Log.i("response", "список событий  налл")
                }
            }
            Toast.makeText(applicationContext, "page = ${t?.totalPages}", Toast.LENGTH_SHORT).show()
        })
//        viewModel.getMostPopularEvents(0).observe(this, Observer { eventsResponse: EventResponse ->
//            Toast.makeText(
//                applicationContext,
//                "Total pages: ${eventsResponse.totalPages}",
//                Toast.LENGTH_SHORT
//            )
//                .show()
//        })
    }

    private fun toggleLoading() {
        if (currentPage == 1) {
            activityMainBinding.isLoading =
                !(activityMainBinding.isLoading != null && activityMainBinding.isLoading!!)
        } else {
            activityMainBinding.isLoadingMore =
                !(activityMainBinding.isLoadingMore != null && activityMainBinding.isLoadingMore!!)
        }
    }
}


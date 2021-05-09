package course.ru.qsearcher.activities

import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.firebase.database.*
import course.ru.qsearcher.R
import course.ru.qsearcher.adapters.EventsAdapter
import course.ru.qsearcher.databinding.ActivityMainBinding
import course.ru.qsearcher.listeners.EventListener
import course.ru.qsearcher.model.Event
import course.ru.qsearcher.responses.EventResponse
import course.ru.qsearcher.viewmodels.EventsViewModel
import kotlinx.android.synthetic.main.activity_map.*


class MainActivity : AppCompatActivity(), EventListener {
    private lateinit var viewModel: EventsViewModel
    private lateinit var activityMainBinding: ActivityMainBinding
    //ActivityMainBinding


    var events: ArrayList<Event> = ArrayList()
    private lateinit var eventsAdapter: EventsAdapter
    private var currentPage: Int = 1
    private var totalAvailablePages: Int = 1

    companion object {
        lateinit var staticEvents: ArrayList<Event>
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        initialize()
    }


    private fun initialize() {
        activityMainBinding.eventsRecyclerView.setHasFixedSize(true)
        viewModel = ViewModelProvider(this).get(EventsViewModel::class.javaObjectType)
        //var activity: EventsViewModel
        setBottomNavigation()

        eventsAdapter = EventsAdapter(events, this)
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
                        currentPage++
                        getMostPopularEvents()
                    }
                }
            }
        })
        activityMainBinding.imageFavourites.setOnClickListener {
            startActivity(
                Intent(
                    applicationContext,
                    FavoritesActivity::class.java
                )
            )
        }
        activityMainBinding.imageSearch.setOnClickListener {
            startActivity(Intent(applicationContext, SearchActivity::class.java))
        }
        activityMainBinding.imageSign.setOnClickListener {
            startActivity(Intent(applicationContext, UsersActivity::class.java))
        }

        activityMainBinding.imageChat.setOnClickListener {
            startActivity(Intent(applicationContext, ChatActivity::class.java))
        }

        getMostPopularEvents()
    }

    private fun setBottomNavigation() {
        val menuView = activityMainBinding.bottomNavigation
            .getChildAt(0) as BottomNavigationMenuView
        for (i in 0 until menuView.childCount) {
            val iconView =
                menuView.getChildAt(i).findViewById<View>(R.id.icon)
            val layoutParams = iconView.layoutParams
            val displayMetrics = resources.displayMetrics
            layoutParams.height =
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 26f, displayMetrics).toInt()
            layoutParams.width =
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 26f, displayMetrics).toInt()
            iconView.layoutParams = layoutParams
        }
        activityMainBinding.bottomNavigation.selectedItemId = R.id.home
        activityMainBinding.bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.favorites -> {
                    startActivity(
                        Intent(
                            applicationContext,
                            FavoritesActivity::class.java
                        )
                    )
                    overridePendingTransition(0, 0)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.chat -> {
                    startActivity(Intent(applicationContext, UsersActivity::class.java))
                    overridePendingTransition(0, 0)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.settings -> {
                    startActivity(Intent(applicationContext, SettingsActivity::class.java))
                    overridePendingTransition(0, 0)
                    return@setOnNavigationItemSelectedListener true
                }

                R.id.map -> {
                    startActivity(Intent(applicationContext, MapActivity::class.java))
                    overridePendingTransition(0, 0)
                    return@setOnNavigationItemSelectedListener true
                }
            }
            false
        }
    }


    private fun getMostPopularEvents() {
        toggleLoading()
        viewModel.getMostPopularEvents(currentPage).observe(this, { t: EventResponse? ->
            toggleLoading()
            if (t != null) {
                totalAvailablePages = t.page!!
                if (t.events != null) {
                    val oldCount: Int = events.size
                    for (elem in t.events!!) {
                        elem.name = elem.name!![0].toUpperCase() + elem.name!!.substring(
                            1,
                            elem.name!!.length
                        )
                    }
                    events.addAll(t.events!!)
                    eventsAdapter.notifyDataSetChanged()
                    eventsAdapter.notifyItemRangeChanged(
                        oldCount,
                        events.size / 1000
                    )
                } else {
                    Toast.makeText(applicationContext, "Smth went wrong", Toast.LENGTH_SHORT).show()
                }
            }
        })
        staticEvents = events
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

    override fun onEventClicked(event: Event) {
        val images: ArrayList<String> = arrayListOf()
        for (elem in event.images!!) {
            images.plusAssign(elem.toString())
        }
        event.imagesAsString = images
        val intent: Intent = Intent(applicationContext, EventDetailActivity::class.java).apply {
            putExtra("event", event)
        }
        startActivity(intent)
    }

}


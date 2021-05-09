package course.ru.qsearcher.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import course.ru.qsearcher.R
import course.ru.qsearcher.adapters.FavoritesAdapter
import course.ru.qsearcher.databinding.ActivityFavoritesBinding
import course.ru.qsearcher.listeners.FavoritesListener
import course.ru.qsearcher.model.Event
import course.ru.qsearcher.model.User
import course.ru.qsearcher.responses.SingleEventResponse
import course.ru.qsearcher.utilities.TempDataHolder
import course.ru.qsearcher.viewmodels.FavoritesViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_map.*
import java.util.*
import kotlin.collections.ArrayList


class FavoritesActivity : AppCompatActivity(), FavoritesListener {
    private lateinit var activityFavoritesBinding: ActivityFavoritesBinding
    private var favoritesViewModel: FavoritesViewModel? = null
    private var favoritesAdapter: FavoritesAdapter? = null
    private var favoritesList: MutableList<Event>? = null
    private var database: FirebaseDatabase? = null
    private var usersDbRef: DatabaseReference? = null
    private var usersChildEventListener: ChildEventListener? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityFavoritesBinding = DataBindingUtil.setContentView(this, R.layout.activity_favorites)
        initialize()
    }

    private fun setBottomNavigation() {
        val menuView = activityFavoritesBinding.bottomNavigation
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
        activityFavoritesBinding.bottomNavigation.selectedItemId = R.id.favorites
        activityFavoritesBinding.bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    startActivity(Intent(applicationContext, MainActivity::class.java))
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

    private fun initialize() {
        setBottomNavigation()
        favoritesViewModel = ViewModelProvider(this).get(FavoritesViewModel::class.java)
        favoritesList = mutableListOf()
        loadFavorites()
    }

    private fun loadFavorites() {
        activityFavoritesBinding?.isLoading = true
        val compositeDisposable = CompositeDisposable()
        favoritesViewModel?.loadFavorites()?.subscribeOn(Schedulers.computation())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe {
                activityFavoritesBinding.isLoading = false
                if (favoritesList?.size!! > 0) {
                    favoritesList?.clear()
                }
                favoritesList!!.addAll(it)
                favoritesAdapter = FavoritesAdapter(favoritesList!!, this)
                activityFavoritesBinding.favouritesRecyclerView.adapter = favoritesAdapter
                activityFavoritesBinding.favouritesRecyclerView.visibility = View.VISIBLE
                compositeDisposable.dispose()
                Toast.makeText(applicationContext, "Избранное:" + it.size, Toast.LENGTH_SHORT)
                    .show()
            }?.let {
                compositeDisposable.add(
                    it
                )
            }
        val timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                Handler(Looper.getMainLooper()).post {
                    if (SignInActivity.currentUser.favList != null && SignInActivity.currentUser.favList!!.size > 0) {
                        val ids = arrayListOf<Int>()
                        for (event in favoritesList!!) {
                            ids.add(event.id)
                            Log.i("fav", event.id.toString())
                        }
                        for (id in SignInActivity.currentUser.favList!!) {
                            if (!ids.contains(id)) {
                                getEvents(id)
                            }
                        }
                    }
                }
            }
        }, 800)
    }

    override fun onResume() {
        super.onResume()
        if (TempDataHolder.IS_FAVORITES_UPDATED) {
            loadFavorites()
            TempDataHolder.IS_FAVORITES_UPDATED = false
        }
    }

    override fun onEventClicked(event: Event) {
        if (event.imagesAsString == null) {
            val images: ArrayList<String> = arrayListOf()
            for (elem in event.images!!) {
                images.plusAssign(elem.toString())
            }
            event.imagesAsString = images
        }
        val intentTemp = Intent(applicationContext, EventDetailActivity::class.java)
        intentTemp.putExtra("event", event)
        startActivity(intentTemp)
    }

    override fun removeEventFromFavorites(event: Event, position: Int) {
        val compositeDisposableForDelete= CompositeDisposable()
        favoritesViewModel?.removeEventFromFavoritesList(event)
            ?.subscribeOn(Schedulers.computation())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe {
                favoritesList?.removeAt(position)
                favoritesAdapter?.notifyItemRemoved(position)
                favoritesList?.count()?.let {
                    favoritesAdapter?.notifyItemRangeChanged(
                        position,
                        it
                    )
                }
                compositeDisposableForDelete.dispose()
            }?.let { compositeDisposableForDelete.add(it) }


        if (SignInActivity.currentUser.favList != null && SignInActivity.currentUser.favList!!.size > 0) {
            database = FirebaseDatabase.getInstance()
            usersDbRef = database?.reference?.child("users")
            usersChildEventListener = object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val user: User = snapshot.getValue(User::class.java)!!
                    if (user.favList != null && user.favList!!.contains(event.id) &&
                        user.id != FirebaseAuth.getInstance().currentUser.uid && user.superId != null
                    ) {
                        var pos = 0
                        for ((i, id) in user.favList!!.withIndex()) {
                            if (id == event.id)
                                pos = i
                        }
                        user.favList!!.remove(pos)
                        usersDbRef!!.child(user.superId!!).child("favList").setValue(user.favList)
                    }
                }

                override fun onChildChanged(
                    snapshot: DataSnapshot,
                    previousChildName: String?
                ) {
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {}
                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onCancelled(error: DatabaseError) {}
            }
            usersDbRef?.addChildEventListener(usersChildEventListener as ChildEventListener)
        }
    }

    private fun getEvents(id: Int) {
        favoritesViewModel!!.getEventsById(id).observe(this, { t: SingleEventResponse? ->
            if (t != null) {
                if (t.id != null) {
                    val oldCount: Int = favoritesList!!.size
                    t.name = t.name!![0].toUpperCase() + t.name!!.substring(1, t.name!!.length)
                    val event = Event()
                    event.id = t.id!!
                    event.name = t.name!!
                    event.bodyText = t.bodyText!!
                    event.shortTitle = t.shortTitle!!
                    event.description = t.description
                    event.rating = t.rating!!
                    event.images = t.images!!
                    event.siteUrl = t.siteUrl
                    favoritesList!!.add(event)
                    favoritesAdapter!!.notifyDataSetChanged()
                    favoritesAdapter!!.notifyItemRangeChanged(
                        oldCount,
                        favoritesList!!.size / 1000
                    )
                } else {
                    Toast.makeText(applicationContext, "Произошла ошибка при выгрузке события", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(applicationContext, "Проищошла ошибка прии выгрузке события", Toast.LENGTH_SHORT).show()
            }
        })
    }
}


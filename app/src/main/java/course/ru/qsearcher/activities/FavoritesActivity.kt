package course.ru.qsearcher.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import course.ru.qsearcher.R
import course.ru.qsearcher.adapters.FavoritesAdapter
import course.ru.qsearcher.databinding.ActivityFavoritesBinding
import course.ru.qsearcher.listeners.FavoritesListener
import course.ru.qsearcher.model.Event
import course.ru.qsearcher.utilities.TempDataHolder
import course.ru.qsearcher.viewmodels.FavoritesViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_map.*

class FavoritesActivity : AppCompatActivity(), FavoritesListener {
    private lateinit var activityFavoritesBinding: ActivityFavoritesBinding
    private var favoritesViewModel: FavoritesViewModel? = null
    private var favoritesAdapter: FavoritesAdapter? = null
    private var favoritesList: MutableList<Event>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityFavoritesBinding = DataBindingUtil.setContentView(this, R.layout.activity_favorites)
        doInitialization()
    }
   private fun  setBottomNavigation(){
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

    private fun doInitialization() {
        setBottomNavigation()
        favoritesViewModel = ViewModelProvider(this).get(FavoritesViewModel::class.java)
        favoritesList = mutableListOf()
        loadFavorites()
    }

    private fun loadFavorites() {
        activityFavoritesBinding?.isLoading = true
        var compositeDisposable: CompositeDisposable = CompositeDisposable()
        favoritesViewModel?.loadFavorites()?.subscribeOn(Schedulers.computation())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe {
                activityFavoritesBinding?.isLoading = false
                if (favoritesList?.size!! > 0) {
                    favoritesList?.clear()
                }
                favoritesList!!.addAll(it)
                favoritesAdapter = FavoritesAdapter(favoritesList!!, this)
                activityFavoritesBinding?.favouritesRecyclerView?.adapter = favoritesAdapter
                activityFavoritesBinding?.favouritesRecyclerView?.visibility = View.VISIBLE
                compositeDisposable.dispose()
                Toast.makeText(applicationContext, "Избранное:" + it.size, Toast.LENGTH_SHORT)
                    .show()
            }?.let {
                compositeDisposable.add(
                    it
                )
            }
    }

    override fun onResume() {
        super.onResume()
        if (TempDataHolder.IS_FAVORITES_UPDATED) {
            loadFavorites()
            TempDataHolder.IS_FAVORITES_UPDATED = false
        }

    }

    override fun onEventClicked(event: Event) {
        val intentTemp: Intent = Intent(applicationContext, EventDetailActivity::class.java)
        intentTemp.putExtra("event", event)
        startActivity(intentTemp)
    }

    override fun removeEventFromFavorites(event: Event, position: Int) {
        var compositeDisposableForDelete: CompositeDisposable = CompositeDisposable()
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
    }

}
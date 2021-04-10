package course.ru.qsearcher.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import course.ru.qsearcher.R
import course.ru.qsearcher.databinding.ActivityFavoritesBinding
import course.ru.qsearcher.viewmodels.FavoritesViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class FavoritesActivity : AppCompatActivity() {
    private var activityFavoritesBinding: ActivityFavoritesBinding? = null
    private var favoritesViewModel: FavoritesViewModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityFavoritesBinding = DataBindingUtil.setContentView(this, R.layout.activity_favorites)
        doInitialization()
    }

    private fun doInitialization() {
        favoritesViewModel = ViewModelProvider(this).get(FavoritesViewModel::class.java)
        activityFavoritesBinding?.imageBack?.setOnClickListener { onBackPressed() }

    }

    private fun loadFavorites() {
        activityFavoritesBinding?.isLoading = true
        var compositeDisposable: CompositeDisposable = CompositeDisposable()
        favoritesViewModel?.loadFavorites()?.subscribeOn(Schedulers.computation())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe {
                activityFavoritesBinding?.isLoading = false
                Toast.makeText(applicationContext, "Избранное:" + it.size, Toast.LENGTH_SHORT).show()
            }?.let {
                compositeDisposable.add(
                    it
                )
            }
    }

    override protected fun onResume() {
        super.onResume()
        loadFavorites()
    }
}
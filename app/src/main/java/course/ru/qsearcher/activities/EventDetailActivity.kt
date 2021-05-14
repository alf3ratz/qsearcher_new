package course.ru.qsearcher.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import course.ru.qsearcher.R
import course.ru.qsearcher.adapters.ImageSliderAdapter
import course.ru.qsearcher.adapters.UsersAdapter
import course.ru.qsearcher.databinding.ActivityEventDetailBinding
import course.ru.qsearcher.listeners.OnUserClickListener
import course.ru.qsearcher.model.Event
import course.ru.qsearcher.model.User
import course.ru.qsearcher.utilities.TempDataHolder
import course.ru.qsearcher.viewmodels.EventsViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_event_detail.*
import kotlin.collections.ArrayList


@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class EventDetailActivity : AppCompatActivity(), OnUserClickListener {
    private lateinit var eventViewModel: EventsViewModel
    private var eventDetailActivityBinding: ActivityEventDetailBinding? = null
    private var database: FirebaseDatabase? = null
    private var usersDbRef: DatabaseReference? = null
    private var usersChildEventListener: ChildEventListener? = null
    private lateinit var event: Event

    private var isEventAvailableInFavorites: Boolean = false

    private lateinit var usersWithCurrentEvent: MutableList<User>
    private lateinit var userAdapter: UsersAdapter
    private lateinit var usersEmails: MutableList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        eventDetailActivityBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_event_detail);
        initialize(savedInstanceState)
    }

    private fun initialize(savedInstanceState: Bundle?) {
        eventViewModel = ViewModelProvider(this).get(EventsViewModel::class.java)
        BottomSheetBehavior.from(bottomSheet).apply {
            peekHeight = 55
            state = BottomSheetBehavior.STATE_HIDDEN
            isHideable = false
        }
        eventDetailActivityBinding?.imageBack?.setOnClickListener { onBackPressed() }
        event = intent.getSerializableExtra("event") as Event;
        checkEventInFavorites()
        getEvents(/*savedInstanceState*/)
        usersWithCurrentEvent = ArrayList()
        usersEmails = ArrayList()
        getUsersWithCurrentFavEvent()
        eventDetailActivityBinding?.usersWithEventRecycler?.visibility = View.VISIBLE
        eventDetailActivityBinding?.usersWithEventRecycler?.layoutManager =
            LinearLayoutManager(this)
        userAdapter = UsersAdapter(usersWithCurrentEvent, this)
        eventDetailActivityBinding?.apply {
            eventDetailActivityBinding?.usersWithEventRecycler?.adapter = userAdapter
            invalidateAll()
        }
        displayUsersWithCurrentEvent()
    }

    private fun displayUsersWithCurrentEvent() {
        if (usersWithCurrentEvent != null) {
            eventDetailActivityBinding?.usersWithEventRecycler?.visibility = View.VISIBLE
            eventDetailActivityBinding?.emptyListImage?.visibility = View.GONE
            eventDetailActivityBinding?.usersWithEventRecycler?.setHasFixedSize(true)
            val dividerItemDecoration = DividerItemDecoration(this, RecyclerView.VERTICAL)
            eventDetailActivityBinding?.usersWithEventRecycler?.addItemDecoration(
                dividerItemDecoration
            )
        } else {
            Toast.makeText(
                applicationContext,
                "Произошла ошибка при обращении к списку пользователей",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun getUsersWithCurrentFavEvent() {
        database = FirebaseDatabase.getInstance()
        usersDbRef = database?.reference?.child("users")
        usersChildEventListener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val user: User = snapshot.getValue(User::class.java)!!
                if (user.favList != null && user.favList!!.contains(event.id) && !usersEmails.contains(
                        user.email
                    ) && user.id != FirebaseAuth.getInstance().currentUser.uid && user.superId != null
                ) {
                    Log.i("bottom_sht", "добавил " + user.name + "'a")
                    usersWithCurrentEvent.add(user)
                    if (usersEmails == null)
                        usersEmails = ArrayList()
                    usersEmails.add(user.email!!)
                    userAdapter.notifyDataSetChanged()
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

    private fun checkEventInFavorites() {
        val compositeDisposable = CompositeDisposable()
        eventViewModel.getEventFromFavorites(event.id.toString())
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            ?.subscribe {
                isEventAvailableInFavorites = true
                eventDetailActivityBinding?.imageFavorites?.setImageResource(R.drawable.ic_added)
                compositeDisposable.dispose()
            }?.let { compositeDisposable.add(it) }
    }

    private fun getEvents() {
        eventDetailActivityBinding?.isLoading = true
        val imagesTemp: ArrayList<String>? = event.imagesAsString
        var images: ArrayList<String> = ArrayList()

        for (i in 1 until imagesTemp!!.size) {
            images.plusAssign(imagesTemp[i])
        }
        eventDetailActivityBinding?.eventImageURL = imagesTemp[0]
        eventDetailActivityBinding?.imageEvent!!.visibility = View.VISIBLE
        eventDetailActivityBinding?.eventDescription = HtmlCompat.fromHtml(
            event.bodyText.toString(), HtmlCompat.FROM_HTML_MODE_LEGACY
        ).toString()
        eventDetailActivityBinding?.rating =
            event.rating
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
                eventDetailActivityBinding?.textReadMore?.setText(R.string.read_more)
            }
        }
        eventViewModel.getMostPopularEvents(34)
            .observe(this, {
                run {
                    eventDetailActivityBinding?.isLoading = false
                    if (images.size == 0)
                        images = imagesTemp
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
            val intentInner = Intent(Intent.ACTION_VIEW)
            intentInner.data = Uri.parse(event.siteUrl)
            startActivity(intentInner)
        }
        eventDetailActivityBinding?.imageFavorites?.setOnClickListener {
            val compositeDisposable = CompositeDisposable()
            if (isEventAvailableInFavorites) {
                eventViewModel.removeEventFromFavorites(event)
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        isEventAvailableInFavorites = false
                        TempDataHolder.IS_FAVORITES_UPDATED = true
                        eventDetailActivityBinding?.imageFavorites?.setImageResource(R.drawable.ic_bookmark)
                        Toast.makeText(
                            applicationContext,
                            "Удалено из списка избранного",
                            Toast.LENGTH_LONG
                        ).show()
                        compositeDisposable.dispose()
                    }.let { it1 -> compositeDisposable.add(it1) }
            } else {
                eventViewModel.addToFavorites(event)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        TempDataHolder.IS_FAVORITES_UPDATED = true
                        eventDetailActivityBinding?.imageFavorites?.setImageResource(R.drawable.ic_added)
                        Toast.makeText(
                            applicationContext,
                            "Добавлено в список избранного",
                            Toast.LENGTH_LONG
                        ).show()
                        compositeDisposable.dispose()
                    }.let { it1 -> compositeDisposable.add(it1) }

                database = FirebaseDatabase.getInstance()
                usersDbRef = database?.reference?.child("users")
                usersChildEventListener = object : ChildEventListener {
                    override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                        for (temp in snapshot.children) {
                            Log.i("favList", temp.value.toString())
                        }
                        Log.i("favList", snapshot.value.toString())
                        val user: User = snapshot.getValue(User::class.java)!!
                        if (user.favList == null)
                            user.favList = ArrayList()
                        if (user.id == FirebaseAuth.getInstance().currentUser.uid) {
                            user.favList?.add(event.id)
                            usersDbRef?.child(user.superId!!)?.child("favList")
                                ?.setValue(user.favList)
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
//                eventViewModel?.addToFavorites(event)
////                    ?.subscribeOn(Schedulers.io())
////                    ?.observeOn(AndroidSchedulers.mainThread())
////                    ?.subscribe {
////                        eventDetailActivityBinding?.imageFavorites?.setImageResource(R.drawable.ic_added)
////                        Toast.makeText(
////                            applicationContext,
////                            "Добавлено в список озбранного",
////                            Toast.LENGTH_SHORT
////                        ).show()
////
////                    }?.let {
////                        CompositeDisposable().add(
////                            it
////                        )
////                        it.dispose()
////                    }
            }
        }
        eventDetailActivityBinding?.imageFavorites?.visibility = View.VISIBLE
        loadBasicEventDetails()
    }

    private fun loadImageSlider(sliderImages: ArrayList<String>) {
        eventDetailActivityBinding?.sliderViewPager?.offscreenPageLimit = 1
        eventDetailActivityBinding?.sliderViewPager?.adapter = ImageSliderAdapter(sliderImages)
        eventDetailActivityBinding?.sliderViewPager?.visibility = View.VISIBLE
        eventDetailActivityBinding?.viewFadingEdge?.visibility = View.VISIBLE
        setupSliderIndicators(sliderImages.size)
        eventDetailActivityBinding?.sliderViewPager?.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentSliderIndicator(position)
            }
        })
    }

    private fun setupSliderIndicators(count: Int) {
        val indicators: MutableList<ImageView> = mutableListOf<ImageView>().toMutableList()
        for (i in 0 until count) {
            indicators += ImageView(applicationContext)
        }
        val layoutParams: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(8, 0, 8, 0)

        for (i in 0 until count) {
            indicators[i] = ImageView(applicationContext)
            indicators[i].setImageDrawable(
                ContextCompat.getDrawable(
                    applicationContext, R.drawable.background_slider_indicator_inactive
                )
            )
            indicators[i].layoutParams = layoutParams
            eventDetailActivityBinding?.layoutSliderIndicators?.addView(indicators[i])
        }
        eventDetailActivityBinding?.layoutSliderIndicators?.visibility = View.VISIBLE
        setCurrentSliderIndicator(0)
    }

    private fun setCurrentSliderIndicator(position: Int) {
        val childCount: Int? = eventDetailActivityBinding?.layoutSliderIndicators?.childCount
        for (i in 0 until childCount!!) {
            val imageView: ImageView =
                eventDetailActivityBinding?.layoutSliderIndicators?.getChildAt(i) as ImageView
            if (i == position) {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.background_slider_indicator_active
                    )
                )
            } else {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.background_slider_indicator_inactive
                    )
                )
            }
        }
    }

    private fun loadBasicEventDetails() {
        eventDetailActivityBinding?.eventShortName =
            event.shortTitle
        eventDetailActivityBinding?.textName?.visibility = View.VISIBLE
        eventDetailActivityBinding?.textShortName?.visibility = View.VISIBLE
    }

    /**
     * Обработчик нажатия на элемент списка пользователей.
     */
    override fun onUserClick(user: User) {
        if (user != null) {
            super.onUserClick(user)
            goToProfile(user)
        } else {
            Log.i("user", "Невозможно перейти на страницу профиля")
        }
    }

    /**
     * Функция осуществления перехода на страницу профиля выбранного пользователя.
     */
    private fun goToProfile(user: User) {
        val intent = Intent(applicationContext, ProfileActivity::class.java).apply {
            putExtra("user", user)
        }
        startActivity(intent)
    }
}

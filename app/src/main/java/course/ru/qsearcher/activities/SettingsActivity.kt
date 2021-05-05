package course.ru.qsearcher.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import course.ru.qsearcher.R
import course.ru.qsearcher.adapters.EventsAdapter
import course.ru.qsearcher.databinding.ActivitySettingsBinding
import course.ru.qsearcher.listeners.EventListener
import course.ru.qsearcher.model.Event
import course.ru.qsearcher.model.User
import course.ru.qsearcher.responses.SingleEventResponse
import course.ru.qsearcher.viewmodels.MostPopularEventsViewModel
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.activity_settings.*
import java.lang.Exception


class SettingsActivity : AppCompatActivity(), EventListener {
    private lateinit var activitySettingsBinding: ActivitySettingsBinding
    private lateinit var viewModel: MostPopularEventsViewModel
    private lateinit var eventsAdapter: EventsAdapter
    private var database: FirebaseDatabase? = null
    private var usersDbRef: DatabaseReference? = null
    private var usersChildEventListener: ChildEventListener? = null
    private var newName: String = ""
    private var currentPage: Int = 1;
    private var totalAvailablePages: Int = 1
    private var events: ArrayList<Event> = ArrayList()
    private var storage: FirebaseStorage? = null
    private var storageRef: StorageReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activitySettingsBinding = DataBindingUtil.setContentView(this, R.layout.activity_settings)
        storage = FirebaseStorage.getInstance()
        storageRef = storage?.reference?.child("avatars")
        setNavigation()
        doInitialization()

    }

    private fun doInitialization() {
        activitySettingsBinding.confirmButton.visibility = View.GONE
        activitySettingsBinding.favEventsRecycler?.setHasFixedSize(true)
        activitySettingsBinding.userName.text = SignInActivity.userName
        storageRef?.child(SignInActivity.currentUser.email + "avatar")?.downloadUrl?.addOnSuccessListener {
            Picasso.get().load(it).noFade().into(activitySettingsBinding.userImage, object : Callback {
                override fun onSuccess() {
                    // photoImageView.animate().setDuration(300).alpha(1f).start()
                }

                override fun onError(e: Exception) {
                }
            })
            //activitySettingsBinding.userImage.setImageURI(uri)
        }?.addOnFailureListener {
            Log.i(
                "ava",
                "Не получилось загрузить аватар для " + SignInActivity.currentUser.email
            )
        }
        //activitySettingsBinding.userImage.setImageResource(SignInActivity.currentUser.avatarMock)
        //activitySettingsBinding.userImage.setImageURI(Uri.parse("https://disk.yandex.ru/i/uIJBwukIRkePHQ"))
        viewModel = ViewModelProvider(this).get(MostPopularEventsViewModel::class.javaObjectType)
        eventsAdapter = EventsAdapter(events, this)
        activitySettingsBinding.apply {
            activitySettingsBinding.favEventsRecycler.adapter = eventsAdapter
            invalidateAll()
        }


        activitySettingsBinding.favEventsRecycler.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!activitySettingsBinding.favEventsRecycler.canScrollVertically(1)) {
                    database = FirebaseDatabase.getInstance()
                    usersDbRef = database?.reference?.child("users")
                    usersChildEventListener = object : ChildEventListener {
                        override fun onChildAdded(
                            snapshot: DataSnapshot,
                            previousChildName: String?
                        ) {
                            val user: User = snapshot.getValue(User::class.java)!!
                            if (user.id == FirebaseAuth.getInstance().currentUser.uid && user.name != newName) {
                                //getEvents(user.favList[1])
                            }
                        }

                        override fun onChildChanged(
                            snapshot: DataSnapshot,
                            previousChildName: String?
                        ) {
                        }

                        override fun onChildRemoved(snapshot: DataSnapshot) {}
                        override fun onChildMoved(
                            snapshot: DataSnapshot,
                            previousChildName: String?
                        ) {
                        }

                        override fun onCancelled(error: DatabaseError) {}
                    }
                    usersDbRef?.addChildEventListener(usersChildEventListener as ChildEventListener)

                }
            }
        })
        activitySettingsBinding.exitButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(applicationContext, SignInActivity::class.java))
        }
        activitySettingsBinding.editButton.setOnClickListener {
            activitySettingsBinding.userName.visibility = View.GONE
            activitySettingsBinding.editUserName.visibility = View.VISIBLE
        }
        activitySettingsBinding.confirmButton.setOnClickListener {
            if (editUserName.text.toString().trim().isNotBlank() && editUserName.text.toString()
                    .trim().isNotEmpty()
            ) {
                newName = editUserName.text.toString()
                database = FirebaseDatabase.getInstance()
                usersDbRef = database?.reference?.child("users")
                usersChildEventListener = object : ChildEventListener {
                    override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                        val user: User = snapshot.getValue(User::class.java)!!
                        if (user.id == FirebaseAuth.getInstance().currentUser.uid && user.name != newName) {
                            usersDbRef?.child(user.name!!)?.child("name")?.setValue(newName)
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
                activitySettingsBinding.confirmButton.visibility = View.GONE
                activitySettingsBinding.editUserName.visibility = View.GONE
                activitySettingsBinding.userName.text = newName
                activitySettingsBinding.userName.visibility = View.VISIBLE
            }
        }
        getEvents(179108)
        //activitySettingsBinding.userImage =
    }

    private fun setNavigation() {
        val menuView = activitySettingsBinding.bottomNavigation
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
        activitySettingsBinding.bottomNavigation.selectedItemId = R.id.settings
        activitySettingsBinding.bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    startActivity(Intent(applicationContext, MainActivity::class.java))
                    overridePendingTransition(0, 0)
                    return@setOnNavigationItemSelectedListener true
                }
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
                //R.id.settings -> startActivity(Intent(applicationContext,FavoritesActivity::class.java))
                R.id.map -> {
                    startActivity(Intent(applicationContext, MapActivity::class.java))
                    overridePendingTransition(0, 0)
                    return@setOnNavigationItemSelectedListener true
                }
            }
            false
        }
    }
    private fun getEvents(id: Int) {
        //toggleLoading()
        //var temp: ArrayList<Event> = ArrayList()
        viewModel.getEventsById(id).observe(this, Observer { t: SingleEventResponse? ->
            //toggleLoading()
            Log.i("response_", "вошел в лямбду")
            if (t != null) {
                Log.i("response_", "если респонс не налл")
                //                totalAvailablePages = t.page!!
                if (t.id != null) {
                    val oldCount: Int = events.size
                    Log.i("response_", "если список событий не налл")
                    t.name = t.name!![0].toUpperCase() + t.name!!.substring(1, t.name!!.length)
//                    for (elem in t.events!!) {
//                        elem.name = elem.name!![0].toUpperCase() + elem.name!!.substring(
//                            1,
//                            elem.name!!.length
//                        )
//                    }
                    val event = Event()
                    event.id = t.id!!
                    event.name = t.name!!
                    event.bodyText = t.bodyText!!
                    event.shortTitle = t.shortTitle!!
                    event.description = t.description
                    event.rating = t.rating!!
                    event.images = t.images!!
                    events.add(event)
                    eventsAdapter.notifyDataSetChanged()
                    eventsAdapter.notifyItemRangeChanged(
                        oldCount,
                        events.size / 1000
                    )//проблема с выводом - показывает после выхода из экрана
                } else {
                    Toast.makeText(applicationContext, "Smth went wrong", Toast.LENGTH_SHORT).show()
                    Log.i("response_", "список событий  налл")
                }
            } else {
                Toast.makeText(applicationContext, "Smth went wrong", Toast.LENGTH_SHORT).show()
                Log.i("response_", "респонс  налл")
            }
        })
    }

    override fun onEventClicked(event: Event) {
        val images: ArrayList<String> = arrayListOf<String>()
        for (elem in event.images!!) {
            images.plusAssign(elem.toString())
        }
        event.imagesAsString = images
        val intent: Intent = Intent(applicationContext, EventDetailActivity::class.java).apply {
            putExtra("event", event)
        }
        startActivity(intent);
    }
}
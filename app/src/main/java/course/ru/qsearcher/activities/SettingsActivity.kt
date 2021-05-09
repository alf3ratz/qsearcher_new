package course.ru.qsearcher.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import course.ru.qsearcher.R
import course.ru.qsearcher.adapters.EventsAdapter
import course.ru.qsearcher.adapters.UsersAdapter
import course.ru.qsearcher.databinding.ActivitySettingsBinding
import course.ru.qsearcher.listeners.EventListener
import course.ru.qsearcher.listeners.OnUserClickListener
import course.ru.qsearcher.model.Event
import course.ru.qsearcher.model.User
import course.ru.qsearcher.responses.SingleEventResponse
import course.ru.qsearcher.viewmodels.EventsViewModel
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.activity_settings.view.*
import kotlinx.android.synthetic.main.dialog_for_friemds.view.*
import org.jetbrains.anko.dip


class SettingsActivity : AppCompatActivity(), EventListener, OnUserClickListener {
    private lateinit var activitySettingsBinding: ActivitySettingsBinding
    private lateinit var viewModel: EventsViewModel
    private lateinit var eventsAdapter: EventsAdapter
    private var database: FirebaseDatabase? = null
    private var usersDbRef: DatabaseReference? = null
    private var usersChildEventListener: ChildEventListener? = null
    private var newName: String = ""
    private var events: ArrayList<Event> = ArrayList()
    private var storage: FirebaseStorage? = null
    private var storageRef: StorageReference? = null
    private var isEmailEnabled: Boolean = false
    private var isCompanyEnabled: Boolean = false
    private var isTouched: Boolean = false

    private lateinit var preferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var userAdapter: UsersAdapter

    private fun initPrefs() {
        preferences = getSharedPreferences("My prefs", Context.MODE_PRIVATE)
        editor = preferences.edit()
    }

    private fun showDialog() {
        val dialog = LayoutInflater.from(this).inflate(R.layout.dialog_for_friemds, null)
        val builder = AlertDialog.Builder(this).setView(dialog).setTitle("Друзья")
        val alert = builder.show()
        var friends = ArrayList<User>()
        database = FirebaseDatabase.getInstance()
        usersDbRef = database?.reference?.child("users")
        usersChildEventListener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val user: User = snapshot.getValue(User::class.java)!!
                if(SignInActivity.currentUser.friends.contains(user.superId)){
                    friends.add(user)
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) { }

            override fun onChildRemoved(snapshot: DataSnapshot) {}

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onCancelled(error: DatabaseError) {}
        }
        usersDbRef?.addChildEventListener(usersChildEventListener as ChildEventListener)
        //for(str in SignInActivity.currentUser.friends){
//            usersDbRef!!.child(SignInActivity.currentUser.superId!!).child("friends").get().addOnSuccessListener {
//                val tmp = it.getValue(Collection<User>)
//                friends.addAll (it.getValue(Collection<User::class.java>)!!)
//                //Log.i("firebase", "Got value ${it.value}")
//            }.addOnFailureListener{
//                Log.e("firebase", "Error getting data", it)
//            }
        //val user = usersDbRef?.child(SignInActivity.currentUser.superId!!)!!.child("friends").child(str).get().result.getValue(User::class.java)
        //friends.add(user!!)
        //}
        dialog.friends.layoutManager =
            LinearLayoutManager(this)
        userAdapter = UsersAdapter(friends, this)
        dialog.apply {
            dialog.friends.adapter = userAdapter
            //invalidateAll()
        }
        userAdapter.notifyDataSetChanged()
        builder.setNeutralButton("OK") { dialog, which ->
            alert.dismiss()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activitySettingsBinding = DataBindingUtil.setContentView(this, R.layout.activity_settings)
        storage = FirebaseStorage.getInstance()
        storageRef = storage?.reference?.child("avatars")
        setNavigation()
        doInitialization()
        if (SignInActivity.currentUser.isEmailActivated) {
            Log.i("switch", "zashel")
            activitySettingsBinding.isEmailActivated.isChecked = true
        }
        if (SignInActivity.currentUser.isOccupationActivated) {
            activitySettingsBinding.userOccupation.text =
                "Деятельность: " + SignInActivity.currentUser.occupation
            activitySettingsBinding.userOccupation.visibility = View.VISIBLE
        }

        if (SignInActivity.currentUser.isCityActivated) {
            activitySettingsBinding.userCity.text = "Город: " + SignInActivity.currentUser.city
            activitySettingsBinding.userCity.visibility = View.VISIBLE
        }
        if (SignInActivity.currentUser.isNetworkActivated) {
            activitySettingsBinding.userNetwork.visibility = View.VISIBLE
            activitySettingsBinding.userNetwork.text =
                "Соц. сеть: " + SignInActivity.currentUser.socialNetworkUrl
        }

        activitySettingsBinding.userName.text = "Имя: " + SignInActivity.currentUser.name
    }

    private fun doInitialization() {
        activitySettingsBinding.confirmButton.visibility = View.GONE
        activitySettingsBinding.friendsRow.friendsButton.setOnClickListener {
            showDialog()
        }
        //activitySettingsBinding.favEventsRecycler?.setHasFixedSize(true)
        //activitySettingsBinding.userName.text = SignInActivity.currentUser.name
        activitySettingsBinding.isEmailActivated.setOnCheckedChangeListener { compoundButton, b ->
            isEmailEnabled = b
            isTouched = true
        }
        activitySettingsBinding.isCompanyActivated.setOnCheckedChangeListener { compoundButton, b ->
            isCompanyEnabled = b
            isTouched = true
        }

        storageRef?.child(SignInActivity.currentUser.superId + "avatar")?.downloadUrl?.addOnSuccessListener {
            Picasso.get().load(it).noFade().into(
                activitySettingsBinding.userImage,
                object : Callback {
                    override fun onSuccess() {}
                    override fun onError(e: Exception) {}
                })
        }?.addOnFailureListener {
            Log.i(
                "ava",
                "Не получилось загрузить аватар для " + SignInActivity.currentUser.superId
            )
            try {
            } catch (e: StorageException) {
                Log.i(
                    "ava",
                    " at catch Не получилось загрузить аватар для " + SignInActivity.currentUser.superId
                )
            }
        }
        viewModel = ViewModelProvider(this).get(EventsViewModel::class.javaObjectType)
//        eventsAdapter = EventsAdapter(events, this)
//        activitySettingsBinding.apply {
//            activitySettingsBinding.favEventsRecycler.adapter = eventsAdapter
//            invalidateAll()
//        }
//
//
//        activitySettingsBinding.favEventsRecycler.addOnScrollListener(object :
//            RecyclerView.OnScrollListener() {
//            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                super.onScrolled(recyclerView, dx, dy)
//                if (!activitySettingsBinding.favEventsRecycler.canScrollVertically(1)) {
//                    database = FirebaseDatabase.getInstance()
//                    usersDbRef = database?.reference?.child("users")
//                    usersChildEventListener = object : ChildEventListener {
//                        override fun onChildAdded(
//                            snapshot: DataSnapshot,
//                            previousChildName: String?
//                        ) {
//                            val user: User = snapshot.getValue(User::class.java)!!
//                            if (user.id == FirebaseAuth.getInstance().currentUser.uid && user.name != newName) {
//                                //getEvents(user.favList[1])
//                            }
//                        }
//
//                        override fun onChildChanged(
//                            snapshot: DataSnapshot,
//                            previousChildName: String?
//                        ) {
//                        }
//
//                        override fun onChildRemoved(snapshot: DataSnapshot) {}
//                        override fun onChildMoved(
//                            snapshot: DataSnapshot,
//                            previousChildName: String?
//                        ) {
//                        }
//
//                        override fun onCancelled(error: DatabaseError) {}
//                    }
//                    usersDbRef?.addChildEventListener(usersChildEventListener as ChildEventListener)
//
//                }
//            }
//        })
        activitySettingsBinding.exitButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(applicationContext, SignInActivity::class.java))
        }
        activitySettingsBinding.editButton.setOnClickListener {
            activitySettingsBinding.userName.visibility = View.GONE
            activitySettingsBinding.userOccupation.visibility = View.GONE
            activitySettingsBinding.userCity.visibility = View.GONE
            activitySettingsBinding.userNetwork.visibility = View.GONE
            activitySettingsBinding.editUserName.visibility = View.VISIBLE
            activitySettingsBinding.editOccupation.visibility = View.VISIBLE
            activitySettingsBinding.confirmButton.visibility = View.VISIBLE
            activitySettingsBinding.editNetwork.visibility = View.VISIBLE
            activitySettingsBinding.editCity.visibility = View.VISIBLE
            activitySettingsBinding.isEmailActivated.visibility = View.VISIBLE
            activitySettingsBinding.isCompanyActivated.visibility = View.VISIBLE
            activitySettingsBinding.infoHeader.layoutParams =
                ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, 800)
        }
        activitySettingsBinding.confirmButton.setOnClickListener {
            if (editUserName.text.toString().trim().isNotBlank() || editOccupation.text.toString()
                    .trim().isNotBlank() || editCity.text.toString().trim()
                    .isNotBlank() || editNetwork.text.toString().trim()
                    .isNotBlank() || isTouched
            ) {
                var newOccupation: String = "-"
                var newCity: String = "-"
                var newNetwork: String = "-"
                var isOcAct: Boolean = false
                var isCityAct: Boolean = false
                var isNetAct = false
                if (editUserName.text.toString().trim().isNotBlank() && editUserName.text.toString()
                        .trim().isNotEmpty()
                )
                    newName = editUserName.text.toString()
                if (editCity.text.toString().trim().isNotBlank() && editCity.text.toString().trim()
                        .isNotEmpty()
                ) {
                    isCityAct = true
                    newCity = editCity.text.toString()
                }
                if (editOccupation.text.toString().trim()
                        .isNotBlank() && editOccupation.text.toString().trim().isNotEmpty()
                ) {
                    isOcAct = true
                    newOccupation = editOccupation.text.toString()
                }
                if (editNetwork.text.toString().trim().isNotBlank() && editNetwork.text.toString()
                        .trim().isNotEmpty()
                ) {
                    isNetAct = true
                    newNetwork = editNetwork.text.toString()
                }
                database = FirebaseDatabase.getInstance()
                usersDbRef = database?.reference?.child("users")
                usersChildEventListener = object : ChildEventListener {
                    override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                        val user: User = snapshot.getValue(User::class.java)!!
                        if (user.id == FirebaseAuth.getInstance().currentUser.uid && newName.isEmpty() && newName.isBlank()) {
//                            usersDbRef?.child(user.name!!)?.child("name")?.setValue(newName)
//                            usersDbRef?.child(user.name!!)?.child("occupation")?.setValue(newOccupation)
//                            usersDbRef?.child(user.name!!)?.child("city")?.setValue(newCity)
//                            usersDbRef?.child(user.name!!)?.child("socialNetwork")?.setValue(newNetwork)
//                            usersDbRef?.child(user.name!!)?.child("cityActivated")?.setValue(isCityAct)
//                            usersDbRef?.child(user.name!!)?.child("occupationActivated")?.setValue(isOcAct)
//                            usersDbRef?.child(user.name!!)?.child("networkActivated")?.setValue(isNetAct)
//                            if(activitySettingsBinding.isEmailActivated.isEnabled)
//                                usersDbRef?.child(user.name!!)?.child("emailActivated")?.setValue(true)
//                            else
//                                usersDbRef?.child(user.name!!)?.child("emailActivated")?.setValue(false)
                            if (newCity != "-") {
                                user.city = newCity
                                user.isCityActivated = isCityAct
                            }
                            if (newOccupation != "-") {
                                user.occupation = newOccupation
                                user.isOccupationActivated = isOcAct
                            }
                            if (newNetwork != "-") {
                                user.socialNetworkUrl = newNetwork
                                user.isNetworkActivated = isNetAct
                            }

                            user.isEmailActivated = isEmailEnabled
                            user.searchingCompany = isCompanyEnabled

                            usersDbRef?.child(user.superId!!)?.setValue(user)
                            SignInActivity.currentUser = user


                        } else if (user.id == FirebaseAuth.getInstance().currentUser.uid && newName != user.name) {
                            val userTemp = User()
                            userTemp.name = newName
                            SignInActivity.userName = newName
                            if (newCity != "-") {
                                userTemp.city = newCity
                                userTemp.isCityActivated = isCityAct
                            } else {
                                userTemp.city = user.city
                            }
                            if (newOccupation != "-") {
                                userTemp.occupation = newOccupation
                                userTemp.isOccupationActivated = isOcAct
                            } else {
                                userTemp.occupation = user.occupation
                            }
                            if (newNetwork != "-") {
                                userTemp.socialNetworkUrl = newNetwork
                                userTemp.isNetworkActivated = isNetAct
                            } else {
                                userTemp.socialNetworkUrl = user.socialNetworkUrl
                            }

                            userTemp.isEmailActivated = isEmailEnabled
                            userTemp.searchingCompany = isCompanyEnabled

                            userTemp.email = user.email
                            userTemp.avatarMock = user.avatarMock
                            userTemp.favList = user.favList
                            userTemp.usersList = user.usersList
                            userTemp.id = user.id
                            userTemp.superId = user.superId
                            usersDbRef?.child(userTemp.superId!!)?.setValue(userTemp)
                            SignInActivity.currentUser = userTemp

                            //usersDbRef?.child(user.email!!)?.removeValue(user)
                        }
                        if (SignInActivity.currentUser.isOccupationActivated) {
                            activitySettingsBinding.userOccupation.text =
                                "Деятельность: " + SignInActivity.currentUser.occupation
                            activitySettingsBinding.userOccupation.visibility = View.VISIBLE
                        }

                        if (SignInActivity.currentUser.isCityActivated) {
                            activitySettingsBinding.userCity.text =
                                "Город: " + SignInActivity.currentUser.city
                            activitySettingsBinding.userCity.visibility = View.VISIBLE
                        }
                        if (SignInActivity.currentUser.isNetworkActivated) {
                            activitySettingsBinding.userNetwork.visibility = View.VISIBLE
                            activitySettingsBinding.userNetwork.text =
                                "Соц. сеть: " + SignInActivity.currentUser.socialNetworkUrl
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
                activitySettingsBinding.editOccupation.visibility = View.GONE
                activitySettingsBinding.editCity.visibility = View.GONE
                activitySettingsBinding.editNetwork.visibility = View.GONE
                activitySettingsBinding.isEmailActivated.visibility = View.GONE
                activitySettingsBinding.isCompanyActivated.visibility = View.GONE
                if (newName.isNotEmpty() && newName.isNotBlank())
                    activitySettingsBinding.userName.text = "Имя: " + newName
//                if (SignInActivity.currentUser.isOccupationActivated){
//                    activitySettingsBinding.userOccupation.text =
//                        "Деятельность: " + SignInActivity.currentUser.occupation
//                    activitySettingsBinding.userOccupation.visibility = View.VISIBLE
//                }
//
//                if (SignInActivity.currentUser.isCityActivated){
//                    activitySettingsBinding.userCity.text = "Город: " + SignInActivity.currentUser.city
//                    activitySettingsBinding.userCity.visibility = View.VISIBLE
//                }
//                if (SignInActivity.currentUser.isNetworkActivated){
//                    activitySettingsBinding.userNetwork.visibility = View.VISIBLE
//                    activitySettingsBinding.userNetwork.text =
//                        "Соц. сеть: " + SignInActivity.currentUser.socialNetworkUrl
//                }

                activitySettingsBinding.userName.visibility = View.VISIBLE
                val params = ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.MATCH_PARENT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT
                )
                val dd = dip(4)
                params.setMargins(dd, dd, dd, dd)
                activitySettingsBinding.infoHeader.layoutParams = params
            } else {
                activitySettingsBinding.confirmButton.visibility = View.GONE
                activitySettingsBinding.editUserName.visibility = View.GONE
                activitySettingsBinding.editOccupation.visibility = View.GONE
                activitySettingsBinding.editCity.visibility = View.GONE
                activitySettingsBinding.editNetwork.visibility = View.GONE
                activitySettingsBinding.userName.visibility = View.VISIBLE
                activitySettingsBinding.isEmailActivated.visibility = View.GONE
                activitySettingsBinding.isCompanyActivated.visibility = View.GONE

                val params = ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.MATCH_PARENT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT
                )
                val dd = dip(4)
                params.setMargins(dd, dd, dd, dd)
                activitySettingsBinding.infoHeader.layoutParams = params
            }
        }
        //getEvents(179108)
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
    override fun onUserCLick(user: User) {
        super.onUserCLick(user)
        if (user != null) {
            Log.i("user", user.name!!)
            goToProfile(user)
        } else {
            Log.i("user", "fail to send user in intent")
        }
    }

    private fun goToProfile(user: User) {
        val intent = Intent(applicationContext, ProfileActivity::class.java).apply {
            putExtra("user", user)
        }
        startActivity(intent)
    }
}
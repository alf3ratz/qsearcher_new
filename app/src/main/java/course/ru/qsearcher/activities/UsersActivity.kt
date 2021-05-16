package course.ru.qsearcher.activities

import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import course.ru.qsearcher.R
import course.ru.qsearcher.adapters.UsersAdapterForUsersPage
import course.ru.qsearcher.databinding.ActivityUsersBinding
import course.ru.qsearcher.listeners.OnUserClickListener
import course.ru.qsearcher.model.User
import kotlinx.android.synthetic.main.activity_map.*


@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class UsersActivity : AppCompatActivity(), OnUserClickListener {

    private lateinit var usersRef: DatabaseReference
    private var usersChildEventListener: ChildEventListener? = null
    private lateinit var activityUsersBinding: ActivityUsersBinding
    private lateinit var users: MutableList<User>
    private lateinit var userAdapter: UsersAdapterForUsersPage
    private lateinit var auth: FirebaseAuth
    private lateinit var userName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityUsersBinding = DataBindingUtil.setContentView(this, R.layout.activity_users)
        auth = FirebaseAuth.getInstance()
        users = mutableListOf()
        userName = SignInActivity.userName
        doInitialization()
    }

    override fun onResume() {
        super.onResume()
        usersChildEventListener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val user: User = snapshot.getValue(User::class.java)!!
                if (SignInActivity.currentUser.usersList == null) {
                    SignInActivity.currentUser.usersList = ArrayList()
                }
                if (user.id != auth.currentUser.uid && SignInActivity.currentUser.usersList?.contains(
                        user.id
                    )!!
                ) {
                    user.avatarMock = R.drawable.ic_person
                    users.add(user)
                    userAdapter.notifyDataSetChanged()
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        }
    }
    private fun doInitialization() {
        setBottomNavigation()
        activityUsersBinding.usersRecyclerView.setHasFixedSize(true)
        val dividerItemDecoration = DividerItemDecoration(this, RecyclerView.VERTICAL)
        activityUsersBinding.usersRecyclerView.addItemDecoration(dividerItemDecoration)
        activityUsersBinding.usersRecyclerView.layoutManager = LinearLayoutManager(this)
        usersRef = FirebaseDatabase.getInstance().reference.child("users")
        if (usersChildEventListener == null) {
            usersChildEventListener = object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val user: User = snapshot.getValue(User::class.java)!!
                    if(SignInActivity.currentUser.usersList == null){
                        SignInActivity.currentUser.usersList = ArrayList()
                    }
                     if (user.id != auth.currentUser.uid && SignInActivity.currentUser.usersList?.contains(user.id)!!) {
                        users.add(user)
                        userAdapter.notifyDataSetChanged()
                    }
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        }
    }
    usersRef.addChildEventListener(usersChildEventListener!!)
    userAdapter = UsersAdapterForUsersPage(users, this)
    activityUsersBinding.apply {
            activityUsersBinding.usersRecyclerView.adapter = userAdapter
            invalidateAll()
        }
        //activityUsersBinding.imageBack.setOnClickListener { onBackPressed() }
    }

    private fun setBottomNavigation() {
        val menuView = activityUsersBinding.bottomNavigation
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
        activityUsersBinding.bottomNavigation.selectedItemId = R.id.chat
        activityUsersBinding.bottomNavigation.setOnNavigationItemSelectedListener { item ->
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

    override fun onUserClick(user: User) {
        super.onUserClick(user)
        if (user != null) {
            goToChat(user)
        } else {
            Toast.makeText(applicationContext,"Невозможно перейти на страницу пользователя", Toast.LENGTH_LONG).show()
        }
    }

    private fun goToChat(user: User) {
        val intent: Intent = Intent(applicationContext, ChatActivity::class.java).apply {
            putExtra("user",user)
        }
        startActivity(intent)
    }
}
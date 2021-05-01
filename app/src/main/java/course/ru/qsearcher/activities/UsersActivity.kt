package course.ru.qsearcher.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import course.ru.qsearcher.R
import course.ru.qsearcher.adapters.UsersAdapter
import course.ru.qsearcher.databinding.ActivityUsersBinding
import course.ru.qsearcher.listeners.OnUserClickListener
import course.ru.qsearcher.model.User
import kotlinx.android.synthetic.main.activity_map.*


class UsersActivity : AppCompatActivity(), OnUserClickListener {

    private lateinit var usersRef: DatabaseReference
    private var usersChildEventListener: ChildEventListener? = null
    private lateinit var activityUsersBinding: ActivityUsersBinding
    private lateinit var users: MutableList<User>
    private lateinit var userAdapter: UsersAdapter
    private lateinit var auth: FirebaseAuth
    private lateinit var userName:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityUsersBinding = DataBindingUtil.setContentView(this, R.layout.activity_users)
        auth = FirebaseAuth.getInstance()
        users = mutableListOf()
        userName = SignInActivity.userName
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
                R.id.settings -> {startActivity(Intent(applicationContext,SettingsActivity::class.java))
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
        doInitialization()
    }

    private fun doInitialization() {
        activityUsersBinding.usersRecyclerView.setHasFixedSize(true)
        val dividerItemDecoration = DividerItemDecoration(this, RecyclerView.VERTICAL)
        activityUsersBinding.usersRecyclerView.addItemDecoration(dividerItemDecoration)
        activityUsersBinding.usersRecyclerView.layoutManager = LinearLayoutManager(this)
        usersRef = FirebaseDatabase.getInstance().reference.child("users")
        if (usersChildEventListener == null) {
            usersChildEventListener = object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val user: User = snapshot.getValue(User::class.java)!!
                    if (user.id != auth.currentUser.uid) {
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
        usersRef.addChildEventListener(usersChildEventListener!!)
        userAdapter = UsersAdapter(users, this)
        activityUsersBinding.apply {
            activityUsersBinding.usersRecyclerView.adapter = userAdapter
            invalidateAll()
        }
        activityUsersBinding.imageBack.setOnClickListener { onBackPressed() }
    }

    override fun onUserCLick(user: User) {
        super.onUserCLick(user)
        if(user!=null){
            Log.i("user",user.name)
            goToChat(user)
        }else{
            Log.i("user","jopa")
        }
    }

    private fun goToChat(user: User) {
        var intent:Intent = Intent(applicationContext, ChatActivity::class.java).apply {
//            putExtra("name",user.name)
//            putExtra("email",user.email)
            putExtra("receiverId", user.id)
            putExtra("receiverName",user.name)
            putExtra("userName", userName)
//            putExtra("avatar",user.avatarMock)
        }
        startActivity(intent)
    }
}
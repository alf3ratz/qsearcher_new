package course.ru.qsearcher.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import course.ru.qsearcher.R
import course.ru.qsearcher.adapters.UsersAdapter
import course.ru.qsearcher.databinding.ActivityUsersBinding
import course.ru.qsearcher.listeners.OnUserClickListener
import course.ru.qsearcher.model.User
import kotlinx.android.synthetic.main.activity_users.*

class UsersActivity : AppCompatActivity(), OnUserClickListener {

    private lateinit var usersRef: DatabaseReference
    private var usersChildEventListener: ChildEventListener? = null
    private lateinit var activityUsersBinding: ActivityUsersBinding
    private lateinit var users: MutableList<User>
    private lateinit var userAdapter: UsersAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityUsersBinding = DataBindingUtil.setContentView(this, R.layout.activity_users)
        users = mutableListOf()
        doInitialization()
    }

    private fun doInitialization() {
        activityUsersBinding.usersRecyclerView.setHasFixedSize(true)
        activityUsersBinding.usersRecyclerView.layoutManager = LinearLayoutManager(this)
        usersRef = FirebaseDatabase.getInstance().reference.child("users")
        if (usersChildEventListener == null) {
            usersChildEventListener = object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    var user: User = snapshot.getValue(User::class.java)!!
                    user.avatarMock = R.drawable.ic_person
                    users.add(user)
                    userAdapter.notifyDataSetChanged()
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
}
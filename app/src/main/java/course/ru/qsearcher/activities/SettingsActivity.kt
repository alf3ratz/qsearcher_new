package course.ru.qsearcher.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import course.ru.qsearcher.R
import course.ru.qsearcher.databinding.ActivitySettingsBinding
import course.ru.qsearcher.model.User
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.activity_settings.*


class SettingsActivity : AppCompatActivity() {
    private lateinit var activitySettingsBinding: ActivitySettingsBinding
    private var database: FirebaseDatabase? = null
    private var usersDbRef: DatabaseReference? = null
    private var usersChildEventListener: ChildEventListener? = null
    private var newName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activitySettingsBinding = DataBindingUtil.setContentView(this, R.layout.activity_settings)
        setNavigation()
        doInitialization()
    }

    private fun doInitialization() {
        activitySettingsBinding.userName.text = SignInActivity.userName
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
                            usersDbRef?.child(user.name)?.child("name")?.setValue(newName)
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
        //activitySettingsBinding.userImage =
    }

    private fun setNavigation() {
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
}
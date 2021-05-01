package course.ru.qsearcher.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import course.ru.qsearcher.R
import course.ru.qsearcher.databinding.ActivitySettingsBinding
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.activity_settings.*


class SettingsActivity : AppCompatActivity() {
    private lateinit var activitySettingsBinding: ActivitySettingsBinding
    private var newName:String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activitySettingsBinding = DataBindingUtil.setContentView(this, R.layout.activity_settings)
        setNavigation()
        doInitialization()
    }

    private fun doInitialization() {
        activitySettingsBinding.userName.text = SignInActivity.userName
        activitySettingsBinding.imageSignOut.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(applicationContext, SignInActivity::class.java))
        }
        activitySettingsBinding.editButton.setOnClickListener {
            activitySettingsBinding.userName.visibility = View.GONE
            activitySettingsBinding.editUserName.visibility = View.VISIBLE
        }
        activitySettingsBinding.confirmButton.setOnCLickListener{
            if(editUserName.text.toString().trim().isNotBlank()){
                newName =
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
                 R.id.map -> {startActivity(Intent(applicationContext, MapActivity::class.java))
                     overridePendingTransition(0, 0)
                     return@setOnNavigationItemSelectedListener true
                 }
            }
            false
        }
    }
}
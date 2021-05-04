package course.ru.qsearcher.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import course.ru.qsearcher.R
import course.ru.qsearcher.databinding.ActivityProfileBinding
import course.ru.qsearcher.model.User

class ProfileActivity : AppCompatActivity() {
    private lateinit var activityProfileBinding: ActivityProfileBinding
    private lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityProfileBinding = DataBindingUtil.setContentView(this, R.layout.activity_profile)
        initialize()
    }

    private fun initialize() {
        user = intent.getSerializableExtra("user") as User
        activityProfileBinding.viewFadingEdge.visibility = View.VISIBLE
        activityProfileBinding.sendMessageButton.setOnClickListener {
            val intent: Intent = Intent(applicationContext, ChatActivity::class.java).apply {
                putExtra("user", user)
            }
            startActivity(intent)
        }
        activityProfileBinding.imageBack.setOnClickListener { onBackPressed() }
        activityProfileBinding.nameText.text = user.name
        activityProfileBinding.emailText.text = user.email
    }
}
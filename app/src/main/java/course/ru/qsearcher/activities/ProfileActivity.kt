package course.ru.qsearcher.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import course.ru.qsearcher.R
import course.ru.qsearcher.databinding.ActivityProfileBinding
import course.ru.qsearcher.model.User
import java.lang.Exception

class ProfileActivity : AppCompatActivity() {
    private lateinit var activityProfileBinding: ActivityProfileBinding
    private lateinit var user: User
    private var storage: FirebaseStorage? = null
    private var storageRef: StorageReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityProfileBinding = DataBindingUtil.setContentView(this, R.layout.activity_profile)
        initialize()
    }
    private fun setAvatar(){
        storageRef?.child(user.email + "avatar")?.downloadUrl?.addOnSuccessListener {
            Picasso.get().load(it).noFade().into(activityProfileBinding.userImage, object :
                Callback {
                override fun onSuccess() {
                    //itemLayoutBinding?.avatar!!.animate().setDuration(300).alpha(1f).start()
                }

                override fun onError(e: Exception) {
                }
            })
            //activitySettingsBinding.userImage.setImageURI(uri)
        }?.addOnFailureListener {
            Log.i(
                "usersAdapter",
                "Не получилось загрузить аватар для " + user.email
            )
        }
    }
    private fun initialize() {
        storage = FirebaseStorage.getInstance()
        storageRef = storage?.reference?.child("avatars")
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
        setAvatar()
    }
}
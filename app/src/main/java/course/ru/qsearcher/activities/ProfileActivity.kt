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
                override fun onSuccess() {}
                override fun onError(e: Exception) {}
            })
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
        if(user.isEmailActivated){
            activityProfileBinding.emailText.text = user.email
        }else{
            //activityProfileBinding.emailText.text = "Не указано"
            activityProfileBinding.infoLayoutRow1.visibility = View.GONE
        }
        if(user.isNetworkActivated){
            activityProfileBinding.vkText.text = user.socialNetworkUrl
        }else{
            activityProfileBinding.infoLayoutRow2.visibility = View.GONE
        }
        if(user.isCityActivated){
            activityProfileBinding.city.text = "Город: "+user.city
        }else{
            activityProfileBinding.city.text = "Город: не указано"
        }
        if(user.isOccupationActivated){
            activityProfileBinding.occupation.text = "Деятельность: "+user.occupation
        }else{
            activityProfileBinding.occupation.text = "Деятальность: не указано"
        }
        activityProfileBinding.nameText.text = user.name

        setAvatar()
    }
}
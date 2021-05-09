package course.ru.qsearcher.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import com.google.firebase.database.*
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
    private var database: FirebaseDatabase? = null
    private var usersDbRef: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityProfileBinding = DataBindingUtil.setContentView(this, R.layout.activity_profile)
        initialize()
    }
    private fun setAvatar(){
        storageRef?.child(user.superId+ "avatar")?.downloadUrl?.addOnSuccessListener {
            Picasso.get().load(it).noFade().into(activityProfileBinding.userImage, object :
                Callback {
                override fun onSuccess() {}
                override fun onError(e: Exception) {}
            })
        }?.addOnFailureListener {
            Log.i(
                "userProfile",
                "Не получилось загрузить аватар для " + user.superId
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
        if(user.searchingCompany){
            activityProfileBinding.companyButton.visibility = View.VISIBLE
        }else{
//            val params = ConstraintLayout.LayoutParams(
//                ConstraintLayout.LayoutParams.WRAP_CONTENT,
//                ConstraintLayout.LayoutParams.WRAP_CONTENT
//            )
//            params.setMargins(dip(8),dip(60),dip(8),0)
//            activityProfileBinding.nameText.layoutParams = params
            activityProfileBinding.notCompanyButton.visibility = View.VISIBLE
        }
        if(user.friendsActivated) {
            if(!SignInActivity.currentUser.friends.contains(user.superId)){
                activityProfileBinding.addToFriends.visibility = View.VISIBLE
                activityProfileBinding.addToFriends.setOnClickListener {
                    database = FirebaseDatabase.getInstance()
                    usersDbRef = database?.reference?.child("users")
                    SignInActivity.currentUser.friends.add(user.superId!!)
                    usersDbRef?.child(SignInActivity.currentUser.superId!!)?.child("friends")?.setValue(SignInActivity.currentUser.friends)
                }
            }else{
                activityProfileBinding.addToFriends.visibility = View.GONE
            }
        }
        activityProfileBinding.nameText.text = user.name
        setAvatar()
    }
}
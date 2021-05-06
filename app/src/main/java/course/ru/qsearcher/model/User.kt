package course.ru.qsearcher.model

import android.util.Log
import androidx.room.Entity
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.gson.annotations.SerializedName
import course.ru.qsearcher.R
import kotlinx.android.synthetic.main.user_item.view.*
import java.io.Serializable

class User : Serializable, DatabaseReference.CompletionListener {
    var name: String? = null
    var email: String? = null
    var id: String? = null
    var avatarMock: Int = 0
    var favList: MutableList<Int>? = null
    var usersList:MutableList<String>?=null
    var occupation:String?=null
    var isOccupationActivated:Boolean = false
    var city:String?=null
    var isCityActivated:Boolean = false
    var socialNetworkUrl:String?=null
    var isNetworkActivated:Boolean = false
    var isEmailActivated:Boolean = false
    var superId:String?=null
    var searchingCompany:Boolean = false
    var friends:ArrayList<String> = ArrayList()
    var friendsActivated:Boolean = true
    override fun onComplete(error: DatabaseError?, ref: DatabaseReference) {
        Log.i("userClass","deleted")
    }
}
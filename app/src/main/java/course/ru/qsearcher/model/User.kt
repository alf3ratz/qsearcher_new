package course.ru.qsearcher.model

import androidx.room.Entity
import com.google.gson.annotations.SerializedName
import course.ru.qsearcher.R
import kotlinx.android.synthetic.main.user_item.view.*
import java.io.Serializable

class User : Serializable {
    @SerializedName("name")
    var name: String? = null

    @SerializedName("email")
    var email: String? = null

    @SerializedName("id")
    var id: String? = null

    @SerializedName("avatar")
    var avatarMock: Int = 0

    @SerializedName("favList")
    var favList: MutableList<Int>? = null
    @SerializedName("usersList")
    var usersList:MutableList<String>?=null
    @SerializedName("occupation")
    var occupation:String?=null
    @SerializedName("isOccupationActivated")
    var isOccupationActivated:Boolean = false
    @SerializedName("city")
    var city:String?=null
    @SerializedName("isCityActivated")
    var isCityActivated:Boolean = false
    @SerializedName("socialNetwork")
    var socialNetworkUrl:String?=null
    @SerializedName("isNetworkActivated")
    var isNetworkActivated:Boolean = false
    @SerializedName("isNetworkActivated")
    var isEmailActivated:Boolean = false
}
package course.ru.qsearcher.model

import androidx.room.Entity
import com.google.gson.annotations.SerializedName
import course.ru.qsearcher.R
import kotlinx.android.synthetic.main.user_item.view.*
import java.io.Serializable

class User:Serializable{
    @SerializedName("name")
    lateinit var name: String
    @SerializedName("email")
    lateinit var email: String
    @SerializedName("id")
    lateinit var id: String
    @SerializedName("avatar") var avatarMock: Int = 0

    @SerializedName("favList")
    lateinit var favList: MutableList<Int>
}
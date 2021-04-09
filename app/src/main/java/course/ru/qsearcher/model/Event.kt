package course.ru.qsearcher.model

import android.media.Image
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity(tableName = "events")
class Event : Serializable {
    @PrimaryKey
    @SerializedName("id")
    var id:Int = -1

    @SerializedName("favorites_count")
    var rating: String? = null

    @SerializedName("description")
    var description: String? = null

    @SerializedName("title")
    var name: String? = null

    @SerializedName("short_title")
    var shortTitle: String? = null

    @SerializedName("body_text")
    var bodyText: String? = null

    @SerializedName("site_url")
    var siteUrl: String? = null

    @SerializedName("lat")
    var lat: Double = 0.0

    @SerializedName("lon")
    var lon: Double = 0.0


    @SerializedName("images")
    var images: ArrayList<Image>? = null

    class Image {
        @SerializedName("image")
        var image: String? = null;
        override fun toString(): String = image!!
    }

}
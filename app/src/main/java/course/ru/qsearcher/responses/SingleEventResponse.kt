package course.ru.qsearcher.responses

import androidx.room.TypeConverters
import com.google.gson.annotations.SerializedName
import course.ru.qsearcher.model.Event
import course.ru.qsearcher.utilities.CustomTypeConverter
import java.io.Serializable

class SingleEventResponse {
    @SerializedName("id")
    var id: Int? = null

    @SerializedName("description")
    var description: String? = null

    @SerializedName("title")
    var name: String? = null

    @SerializedName("body_text")
    var bodyText: String? = null

    @SerializedName("site_url")
    var siteUrl: String? = null

    @SerializedName("short_title")
    var shortTitle: String? = null

    @SerializedName("images")
    var images: ArrayList<Event.Image>? = null

    @SerializedName("favorites_count")
    var rating: String? = null

}
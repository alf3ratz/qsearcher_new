package course.ru.qsearcher.model

import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.gson.annotations.SerializedName
import course.ru.qsearcher.utilities.CustomTypeConverter

class EventBySearching {
    @PrimaryKey
    @SerializedName("id")
    var id:Int = -1

    @SerializedName("favorites_count")
    var rating: String? = null
    @SerializedName("comments_count")
    var comments: String? = null

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
    @SerializedName("item_url")
    var itemUrl: String? = null

    @SerializedName("lat")
    var lat: Double = 0.0

    @SerializedName("lon")
    var lon: Double = 0.0

    @SerializedName("disable_comments")
    var disComm:Boolean = false

    @SerializedName("place")
    var places: ArrayList<Int>?=null

    @SerializedName("age_restriction")
    var age:Int = -1
    @SerializedName("images")
    @TypeConverters(CustomTypeConverter::class)
    var images: ArrayList<Event.Image>? = null
}
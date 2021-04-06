package course.ru.qsearcher.model

import android.media.Image
import com.google.gson.annotations.SerializedName

class Event {
    @SerializedName("title")
    var name:String?=null

    @SerializedName("short_title")
    var shortTitle:String?=null

    @SerializedName("body_text")
    var bodyText:String?=null

    @SerializedName("site_url")
    var siteUrl:String?=null

    @SerializedName("images")
    var images:ArrayList<Image>?=null

    class Image{
        @SerializedName("image")
        var image:String?=null;
        override fun toString(): String =image!!
    }

}
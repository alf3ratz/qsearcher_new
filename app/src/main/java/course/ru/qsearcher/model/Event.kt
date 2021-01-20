package course.ru.qsearcher.model

import com.google.gson.annotations.SerializedName

class Event {
//    @SerializedName("id")
//    var id:Int?=null

    @SerializedName("title")
    var name:String?=null

    @SerializedName("short_title")
    var shortTitle:String?=null

//    @SerializedName("country")
//    var country:String?=null
//
//    //@SerializedName("network")
//    @SerializedName("network")
//    var network:String?=null

   // @SerializedName("image_thumbnail_path")
//    @SerializedName("link")
//    var imagePath:String?=null

    @SerializedName("images")
    var images:List<Image>?=null

    class Image{
        @SerializedName("image")
        var image:String?=null;

    }

}
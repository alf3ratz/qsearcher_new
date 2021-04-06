package course.ru.qsearcher.responses

import com.google.gson.annotations.SerializedName
import course.ru.qsearcher.model.Event

class EventResponse {

    @SerializedName("count")
    var page:Int?=null

    @SerializedName("next")
    var totalPages:String?=null

    @SerializedName("results")
    var events:ArrayList<Event>?=null


    //fun getEvents():List<Event> =events
}
package course.ru.qsearcher.network

import course.ru.qsearcher.responses.EventResponse
import retrofit2.http.GET
import retrofit2.Call
import retrofit2.http.Query

interface ApiService {

    @GET("events/?fields=images,title,short_title")
    fun getMostPopularEvents(@Query("page") page: Int): Call<EventResponse>


}
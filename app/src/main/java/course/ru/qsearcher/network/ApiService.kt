package course.ru.qsearcher.network

import course.ru.qsearcher.responses.EventResponse
import retrofit2.http.GET
import retrofit2.Call
import retrofit2.http.Query

interface ApiService {

    @GET("events/?fields=images,title,short_title,body_text,site_url,favorites_count,description,id")
    fun getMostPopularEvents(@Query("page") page: Int): Call<EventResponse>
    @GET("https://kudago.com/public-api/v1.4/events/АЙДИ/?fields=images,title,short_title,body_text,site_url,favorites_count,description,id")
    fun getEventById(@Query("id") id:Int):Call<EventResponse>

    @GET("search")
    fun searchEvent(@Query("q") query: String, @Query("page") page: Int): Call<EventResponse>

}
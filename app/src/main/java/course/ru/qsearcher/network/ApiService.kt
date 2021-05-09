package course.ru.qsearcher.network

import course.ru.qsearcher.responses.EventResponse
import course.ru.qsearcher.responses.SingleEventResponse
import retrofit2.http.GET
import retrofit2.Call
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {


    @GET ("events/?fields=images,title,dates,short_title,body_text,site_url,favorites_count,description,id,place&expand=dates,place&order_by=-publication_date,-rank")
   fun getMostPopularEvents(@Query("page") page: Int): Call<EventResponse>

    @GET("events/{id}/?fields=images,title,short_title,body_text,site_url,favorites_count,description,id")
    fun getEventById(@Path("id") id:Int):Call<SingleEventResponse>

    @GET("search/?ctype=event&fields=images,title,short_title,body_text,site_url,favorites_count,description,id")
    fun searchEvent(@Query("q")query: String): Call<EventResponse>

    @GET("https://kudago.com/public-api/v1.4/events/?fields=images,title,is_free,categories,short_title,body_text,site_url,favorites_count,description,id,place&expand=dates,place&is_free=true&order_by=-publication_date,-rank")
    fun eventsWithSelectedCategories(@Query("categories")categories:String):Call<EventResponse>

}
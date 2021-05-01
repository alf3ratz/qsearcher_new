package course.ru.qsearcher.repositories

import androidx.annotation.NonNull
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import course.ru.qsearcher.network.ApiClient
import course.ru.qsearcher.network.ApiService
import course.ru.qsearcher.responses.EventResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MostPopularEventsRepository() {

    private lateinit var apiService: ApiService

    init {
        apiService = ApiClient.getRetrofit().create(ApiService::class.java)
    }

    fun getMostPopularEvents(page: Int): LiveData<EventResponse> {
        val data: MutableLiveData<EventResponse> = MutableLiveData()
        apiService.getMostPopularEvents(page).enqueue(object : Callback<EventResponse> {
            override fun onFailure(@NonNull call: Call<EventResponse>, t: Throwable) {
                data.setValue(null)
            }
            override fun onResponse(
                @NonNull call: Call<EventResponse>,
                @NonNull response: Response<EventResponse>
            ) {
                data.setValue(response.body())
            }
        })
        return data
    }
    fun getEventsById(id:Int): LiveData<EventResponse> {
        val data: MutableLiveData<EventResponse> = MutableLiveData()
        apiService.getEventById(id).enqueue(object : Callback<EventResponse> {
            override fun onFailure(@NonNull call: Call<EventResponse>, t: Throwable) {
                data.setValue(null)
            }
            override fun onResponse(
                @NonNull call: Call<EventResponse>,
                @NonNull response: Response<EventResponse>
            ) {
                data.setValue(response.body())
            }
        })
        return data
    }
}
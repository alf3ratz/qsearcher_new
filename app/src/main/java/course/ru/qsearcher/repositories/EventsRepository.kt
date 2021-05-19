package course.ru.qsearcher.repositories

import androidx.annotation.NonNull
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import course.ru.qsearcher.network.ApiClient
import course.ru.qsearcher.network.ApiService
import course.ru.qsearcher.responses.EventResponse
import course.ru.qsearcher.responses.SingleEventResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EventsRepository {

    private var apiService: ApiService = ApiClient.getRetrofit().create(ApiService::class.java)

    fun getMostPopularEvents(page: Int): LiveData<EventResponse> {
        val data: MutableLiveData<EventResponse> = MutableLiveData()
        apiService.getMostPopularEvents(page).enqueue(object : Callback<EventResponse> {
            override fun onFailure(@NonNull call: Call<EventResponse>, t: Throwable) {
                data.value = null
            }

            override fun onResponse(
                @NonNull call: Call<EventResponse>,
                @NonNull response: Response<EventResponse>
            ) {
                data.value = response.body()
            }
        })
        return data
    }

    fun getEventsById(id: Int): LiveData<SingleEventResponse> {
        val data: MutableLiveData<SingleEventResponse> = MutableLiveData()
        apiService.getEventById(id).enqueue(object : Callback<SingleEventResponse> {
            override fun onFailure(@NonNull call: Call<SingleEventResponse>, t: Throwable) {
                data.value = null
            }

            override fun onResponse(
                @NonNull call: Call<SingleEventResponse>,
                @NonNull response: Response<SingleEventResponse>
            ) {
                data.value = response.body()
            }
        })
        return data
    }

    fun eventsWithSelectedCategories(categories: String): LiveData<EventResponse> {
        val data: MutableLiveData<EventResponse> = MutableLiveData()
        apiService.eventsWithSelectedCategories(categories)
            .enqueue(object : Callback<EventResponse> {
                override fun onFailure(@NonNull call: Call<EventResponse>, t: Throwable) {
                    data.value = null
                }

                override fun onResponse(
                    @NonNull call: Call<EventResponse>,
                    @NonNull response: Response<EventResponse>
                ) {
                    data.value = response.body()
                }
            })
        return data
    }
}
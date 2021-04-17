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

class SearchEventRepository {
    private var apiService: ApiService? = null;

    init {
        apiService = ApiClient.getRetrofit().create(ApiService::class.java)
    }

    fun searchEvent(query: String, page: Int): LiveData<EventResponse> {
        var data: MutableLiveData<EventResponse> = MutableLiveData()
        apiService?.searchEvent(query,page)?.enqueue(object:Callback <EventResponse> {
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
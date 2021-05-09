package course.ru.qsearcher.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiClient {
    companion object {
        private var retrofit: Retrofit? = null
        fun getRetrofit(): Retrofit {
            if (retrofit == null)
                retrofit = Retrofit.Builder()
                    .baseUrl("https://kudago.com/public-api/v1.4/")//
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            return retrofit!!
        }
    }
}
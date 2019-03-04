package app.chatyar.com.ViewModel.Classes

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object APIClient {

    fun getClient(baseUrl: String): Retrofit {

        val retrofit: Retrofit = Retrofit.Builder()

            .baseUrl(baseUrl)

            .addConverterFactory(GsonConverterFactory.create())

            .build()

        return retrofit
    }


}
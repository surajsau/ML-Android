package `in`.surajsau.jisho.di

import `in`.surajsau.jisho.data.chat.ChatApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
abstract class ApiModule {

    companion object {

        @Provides
        fun provideChatApi(): ChatApi {
            val okhttp = OkHttpClient.Builder()
                .build()

            val retrofit = Retrofit.Builder()
                .client(okhttp)
                .baseUrl("https://api.kanye.rest/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(ChatApi::class.java)
        }
    }
}
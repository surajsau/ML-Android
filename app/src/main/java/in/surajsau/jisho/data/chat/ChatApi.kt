package `in`.surajsau.jisho.data.chat

import retrofit2.http.GET
import retrofit2.http.Url

interface ChatApi {

    @GET
    suspend fun randomKanye(@Url url: String): ChatResponse
}
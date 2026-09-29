package com.example.analisis_resultadosguia9

import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

data class GitHubRepository(
    val id: Long,
    val name: String,
    val description: String?,
    val language: String?,
    @SerializedName("html_url") val htmlUrl: String,
    @SerializedName("stargazers_count") val stars: Int
)

interface GitHubApi {
    @Headers("Accept: application/vnd.github+json", "User-Agent: DSM104-Android")
    @GET("users/{username}/repos")
    fun repositories(
        @Path("username") username: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int = 100,
        @Query("sort") sort: String = "full_name"
    ): Call<List<GitHubRepository>>
}

object RetrofitClient {
    val api: GitHubApi by lazy {
        Retrofit.Builder().baseUrl("https://api.github.com/")
            .addConverterFactory(GsonConverterFactory.create()).build()
            .create(GitHubApi::class.java)
    }
}

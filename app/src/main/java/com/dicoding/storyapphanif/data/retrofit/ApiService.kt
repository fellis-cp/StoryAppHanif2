package com.dicoding.storyapphanif.data.retrofit

import com.dicoding.storyapphanif.data.retrofit.response.LoginResponse
import com.dicoding.storyapphanif.data.retrofit.response.RegisterResponse
import com.dicoding.storyapphanif.data.retrofit.response.StoryResponse
import com.dicoding.storyapphanif.data.retrofit.response.UploadStoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query


interface ApiService {

    @Multipart
    @POST("stories")
    suspend fun uploadStory(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: RequestBody? = null,
        @Part("lon") lon: RequestBody? = null
    ): UploadStoryResponse


    @GET("stories")
    suspend fun getStory(
        @Header("Authorization") token: String,
    ): StoryResponse


    @FormUrlEncoded
    @POST("register")
    suspend fun registerStory(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): RegisterResponse

    @FormUrlEncoded
    @POST("login")
    suspend fun loginStory(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @GET("stories")
    suspend fun storyWithLocation(
        @Header("Authorization") token: String,
        @Query("location") location : Int = 1,
    ): StoryResponse

}
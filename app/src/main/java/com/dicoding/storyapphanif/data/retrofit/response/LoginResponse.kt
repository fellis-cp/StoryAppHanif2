package com.dicoding.storyapphanif.data.retrofit.response

import com.google.gson.annotations.SerializedName

data class LoginResponse(

    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String ,

    @field:SerializedName("loginResult")
    val loginResult: LoginResult


)

data class LoginResult(

    @field:SerializedName("token")
    val token: String,

    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("userId")
    val userId: String,


)

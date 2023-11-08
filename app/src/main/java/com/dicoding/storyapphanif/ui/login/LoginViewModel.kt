package com.dicoding.storyapphanif.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.storyapphanif.data.Result
import com.dicoding.storyapphanif.data.UserRepository
import com.dicoding.storyapphanif.data.retrofit.response.LoginResponse


class LoginViewModel(private val repository: UserRepository) : ViewModel() {
    private val _responseLogin = MediatorLiveData<Result<LoginResponse>>()
    val responseLogin : LiveData<Result<LoginResponse>> = _responseLogin


    fun login(email : String , password : String){
        val liveData = repository.loginStory(email, password)
        _responseLogin.addSource(liveData){result -> _responseLogin.value = result}
    }

}

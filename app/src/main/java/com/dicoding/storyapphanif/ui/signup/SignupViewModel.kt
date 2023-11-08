package com.dicoding.storyapphanif.ui.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.storyapphanif.data.Result
import com.dicoding.storyapphanif.data.UserRepository
import com.dicoding.storyapphanif.data.retrofit.response.RegisterResponse

class SignupViewModel (private val userRepository: UserRepository) : ViewModel(){
    private val _responseRegister  = MediatorLiveData<Result<RegisterResponse>> ()
    val responseRegister:LiveData<Result<RegisterResponse>> = _responseRegister


    fun register(name : String , email : String , password : String) {
        val liveData = userRepository.registerStory(name, email, password)
        _responseRegister.addSource(liveData) { result -> _responseRegister.value = result}

    }

}
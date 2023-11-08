package com.dicoding.storyapphanif.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.storyapphanif.data.Result
import com.dicoding.storyapphanif.data.UserRepository
import com.dicoding.storyapphanif.data.pref.UserModel
import com.dicoding.storyapphanif.data.retrofit.response.ListStoryItem
import kotlinx.coroutines.launch

class MainViewModel(private val repository: UserRepository) : ViewModel() {

    private val _storyList = MediatorLiveData<Result<List<ListStoryItem>>>()
    val storyList : LiveData<Result<List<ListStoryItem>>> = _storyList
    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }
    fun getStory(token: String) {
        val liveData = repository.getStory(token)
        _storyList.addSource(liveData) { result ->
            _storyList.value = result
        }
    }

}
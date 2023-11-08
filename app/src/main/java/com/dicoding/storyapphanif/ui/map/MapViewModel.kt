package com.dicoding.storyapphanif.ui.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.dicoding.storyapphanif.data.Result
import com.dicoding.storyapphanif.data.UserRepository
import com.dicoding.storyapphanif.data.pref.UserModel
import com.dicoding.storyapphanif.data.retrofit.response.ListStoryItem

class MapViewModel(private val repository: UserRepository) : ViewModel() {

    private val _storyWithLocation = MediatorLiveData<Result<List<ListStoryItem>>>()
    val storyWithLocation: LiveData<Result<List<ListStoryItem>>> = _storyWithLocation

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun storyWithLocation (token : String) {
        val liveData = repository.storyWithLocation(token)
            _storyWithLocation.addSource(liveData) { result ->
                _storyWithLocation.value = result
        }

    }
}
package com.dicoding.storyapphanif.di

import android.content.Context
import com.dicoding.storyapphanif.data.UserRepository
import com.dicoding.storyapphanif.data.pref.UserPreference
import com.dicoding.storyapphanif.data.pref.dataStore
import com.dicoding.storyapphanif.data.retrofit.ApiConfig


object Injection {
    fun provideRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService()
        return UserRepository.getInstance(pref , apiService)
    }
}
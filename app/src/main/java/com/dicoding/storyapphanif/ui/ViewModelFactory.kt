package com.dicoding.storyapphanif.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.storyapphanif.data.UserRepository
import com.dicoding.storyapphanif.di.Injection
import com.dicoding.storyapphanif.ui.login.LoginViewModel
import com.dicoding.storyapphanif.ui.main.MainViewModel
import com.dicoding.storyapphanif.ui.map.MapViewModel
import com.dicoding.storyapphanif.ui.signup.SignupViewModel
import com.dicoding.storyapphanif.ui.upload.UploadViewModel


class ViewModelFactory(private val repository: UserRepository) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MapViewModel::class.java) -> {
                MapViewModel(repository) as T
            }
            modelClass.isAssignableFrom(UploadViewModel::class.java) -> {
                UploadViewModel(repository) as T
            }
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(repository) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(repository) as T
            }
            modelClass.isAssignableFrom(SignupViewModel::class.java) -> {
                SignupViewModel(repository) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null
        @JvmStatic
        fun getInstance(context: Context): ViewModelFactory {
            if (INSTANCE == null) {
                synchronized(ViewModelFactory::class.java) {
                    INSTANCE = ViewModelFactory(Injection.provideRepository(context))
                }
            }
            return INSTANCE as ViewModelFactory
        }
    }
}
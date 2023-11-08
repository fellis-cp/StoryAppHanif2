package com.dicoding.storyapphanif.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.storyapphanif.R
import com.dicoding.storyapphanif.data.Result
import com.dicoding.storyapphanif.databinding.ActivityMainBinding
import com.dicoding.storyapphanif.ui.ViewModelFactory
import com.dicoding.storyapphanif.ui.adapter.StoryListAdapter
import com.dicoding.storyapphanif.ui.map.MapsActivity
import com.dicoding.storyapphanif.ui.upload.UploadActivity
import com.dicoding.storyapphanif.ui.welcome.WelcomeActivity

class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel
    private lateinit var adapter: StoryListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val layoutManager = LinearLayoutManager(this)
        binding.rvItemList.layoutManager = layoutManager

        val factory: ViewModelFactory = ViewModelFactory.getInstance(this)
        mainViewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]


        mainViewModel.storyList.observe(this) {
            when (it) {
                is Result.Loading -> loadingVisible(true)
                is Result.Error -> {
                    loadingVisible(false)
                }

                is Result.Success -> {
                    loadingVisible(false)
                    adapter = StoryListAdapter(it.data)
                    binding.rvItemList.adapter = adapter
                }

            }

        }

        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            } else {
                mainViewModel.getStory(user.token)
            }
        }

        menu()
        actionButton()

    }


    private fun loadingVisible(isLoading: Boolean) {
        binding.rvItemList.visibility = if (isLoading) View.GONE else View.VISIBLE
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun actionButton() {
        binding.fabAddItem.setOnClickListener {
            val intent = Intent(this, UploadActivity::class.java)
            startActivity(intent)
        }
    }

    private fun menu() {
        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_logout -> {
                    AlertDialog.Builder(this).apply {
                        setTitle(getString(R.string.logout_dialog))
                        setMessage(getString(R.string.logout_message))
                        setPositiveButton(getString(R.string.yes)) { _, _ ->
                            mainViewModel.logout()
                        }
                        setNegativeButton(getString(R.string.no)) { _, _ ->

                        }
                        create()
                        show()
                    }
                    true
                }
                R.id.menu_maps -> {
                    val intent = Intent(this, MapsActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }

}
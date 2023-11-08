package com.dicoding.storyapphanif.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.storyapphanif.R
import com.dicoding.storyapphanif.databinding.ActivityMainBinding
import com.dicoding.storyapphanif.ui.ViewModelFactory
import com.dicoding.storyapphanif.ui.adapter.StoryListAdapter
import com.dicoding.storyapphanif.ui.map.MapsActivity
import com.dicoding.storyapphanif.ui.upload.UploadActivity
import com.dicoding.storyapphanif.ui.welcome.WelcomeActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel
    private lateinit var adapter : StoryListAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadingVisible(isLoading = true)

        val factory: ViewModelFactory = ViewModelFactory.getInstance(this)
        mainViewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]




        layoutSetup()
        fetchSession()
        actionButton()
        menu()


    }

    private fun layoutSetup () {
        val layoutManager = LinearLayoutManager(this)
        binding.rvItemList.layoutManager = layoutManager
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

    private fun fetchSession() {
        mainViewModel.getSession().observe(this) { user ->
            val token = user.token
            if (!user.isLogin) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            }
            adapter = StoryListAdapter()
            binding.rvItemList.adapter = adapter
            mainViewModel.getStory(token).observe(this) {
                adapter.submitData(lifecycle, it)
            }
            loadingVisible(false)
        }
    }


}
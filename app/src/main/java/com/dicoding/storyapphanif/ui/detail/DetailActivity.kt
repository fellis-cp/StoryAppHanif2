package com.dicoding.storyapphanif.ui.detail

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.dicoding.storyapphanif.R
import com.dicoding.storyapphanif.data.retrofit.response.ListStoryItem
import com.dicoding.storyapphanif.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private lateinit var storyItems : ListStoryItem
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = getString(R.string.detail_title)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        storyItems = if (Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra("storyItems", ListStoryItem::class.java)!!
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("storyItems")!!
        }
        bind()
    }

    @Suppress("DEPRECATION")
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }


    private fun bind() {
        Glide
            .with(this)
            .load(storyItems.photoUrl)
            .fitCenter()
            .into(binding.ivProfilePhoto)

        binding.tvName.text = storyItems.name
        binding.tvDescription.text = storyItems.description
    }

}
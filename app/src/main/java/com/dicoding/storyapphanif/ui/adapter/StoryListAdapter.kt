package com.dicoding.storyapphanif.ui.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.storyapphanif.data.retrofit.response.ListStoryItem
import com.dicoding.storyapphanif.databinding.LayoutBinding
import com.dicoding.storyapphanif.ui.detail.DetailActivity

class StoryListAdapter(private val storyList: List<ListStoryItem>) : RecyclerView.Adapter<StoryListAdapter.StoryListViewHolder>() {

    inner class StoryListViewHolder(private val binding:  LayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindView(storyItem: ListStoryItem) {
            binding.tvDescription.text = storyItem.description
            binding.tvName.text = storyItem.name

            Glide.with(itemView.context)
                .load(storyItem.photoUrl)
                .fitCenter()
                .into(binding.ivStoryImage)

            binding.itemLayout.setOnClickListener {
                val intent = Intent(itemView.context, DetailActivity::class.java)
                intent.putExtra("storyItems", storyItem)


                val activityOptionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        itemView.context as Activity,
                        Pair(binding.ivStoryImage, "image"),
                        Pair(binding.tvName, "nama"),
                        Pair(binding.tvDescription, "deskripsi")
                    )
                itemView.context.startActivity(intent, activityOptionsCompat.toBundle())
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryListViewHolder {
        return StoryListViewHolder(
            LayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return storyList.size
    }

    override fun onBindViewHolder(holder: StoryListViewHolder, position: Int) {
        val storyItem = storyList[position]
        holder.bindView(storyItem)
    }
}

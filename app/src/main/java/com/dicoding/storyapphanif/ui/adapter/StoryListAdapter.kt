package com.dicoding.storyapphanif.ui.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.storyapphanif.data.retrofit.response.ListStoryItem
import com.dicoding.storyapphanif.databinding.LayoutBinding
import com.dicoding.storyapphanif.ui.detail.DetailActivity

class StoryListAdapter: PagingDataAdapter<ListStoryItem,StoryListAdapter.StoryListViewHolder>(DIFF_CALLBACK) {

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



    override fun onBindViewHolder(holder: StoryListViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bindView(data)
        }
    }
    companion object {
        val DIFF_CALLBACK = object: DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }

            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}

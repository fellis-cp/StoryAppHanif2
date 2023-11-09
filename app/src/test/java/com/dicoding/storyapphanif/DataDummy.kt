package com.dicoding.storyapphanif

import com.dicoding.storyapphanif.data.retrofit.response.ListStoryItem

object DataDummy {

    fun generateDummyStoryResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val story = ListStoryItem(
                id = "story-FvU4u0Vp2S3PMsFg",
                name = "Hanif Anggara",
                description = "ini testing angka ${i+1}.",
                photoUrl = "https://story-api.dicoding.dev/images/stories/photos-1641623658595_dummy-pic.png",
                createdAt = "2022-01-08T06:34:18.598Z",
                lat = -10.212,
                lon = -16.002
            )
            items.add(story)
        }
        return items
    }
}
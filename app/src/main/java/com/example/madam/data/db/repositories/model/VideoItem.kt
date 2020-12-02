package com.example.madam.data.db.repositories.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "videos")
data class VideoItem(
    @PrimaryKey
    val id: String,
    val created_at: String,
    val video_url: String,
    val username: String,
    val user_image_url: String,
)
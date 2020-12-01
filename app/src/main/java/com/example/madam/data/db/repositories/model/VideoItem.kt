package com.example.madam.data.db.repositories.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "videos")
data class VideoItem(
    @PrimaryKey val id: String,
    val video_src: String,
    val user_image_src: String,
    val user_name: String,
    val creation_date: String
)
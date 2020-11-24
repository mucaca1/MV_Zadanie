package com.example.madam.data.db.repositories.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "videos")
data class VideoItem(
    @PrimaryKey val id: String,
    val src: String
)
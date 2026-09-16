package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val surahNumber: Int,
    val surahNameAr: String,
    val ayahNumberInSurah: Int,
    val ayahText: String,
    val pageNumber: Int,
    val juzNumber: Int,
    val note: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "last_read")
data class LastReadEntity(
    @PrimaryKey val id: Int = 1, // Single row for current progress
    val surahNumber: Int,
    val surahNameAr: String,
    val ayahNumberInSurah: Int,
    val pageNumber: Int,
    val juzNumber: Int,
    val updatedAt: Long = System.currentTimeMillis()
)

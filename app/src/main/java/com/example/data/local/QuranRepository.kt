package com.example.data.local

import kotlinx.coroutines.flow.Flow

class QuranRepository(
    private val bookmarkDao: BookmarkDao,
    private val lastReadDao: LastReadDao
) {
    val allBookmarks: Flow<List<BookmarkEntity>> = bookmarkDao.getAllBookmarks()
    val lastRead: Flow<LastReadEntity?> = lastReadDao.getLastRead()

    fun isBookmarked(surahNumber: Int, ayahNumber: Int): Flow<Boolean> {
        return bookmarkDao.isBookmarked(surahNumber, ayahNumber)
    }

    suspend fun addBookmark(bookmark: BookmarkEntity) {
        bookmarkDao.insertBookmark(bookmark)
    }

    suspend fun clearAllBookmarks() {
        bookmarkDao.deleteAllBookmarks()
    }

    suspend fun removeBookmarkById(id: Int) {
        bookmarkDao.deleteBookmarkById(id)
    }

    suspend fun removeBookmark(surahNumber: Int, ayahNumber: Int) {
        bookmarkDao.deleteBookmarkBySurahAndAyah(surahNumber, ayahNumber)
    }

    suspend fun saveLastReadPosition(
        surahNumber: Int,
        surahNameAr: String,
        ayahNumberInSurah: Int,
        pageNumber: Int,
        juzNumber: Int
    ) {
        lastReadDao.saveLastRead(
            LastReadEntity(
                id = 1,
                surahNumber = surahNumber,
                surahNameAr = surahNameAr,
                ayahNumberInSurah = ayahNumberInSurah,
                pageNumber = pageNumber,
                juzNumber = juzNumber,
                updatedAt = System.currentTimeMillis()
            )
        )
    }
}

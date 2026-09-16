package com.example.ui.viewmodel

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.QuranAudioPlayer
import com.example.data.local.BookmarkEntity
import com.example.data.local.LastReadEntity
import com.example.data.local.QuranDatabase
import com.example.data.local.QuranRepository
import com.example.data.model.AudioPlaybackState
import com.example.data.model.Ayah
import com.example.data.model.Juz
import com.example.data.model.Reciter
import com.example.data.model.Surah
import com.example.data.quran.AyahSearchResult
import com.example.data.quran.QuranApi
import com.example.data.quran.QuranDataProvider
import com.example.data.quran.QuranPageMapper
import com.example.data.quran.QuranTextProvider
import com.example.ui.theme.QuranThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class AppTab(val titleAr: String, val icon: String) {
    READER("المصحف", "menu_book"),
    INDEX("الفهرس", "format_list_bulleted"),
    RECITERS("القراء", "headphones"),
    SEARCH("البحث", "search")
}

data class QuranUiState(
    val currentTab: AppTab = AppTab.READER,
    val currentPage: Int = 1,
    val selectedSurah: Surah = QuranDataProvider.surahs.first(),
    val selectedJuz: Juz? = null,
    val isOverlayVisible: Boolean = false,
    val isFullscreenReading: Boolean = false,
    val zoomScale: Float = 1.0f,
    val themeMode: QuranThemeMode = QuranThemeMode.DARK,
    val searchQuery: String = "",
    val searchResults: List<AyahSearchResult> = emptyList(),
    val selectedAyahForAction: Ayah? = null,
    val showTafsirDialog: Boolean = false,
    val showPageJumpDialog: Boolean = false,
    val showAudioPlayerSheet: Boolean = false,
    val showDuaaKhatmSheet: Boolean = false,
    val showReciterPickerSheet: Boolean = false,
    val dynamicAyahsForSurah: Map<Int, List<Ayah>> = emptyMap(),
    val isNightMode: Boolean = true
)

class QuranViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: QuranRepository
    val audioPlayer: QuranAudioPlayer = QuranAudioPlayer(application)

    private val _uiState = MutableStateFlow(QuranUiState())
    val uiState: StateFlow<QuranUiState> = _uiState.asStateFlow()

    val audioPlaybackState: StateFlow<AudioPlaybackState> = audioPlayer.playbackState

    val bookmarks: StateFlow<List<BookmarkEntity>>
    val lastRead: StateFlow<LastReadEntity?>

    init {
        val database = QuranDatabase.getDatabase(application)
        repository = QuranRepository(database.bookmarkDao(), database.lastReadDao())

        bookmarks = repository.allBookmarks.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        lastRead = repository.lastRead.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

        // Restore last read position if available
        viewModelScope.launch {
            repository.lastRead.collect { saved ->
                if (saved != null && _uiState.value.currentPage == 1 && saved.pageNumber > 1) {
                    val surah = QuranDataProvider.surahs.find { it.number == saved.surahNumber } ?: QuranDataProvider.surahs.first()
                    _uiState.update {
                        it.copy(
                            currentPage = saved.pageNumber.coerceIn(1, 604),
                            selectedSurah = surah
                        )
                    }
                }
            }
        }
    }

    fun setTab(tab: AppTab) {
        _uiState.update { it.copy(currentTab = tab) }
    }

    fun openPage(pageNumber: Int) {
        val validPage = pageNumber.coerceIn(1, 604)
        val surah = QuranPageMapper.getSurahForPage(validPage)
        _uiState.update {
            it.copy(
                currentPage = validPage,
                selectedSurah = surah,
                currentTab = AppTab.READER,
                showPageJumpDialog = false
            )
        }
        saveLastReadPage(validPage, surah)
        loadAyahsForSurah(surah.number)
    }

    fun openSurah(surah: Surah, targetAyahNumber: Int? = null) {
        _uiState.update {
            it.copy(
                selectedSurah = surah,
                currentPage = surah.pageNumber.coerceIn(1, 604),
                currentTab = AppTab.READER,
                selectedAyahForAction = null
            )
        }
        saveLastRead(surah, targetAyahNumber ?: 1)
        loadAyahsForSurah(surah.number)
    }

    fun openJuz(juz: Juz) {
        val page = juz.startPage.coerceIn(1, 604)
        val surah = QuranDataProvider.surahs.find { it.number == juz.startSurahNumber } ?: QuranPageMapper.getSurahForPage(page)
        _uiState.update {
            it.copy(
                selectedJuz = juz,
                selectedSurah = surah,
                currentPage = page,
                currentTab = AppTab.READER
            )
        }
        saveLastReadPage(page, surah)
    }

    fun toggleImmersiveOverlay() {
        _uiState.update { it.copy(isOverlayVisible = !it.isOverlayVisible) }
    }

    fun setOverlayVisible(visible: Boolean) {
        _uiState.update { it.copy(isOverlayVisible = visible) }
    }

    fun toggleFullscreenReading() {
        _uiState.update {
            val nextFullscreen = !it.isFullscreenReading
            it.copy(
                isFullscreenReading = nextFullscreen,
                isOverlayVisible = if (nextFullscreen) false else it.isOverlayVisible,
                zoomScale = if (nextFullscreen) 1.25f else 1.0f
            )
        }
    }

    fun setFullscreenReading(fullscreen: Boolean) {
        _uiState.update {
            it.copy(
                isFullscreenReading = fullscreen,
                zoomScale = if (fullscreen) 1.25f else 1.0f
            )
        }
    }

    fun setZoomScale(scale: Float) {
        _uiState.update { it.copy(zoomScale = scale.coerceIn(1.0f, 3.0f)) }
    }

    fun toggleNightMode() {
        _uiState.update {
            val nextNight = !it.isNightMode
            it.copy(
                isNightMode = nextNight,
                themeMode = if (nextNight) QuranThemeMode.DARK else QuranThemeMode.LIGHT
            )
        }
    }

    fun setThemeMode(mode: QuranThemeMode) {
        _uiState.update {
            it.copy(
                themeMode = mode,
                isNightMode = mode == QuranThemeMode.DARK
            )
        }
    }

    fun togglePageBookmark(pageNumber: Int) {
        val validPage = pageNumber.coerceIn(1, 604)
        val surah = QuranPageMapper.getSurahForPage(validPage)
        val juz = QuranPageMapper.getJuzForPage(validPage)
        viewModelScope.launch {
            val existing = bookmarks.value.find { it.pageNumber == validPage }
            if (existing != null) {
                repository.removeBookmarkById(existing.id)
            } else {
                // Delete previous bookmark so only the newest bookmark is kept
                repository.clearAllBookmarks()
                val newBookmark = BookmarkEntity(
                    surahNumber = surah.number,
                    surahNameAr = surah.nameAr,
                    ayahNumberInSurah = 1,
                    ayahText = "صفحة رقم $validPage - سورة ${surah.nameAr}",
                    pageNumber = validPage,
                    juzNumber = juz,
                    note = "علامة القراءة في صفحة $validPage (سورة ${surah.nameAr})"
                )
                repository.addBookmark(newBookmark)
            }
        }
    }

    fun isPageBookmarked(pageNumber: Int): Boolean {
        return bookmarks.value.any { it.pageNumber == pageNumber }
    }

    fun goToLastBookmark() {
        val last = bookmarks.value.lastOrNull()
        if (last != null) {
            openPage(last.pageNumber)
        }
    }

    fun setSearchQuery(query: String) {
        val results = if (query.isBlank()) emptyList() else QuranTextProvider.searchQuran(query)
        _uiState.update {
            it.copy(
                searchQuery = query,
                searchResults = results
            )
        }
    }

    fun selectAyahForAction(ayah: Ayah?) {
        _uiState.update { it.copy(selectedAyahForAction = ayah) }
    }

    fun setShowTafsirDialog(show: Boolean) {
        _uiState.update { it.copy(showTafsirDialog = show) }
    }

    fun setShowPageJumpDialog(show: Boolean) {
        _uiState.update { it.copy(showPageJumpDialog = show) }
    }

    fun setShowAudioPlayerSheet(show: Boolean) {
        _uiState.update { it.copy(showAudioPlayerSheet = show) }
    }

    fun setShowDuaaKhatmSheet(show: Boolean) {
        _uiState.update { it.copy(showDuaaKhatmSheet = show) }
    }

    fun setShowReciterPickerSheet(show: Boolean) {
        _uiState.update { it.copy(showReciterPickerSheet = show) }
    }

    fun toggleBookmark(surah: Surah, ayah: Ayah) {
        viewModelScope.launch {
            val existing = bookmarks.value.find {
                it.surahNumber == surah.number && it.ayahNumberInSurah == ayah.numberInSurah
            }
            if (existing != null) {
                repository.removeBookmarkById(existing.id)
            } else {
                // Delete previous bookmark so only the newest bookmark is kept
                repository.clearAllBookmarks()
                val newBookmark = BookmarkEntity(
                    surahNumber = surah.number,
                    surahNameAr = surah.nameAr,
                    ayahNumberInSurah = ayah.numberInSurah,
                    ayahText = ayah.text,
                    pageNumber = ayah.page,
                    juzNumber = ayah.juz,
                    note = "سورة ${surah.nameAr} - الآية ${ayah.numberInSurah}"
                )
                repository.addBookmark(newBookmark)
            }
        }
    }

    fun removeBookmark(bookmarkId: Int) {
        viewModelScope.launch {
            repository.removeBookmarkById(bookmarkId)
        }
    }

    fun saveLastReadPage(page: Int, surah: Surah) {
        val juz = QuranPageMapper.getJuzForPage(page)
        viewModelScope.launch {
            repository.saveLastReadPosition(
                surahNumber = surah.number,
                surahNameAr = surah.nameAr,
                ayahNumberInSurah = 1,
                pageNumber = page,
                juzNumber = juz
            )
        }
    }

    fun saveLastRead(surah: Surah, ayahNumber: Int = 1) {
        viewModelScope.launch {
            repository.saveLastReadPosition(
                surahNumber = surah.number,
                surahNameAr = surah.nameAr,
                ayahNumberInSurah = ayahNumber,
                pageNumber = surah.pageNumber,
                juzNumber = surah.juzNumber
            )
        }
    }

    fun playSurah(surah: Surah, reciter: Reciter = audioPlaybackState.value.currentReciter) {
        audioPlayer.playSurah(surah, reciter)
    }

    fun playAyah(surah: Surah, ayah: Ayah, reciter: Reciter = audioPlaybackState.value.currentReciter) {
        audioPlayer.playAyah(surah, ayah, reciter)
    }

    fun setReciter(reciter: Reciter) {
        audioPlayer.setReciter(reciter)
    }

    private fun loadAyahsForSurah(surahNumber: Int) {
        viewModelScope.launch {
            val ayahs = QuranApi.fetchSurahAyahs(surahNumber)
            if (ayahs != null && ayahs.isNotEmpty()) {
                _uiState.update { current ->
                    val updated = current.dynamicAyahsForSurah.toMutableMap()
                    updated[surahNumber] = ayahs
                    current.copy(dynamicAyahsForSurah = updated)
                }
            }
        }
    }

    fun copyAyahText(ayah: Ayah, surah: Surah) {
        val clipboard = getApplication<Application>().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(
            "Quran Ayah",
            "﴿ ${ayah.text} ﴾\n[سورة ${surah.nameAr}: ${ayah.numberInSurah}]"
        )
        clipboard.setPrimaryClip(clip)
    }

    fun shareAyahText(ayah: Ayah, surah: Surah) {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(
                Intent.EXTRA_TEXT,
                "قال الله تعالى:\n\n﴿ ${ayah.text} ﴾\n\n[سورة ${surah.nameAr} - الآية ${ayah.numberInSurah}]\n— تطبيق المصحف الشريف"
            )
            type = "text/plain"
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        val shareIntent = Intent.createChooser(sendIntent, "مشاركة الآية الكريمة").apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        getApplication<Application>().startActivity(shareIntent)
    }

    fun shareCurrentPage(pageNumber: Int) {
        val pageInfo = QuranPageMapper.getPageInfo(pageNumber)
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(
                Intent.EXTRA_TEXT,
                "أقرأ الآن في المصحف الشريف:\nصفحة $pageNumber (سورة ${pageInfo.surahNameAr} - ${pageInfo.juzNameAr})\n— تطبيق القرآن الكريم المدني"
            )
            type = "text/plain"
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        val shareIntent = Intent.createChooser(sendIntent, "مشاركة الصفحة").apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        getApplication<Application>().startActivity(shareIntent)
    }

    override fun onCleared() {
        super.onCleared()
        audioPlayer.destroy()
    }
}

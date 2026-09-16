package com.example

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.ui.components.FullAudioPlayerBottomSheet
import com.example.ui.components.MiniAudioPlayerBar
import com.example.ui.components.QuranBottomNavigationBar
import com.example.ui.components.QuranTopBar
import com.example.ui.screens.QuranIndexScreen
import com.example.ui.screens.QuranReaderScreen
import com.example.ui.screens.QuranSearchScreen
import com.example.ui.screens.RecitersScreen
import com.example.ui.theme.QuranAppTheme
import com.example.ui.theme.QuranThemeMode
import com.example.ui.viewmodel.AppTab
import com.example.ui.viewmodel.QuranViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: QuranViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Keep screen on while reading Quran
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        setContent {
            val uiState by viewModel.uiState.collectAsState()
            val audioState by viewModel.audioPlaybackState.collectAsState()
            val bookmarks by viewModel.bookmarks.collectAsState()
            val lastRead by viewModel.lastRead.collectAsState()

            // Quran is in Arabic (Right-to-Left RTL Layout Direction)
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                QuranAppTheme(themeMode = uiState.themeMode) {
                    Scaffold(
                        topBar = {
                            // In READER mode, the top bar is completely removed for clean authentic full-screen reading!
                            if (uiState.currentTab != AppTab.READER) {
                                QuranTopBar(
                                    title = when (uiState.currentTab) {
                                        AppTab.INDEX -> "فهرس القرآن الكريم"
                                        AppTab.RECITERS -> "أصوات القراء"
                                        AppTab.SEARCH -> "البحث في القرآن الكريم"
                                        else -> "المصحف الشريف"
                                    },
                                    themeMode = uiState.themeMode,
                                    audioState = audioState,
                                    onThemeToggleClick = {
                                        viewModel.toggleNightMode()
                                    },
                                    onAudioClick = {
                                        viewModel.setShowAudioPlayerSheet(true)
                                    },
                                    onSettingsClick = {
                                        viewModel.toggleNightMode()
                                    }
                                )
                            }
                        },
                        bottomBar = {
                            if (!uiState.isFullscreenReading || uiState.currentTab != AppTab.READER) {
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    MiniAudioPlayerBar(
                                        audioState = audioState,
                                        onBarClick = { viewModel.setShowAudioPlayerSheet(true) },
                                        onPlayPauseClick = { viewModel.audioPlayer.togglePlayPause() },
                                        onNextClick = { viewModel.audioPlayer.playNextSurah() },
                                        onCloseClick = { viewModel.audioPlayer.stop() }
                                    )
                                    QuranBottomNavigationBar(
                                        currentTab = uiState.currentTab,
                                        onTabSelected = { viewModel.setTab(it) }
                                    )
                                }
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(if (uiState.isFullscreenReading && uiState.currentTab == AppTab.READER) androidx.compose.foundation.layout.PaddingValues(0.dp) else innerPadding)
                        ) {
                            AnimatedContent(
                                targetState = uiState.currentTab,
                                transitionSpec = { fadeIn() togetherWith fadeOut() },
                                label = "quran_tab_animation"
                            ) { tab ->
                                when (tab) {
                                    AppTab.READER -> QuranReaderScreen(
                                        currentPage = uiState.currentPage,
                                        isNightMode = uiState.isNightMode,
                                        isOverlayVisible = uiState.isOverlayVisible,
                                        isFullscreen = uiState.isFullscreenReading,
                                        zoomScale = uiState.zoomScale,
                                        bookmarks = bookmarks,
                                        audioState = audioState,
                                        dynamicAyahsMap = uiState.dynamicAyahsForSurah,
                                        onPageChanged = { page -> viewModel.openPage(page) },
                                        onToggleOverlay = { viewModel.toggleImmersiveOverlay() },
                                        onToggleNightMode = { viewModel.toggleNightMode() },
                                        onToggleFullscreen = { viewModel.toggleFullscreenReading() },
                                        onToggleBookmark = { page -> viewModel.togglePageBookmark(page) },
                                        onGoToLastBookmark = { viewModel.goToLastBookmark() },
                                        onOpenIndex = { viewModel.setTab(AppTab.INDEX) },
                                        onOpenReciters = { viewModel.setTab(AppTab.RECITERS) },
                                        onSharePage = { page -> viewModel.shareCurrentPage(page) },
                                        onPlayAyah = { ayah ->
                                            val surah = com.example.data.quran.QuranDataProvider.surahs.find { it.number == ayah.surahNumber }
                                                ?: com.example.data.quran.QuranPageMapper.getSurahForPage(ayah.page)
                                            viewModel.playAyah(surah, ayah)
                                        },
                                        onCopyAyah = { ayah ->
                                            val surah = com.example.data.quran.QuranDataProvider.surahs.find { it.number == ayah.surahNumber }
                                                ?: com.example.data.quran.QuranPageMapper.getSurahForPage(ayah.page)
                                            viewModel.copyAyahText(ayah, surah)
                                        }
                                    )

                                    AppTab.INDEX -> QuranIndexScreen(
                                        bookmarks = bookmarks,
                                        onSelectSurah = { surah -> viewModel.openSurah(surah) },
                                        onSelectJuz = { juz -> viewModel.openJuz(juz) },
                                        onSelectPage = { page -> viewModel.openPage(page) },
                                        onRemoveBookmark = { id -> viewModel.removeBookmark(id) }
                                    )

                                    AppTab.RECITERS -> RecitersScreen(
                                        audioState = audioState,
                                        onSelectReciter = { reciter -> viewModel.setReciter(reciter) },
                                        onPlaySurahWithReciter = { surah, reciter ->
                                            viewModel.setReciter(reciter)
                                            viewModel.playSurah(surah, reciter)
                                        },
                                        onTogglePlayPause = { viewModel.audioPlayer.togglePlayPause() },
                                        onOpenFullPlayer = { viewModel.setShowAudioPlayerSheet(true) }
                                    )

                                    AppTab.SEARCH -> QuranSearchScreen(
                                        query = uiState.searchQuery,
                                        results = uiState.searchResults,
                                        onQueryChange = { viewModel.setSearchQuery(it) },
                                        onSelectResult = { surah, ayah ->
                                            viewModel.openPage(ayah.page)
                                        }
                                    )
                                }
                            }
                        }

                        // Full Audio Player Bottom Sheet
                        if (uiState.showAudioPlayerSheet) {
                            FullAudioPlayerBottomSheet(
                                audioState = audioState,
                                onDismiss = { viewModel.setShowAudioPlayerSheet(false) },
                                onPlayPauseClick = { viewModel.audioPlayer.togglePlayPause() },
                                onNextClick = { viewModel.audioPlayer.playNextSurah() },
                                onPrevClick = { viewModel.audioPlayer.playPreviousSurah() },
                                onSeekForward10s = { viewModel.audioPlayer.seekForward10s() },
                                onSeekBackward10s = { viewModel.audioPlayer.seekBackward10s() },
                                onSeekTo = { pos -> viewModel.audioPlayer.seekTo(pos) },
                                onSelectReciter = { reciter -> viewModel.setReciter(reciter) },
                                onPlaybackSpeedChange = { speed -> viewModel.audioPlayer.setPlaybackSpeed(speed) },
                                onToggleRepeat = { viewModel.audioPlayer.toggleRepeat() }
                            )
                        }
                    }
                }
            }
        }
    }
}

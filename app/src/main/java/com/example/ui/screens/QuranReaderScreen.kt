package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.BookmarkEntity
import com.example.data.model.AudioPlaybackState
import com.example.data.model.Ayah
import com.example.data.quran.QuranPageMapper
import com.example.data.quran.QuranTextProvider
import com.example.ui.components.DuaaKhatmBottomSheet
import com.example.ui.components.MushafPageComponent
import com.example.ui.components.PageAyahsChoiceDialog
import com.example.ui.components.PageJumpDialog
import com.example.ui.components.PageTafsirBottomSheet
import com.example.ui.components.TafsirDialog
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.ImmersiveBorder
import com.example.ui.theme.ImmersiveHeaderBg

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranReaderScreen(
    currentPage: Int,
    isNightMode: Boolean,
    isOverlayVisible: Boolean,
    isFullscreen: Boolean = false,
    zoomScale: Float = 1.0f,
    bookmarks: List<BookmarkEntity>,
    audioState: AudioPlaybackState,
    dynamicAyahsMap: Map<Int, List<Ayah>>,
    onPageChanged: (Int) -> Unit,
    onToggleOverlay: () -> Unit,
    onToggleNightMode: () -> Unit,
    onToggleFullscreen: () -> Unit,
    onToggleBookmark: (Int) -> Unit,
    onGoToLastBookmark: () -> Unit,
    onOpenIndex: () -> Unit,
    onOpenReciters: () -> Unit,
    onSharePage: (Int) -> Unit,
    onPlayAyah: (Ayah) -> Unit,
    onCopyAyah: (Ayah) -> Unit,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(
        initialPage = (currentPage - 1).coerceIn(0, 603),
        pageCount = { 604 }
    )

    var showJumpDialog by remember { mutableStateOf(false) }
    var showDuaaKhatm by remember { mutableStateOf(false) }
    var showMoreMenu by remember { mutableStateOf(false) }
    var showPageTafsir by remember { mutableStateOf(false) }

    // Sync pager with currentPage state when triggered externally
    LaunchedEffect(currentPage) {
        val targetIndex = (currentPage - 1).coerceIn(0, 603)
        if (pagerState.currentPage != targetIndex) {
            pagerState.scrollToPage(targetIndex)
        }
    }

    // Report page changes when user swipes
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { index ->
            val newPage = index + 1
            if (newPage != currentPage) {
                onPageChanged(newPage)
            }
        }
    }

    val pageInfo = remember(pagerState.currentPage) {
        QuranPageMapper.getPageInfo(pagerState.currentPage + 1)
    }

    val isCurrentPageBookmarked = remember(pagerState.currentPage, bookmarks) {
        bookmarks.any { it.pageNumber == pagerState.currentPage + 1 }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(if (isNightMode) Color(0xFF0F0F0F) else Color(0xFFF6F3EC))
    ) {
        // 604-Page Horizontal Pager
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { pageIndex ->
            val pageNum = pageIndex + 1
            val pageSurah = QuranPageMapper.getSurahForPage(pageNum)
            val isPageBookmarked = bookmarks.any { it.pageNumber == pageNum }
            val dynamicAyahs = dynamicAyahsMap[pageSurah.number]

            MushafPageComponent(
                pageNumber = pageNum,
                isBookmarked = isPageBookmarked,
                isNightMode = isNightMode,
                isFullscreen = isFullscreen,
                zoomScale = zoomScale,
                dynamicAyahs = dynamicAyahs,
                onPageClick = onToggleOverlay,
                onPageLongClick = {},
                onAyahClick = {},
                onAyahLongClick = {},
                onToggleZoom = onToggleFullscreen,
                modifier = Modifier.fillMaxSize()
            )
        }

        // Top Overlay Bar (Authentic slim floating controls when tapped)
        AnimatedVisibility(
            visible = isOverlayVisible,
            enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
            modifier = Modifier.align(Alignment.TopCenter)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .border(
                        1.dp,
                        if (isNightMode) EmeraldPrimary.copy(alpha = 0.35f) else Color(0xFFC5A059).copy(alpha = 0.5f),
                        RoundedCornerShape(22.dp)
                    ),
                color = if (isNightMode) Color(0xFF1E1E1E) else Color(0xFFFAF7F2),
                tonalElevation = 6.dp,
                shadowElevation = 4.dp
            ) {
                val topDefaultIconTint = if (isNightMode) EmeraldPrimary else Color(0xFF006C4C)
                val topGoldIconTint = if (isNightMode) GoldAccent else Color(0xFF8D6E14)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Surah & Juz title
                    Column {
                        Text(
                            text = "سورة ${pageInfo.surahNameAr}",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                fontFamily = FontFamily.Serif,
                                color = if (isNightMode) GoldAccent else Color(0xFF8D6E14)
                            )
                        )
                        Text(
                            text = "${pageInfo.juzNameAr} • صفحة ${pageInfo.pageNumber}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.sp,
                                color = if (isNightMode) Color(0xFF9EAFA9) else Color(0xFF556B62)
                            )
                        )
                    }

                    // Top Action Icons
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Page Tafsir button in Top Bar
                        IconButton(
                            onClick = { showPageTafsir = true },
                            modifier = Modifier.testTag("top_page_tafsir_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoStories,
                                contentDescription = "تفسير الصفحة",
                                tint = topGoldIconTint,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        // Night Mode Toggle
                        IconButton(onClick = onToggleNightMode) {
                            Icon(
                                imageVector = if (isNightMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                                contentDescription = "الوضع الصباحي/الليلي",
                                tint = if (isNightMode) GoldAccent else Color(0xFF8D6E14),
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        // Fullscreen / Zoom Toggle
                        IconButton(
                            onClick = onToggleFullscreen,
                            modifier = Modifier.testTag("top_fullscreen_toggle_btn")
                        ) {
                            Icon(
                                imageVector = if (isFullscreen) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                                contentDescription = if (isFullscreen) "تصغير الشاشة" else "ملء الشاشة",
                                tint = if (isFullscreen) GoldAccent else topDefaultIconTint,
                                modifier = Modifier.size(23.dp)
                            )
                        }

                        // Bookmark Toggle
                        IconButton(
                            onClick = { onToggleBookmark(pageInfo.pageNumber) },
                            modifier = Modifier.testTag("top_toggle_bookmark")
                        ) {
                            Icon(
                                imageVector = if (isCurrentPageBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "حفظ علامة",
                                tint = if (isCurrentPageBookmarked) Color(0xFFD32F2F) else topDefaultIconTint,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        // Share Page
                        IconButton(onClick = { onSharePage(pageInfo.pageNumber) }) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "مشاركة",
                                tint = topDefaultIconTint,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }

        // Bottom Overlay Toolbar
        AnimatedVisibility(
            visible = isOverlayVisible,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 12.dp)
                    .clip(RoundedCornerShape(26.dp))
                    .border(
                        1.dp,
                        if (isNightMode) EmeraldPrimary.copy(alpha = 0.35f) else Color(0xFFC5A059).copy(alpha = 0.5f),
                        RoundedCornerShape(26.dp)
                    ),
                color = if (isNightMode) Color(0xFF1E1E1E) else Color(0xFFFAF7F2),
                tonalElevation = 8.dp,
                shadowElevation = 6.dp
            ) {
                val defaultActionTint = if (isNightMode) EmeraldPrimary else Color(0xFF006C4C)
                val tafsirActionTint = if (isNightMode) GoldAccent else Color(0xFF8D6E14)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 1. Save Bookmark on current page
                    MushafBottomActionItem(
                        icon = if (isCurrentPageBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        label = if (isCurrentPageBookmarked) "علامة محفوظة" else "حفظ علامة",
                        tint = if (isCurrentPageBookmarked) Color(0xFFD32F2F) else defaultActionTint,
                        onClick = { onToggleBookmark(pageInfo.pageNumber) },
                        testTag = "mushaf_action_save_bookmark",
                        isNightMode = isNightMode
                    )

                    // 2. Go to Bookmark
                    MushafBottomActionItem(
                        icon = Icons.Default.Bookmarks,
                        label = "انتقال للعلامة",
                        tint = defaultActionTint,
                        onClick = onGoToLastBookmark,
                        testTag = "mushaf_action_goto_bookmark",
                        isNightMode = isNightMode
                    )

                    // 3. Page Tafsir (تفسير جميع آيات الصفحة)
                    MushafBottomActionItem(
                        icon = Icons.Default.AutoStories,
                        label = "تفسير الصفحة",
                        tint = tafsirActionTint,
                        onClick = { showPageTafsir = true },
                        testTag = "mushaf_action_page_tafsir",
                        isNightMode = isNightMode
                    )

                    // 4. Index
                    MushafBottomActionItem(
                        icon = Icons.Default.FormatListNumbered,
                        label = "الفهرس",
                        tint = defaultActionTint,
                        onClick = onOpenIndex,
                        testTag = "mushaf_action_index",
                        isNightMode = isNightMode
                    )

                    // 5. Pages Jump
                    MushafBottomActionItem(
                        icon = Icons.Default.MenuBook,
                        label = "الصفحات",
                        tint = defaultActionTint,
                        onClick = { showJumpDialog = true },
                        testTag = "mushaf_action_pages",
                        isNightMode = isNightMode
                    )

                    // 6. Reciters
                    MushafBottomActionItem(
                        icon = Icons.Default.Headphones,
                        label = "القراء",
                        tint = defaultActionTint,
                        onClick = onOpenReciters,
                        testTag = "mushaf_action_reciters",
                        isNightMode = isNightMode
                    )

                    // 7. More (Duaa Khatm, Fullscreen, etc.)
                    Box {
                        MushafBottomActionItem(
                            icon = Icons.Default.MoreVert,
                            label = "المزيد",
                            tint = defaultActionTint,
                            onClick = { showMoreMenu = true },
                            testTag = "mushaf_action_more",
                            isNightMode = isNightMode
                        )

                        DropdownMenu(
                            expanded = showMoreMenu,
                            onDismissRequest = { showMoreMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("تفسير الصفحة الحالية") },
                                leadingIcon = {
                                    Icon(Icons.Default.AutoStories, contentDescription = null, tint = tafsirActionTint)
                                },
                                onClick = {
                                    showMoreMenu = false
                                    showPageTafsir = true
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("دعاء ختم القرآن الكريم") },
                                leadingIcon = {
                                    Icon(Icons.Default.MenuBook, contentDescription = null, tint = defaultActionTint)
                                },
                                onClick = {
                                    showMoreMenu = false
                                    showDuaaKhatm = true
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(if (isFullscreen) "تصغير الشاشة" else "تكبير الشاشة") },
                                leadingIcon = {
                                    Icon(
                                        if (isFullscreen) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                                        contentDescription = null,
                                        tint = defaultActionTint
                                    )
                                },
                                onClick = {
                                    showMoreMenu = false
                                    onToggleFullscreen()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("مشاركة الصفحة الحالية") },
                                leadingIcon = {
                                    Icon(Icons.Default.Share, contentDescription = null, tint = defaultActionTint)
                                },
                                onClick = {
                                    showMoreMenu = false
                                    onSharePage(pageInfo.pageNumber)
                                }
                            )
                        }
                    }
                }
            }
        }

        // Floating Fullscreen / Zoom Button
        AnimatedVisibility(
            visible = isOverlayVisible || isFullscreen,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(
                    end = 16.dp,
                    bottom = if (isOverlayVisible) 92.dp else 24.dp
                )
        ) {
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .border(
                        width = 1.5.dp,
                        color = if (isFullscreen) GoldAccent else if (isNightMode) GoldAccent.copy(alpha = 0.7f) else EmeraldPrimary,
                        shape = RoundedCornerShape(24.dp)
                    )
                    .clickable(onClick = onToggleFullscreen)
                    .testTag("floating_zoom_toggle_btn"),
                color = if (isFullscreen) GoldAccent else MaterialTheme.colorScheme.surfaceVariant,
                shadowElevation = 8.dp,
                tonalElevation = 6.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = if (isFullscreen) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                        contentDescription = if (isFullscreen) "تصغير الشاشة" else "تكبير الشاشة",
                        tint = if (isFullscreen) Color.Black else if (isNightMode) GoldAccent else EmeraldPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isFullscreen) "تصغير الشاشة" else "تكبير الشاشة",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = if (isFullscreen) Color.Black else if (isNightMode) Color.White else EmeraldPrimary
                        )
                    )
                }
            }
        }
    }

    // Page Jump Dialog
    if (showJumpDialog) {
        PageJumpDialog(
            currentPage = pageInfo.pageNumber,
            onDismiss = { showJumpDialog = false },
            onJumpToPage = { targetPage ->
                showJumpDialog = false
                onPageChanged(targetPage)
            }
        )
    }

    // Duaa Khatm Sheet
    if (showDuaaKhatm) {
        DuaaKhatmBottomSheet(
            onDismiss = { showDuaaKhatm = false }
        )
    }

    // Full Page Tafsir Sheet (تفسير كافة آيات الصفحة)
    if (showPageTafsir) {
        PageTafsirBottomSheet(
            pageNumber = pageInfo.pageNumber,
            audioState = audioState,
            onPlayAyah = { ayah, surah ->
                onPlayAyah(ayah)
            },
            onDismiss = { showPageTafsir = false }
        )
    }
}

@Composable
fun MushafBottomActionItem(
    icon: ImageVector,
    label: String,
    tint: Color,
    onClick: () -> Unit,
    testTag: String,
    isNightMode: Boolean = true,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 4.dp, vertical = 6.dp)
            .testTag(testTag),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = tint,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (isNightMode) Color(0xFFECEFF1) else Color(0xFF1B2E24)
            ),
            maxLines = 1
        )
    }
}

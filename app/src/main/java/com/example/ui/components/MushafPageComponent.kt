package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest
import com.example.data.model.Ayah
import com.example.data.quran.QuranDataProvider
import com.example.data.quran.QuranPageMapper
import com.example.data.quran.QuranTextProvider
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.ImmersiveBorder
import com.example.ui.theme.ImmersiveTextMuted

@Composable
fun MushafPageComponent(
    pageNumber: Int,
    isBookmarked: Boolean,
    isNightMode: Boolean,
    isFullscreen: Boolean = false,
    zoomScale: Float = 1.0f,
    dynamicAyahs: List<Ayah>?,
    onPageClick: () -> Unit,
    onPageLongClick: (Ayah) -> Unit = {},
    onAyahClick: (Ayah) -> Unit,
    onAyahLongClick: (Ayah) -> Unit = onAyahClick,
    onToggleZoom: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val pageInfo = remember(pageNumber) {
        QuranPageMapper.getPageInfo(pageNumber)
    }

    val context = LocalContext.current
    val imageUrl = remember(pageNumber) { QuranPageMapper.getPageImageUrl(pageNumber) }

    val pageBgColor = if (isNightMode) Color(0xFF121212) else Color(0xFFFFFDF8)
    val pageTextColor = if (isNightMode) Color(0xFFE2E2E2) else Color(0xFF1E1E1E)
    val frameBorderColor = if (isNightMode) GoldAccent.copy(alpha = 0.4f) else GoldAccent.copy(alpha = 0.8f)

    val pageAyahs = remember(pageNumber, dynamicAyahs) {
        QuranPageMapper.getAyahsForPage(pageNumber, dynamicAyahs?.let { mapOf(pageInfo.surahNumber to it) } ?: emptyMap())
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(if (isNightMode) Color(0xFF0A0A0A) else Color(0xFFF3EFE6))
            .testTag("mushaf_page_$pageNumber")
    ) {
        // Main Mushaf Page Card (Framed authentic page or edge-to-edge full screen)
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = if (isFullscreen) 0.dp else 8.dp,
                    vertical = if (isFullscreen) 0.dp else 4.dp
                )
                .clip(RoundedCornerShape(if (isFullscreen) 0.dp else 8.dp))
                .border(
                    width = if (isFullscreen) 0.dp else 1.5.dp,
                    color = frameBorderColor,
                    shape = RoundedCornerShape(if (isFullscreen) 0.dp else 8.dp)
                ),
            color = pageBgColor,
            tonalElevation = if (isFullscreen) 0.dp else 2.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        horizontal = if (isFullscreen) 0.dp else 10.dp,
                        vertical = if (isFullscreen) 0.dp else 6.dp
                    )
            ) {
                if (!isFullscreen) {
                    // Top Header on the page: [Surah name | Page number | Juz name]
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                            .clickable { onPageClick() },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "سُورَةُ ${pageInfo.surahNameAr}",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                fontFamily = FontFamily.Serif,
                                color = if (isNightMode) GoldAccent else EmeraldPrimary
                            )
                        )

                        Text(
                            text = "${pageInfo.pageNumber}",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = pageTextColor
                            )
                        )

                        Text(
                            text = pageInfo.juzNameAr,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                fontFamily = FontFamily.Serif,
                                color = if (isNightMode) GoldAccent else EmeraldPrimary
                            )
                        )
                    }

                    // Decorative horizontal line
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(frameBorderColor.copy(alpha = 0.5f))
                    )

                    Spacer(modifier = Modifier.height(2.dp))
                }

                // Page Content: High quality Madani page image with smooth vertical scrolling in landscape & full screen fit
                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    val isLandscape = maxWidth > maxHeight
                    val scrollState = rememberScrollState()

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .then(
                                if (isLandscape) Modifier.verticalScroll(scrollState) else Modifier
                            )
                            .pointerInput(pageNumber) {
                                detectTapGestures(
                                    onTap = {
                                        // Tapping anywhere on the page smoothly toggles overlay controls
                                        onPageClick()
                                    },
                                    onDoubleTap = {
                                        onToggleZoom()
                                    }
                                )
                            },
                        contentAlignment = if (isLandscape) Alignment.TopCenter else Alignment.Center
                    ) {
                        val imageScale = when {
                            isLandscape -> ContentScale.FillWidth
                            isFullscreen -> ContentScale.FillBounds
                            else -> ContentScale.Fit
                        }

                        SubcomposeAsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(imageUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = "صفحة المصحف $pageNumber",
                            contentScale = imageScale,
                            colorFilter = if (isNightMode) {
                                // Night mode color matrix to invert page and make it dark with glowing white/gold script
                                val nightMatrix = ColorMatrix(
                                    floatArrayOf(
                                        -1f, 0f, 0f, 0f, 240f,
                                        0f, -1f, 0f, 0f, 240f,
                                        0f, 0f, -1f, 0f, 240f,
                                        0f, 0f, 0f, 1f, 0f
                                    )
                                )
                                ColorFilter.colorMatrix(nightMatrix)
                            } else null,
                            modifier = if (isLandscape) {
                                Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight()
                            } else {
                                Modifier.fillMaxSize()
                            }
                        ) {
                            val state = painter.state
                            when (state) {
                                is AsyncImagePainter.State.Loading -> {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .then(if (isLandscape) Modifier.height(300.dp) else Modifier.fillMaxHeight()),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            CircularProgressIndicator(
                                                color = EmeraldPrimary,
                                                strokeWidth = 2.5.dp,
                                                modifier = Modifier.size(36.dp)
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                text = "جاري تحميل صفحة ${pageInfo.pageNumber}...",
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    color = ImmersiveTextMuted,
                                                    fontSize = 11.sp
                                                )
                                            )
                                        }
                                    }
                                }
                                is AsyncImagePainter.State.Error -> {
                                    // High quality authentic digital fallback rendering
                                    MushafPageFallbackView(
                                        pageInfo = pageInfo,
                                        isNightMode = isNightMode,
                                        dynamicAyahs = pageAyahs.ifEmpty { dynamicAyahs },
                                        onAyahClick = onAyahClick,
                                        onAyahLongClick = onAyahLongClick
                                    )
                                }
                                else -> {
                                    SubcomposeAsyncImageContent()
                                }
                            }
                        }
                    }
                }

                if (!isFullscreen) {
                    // Bottom Page Sub-bar
                    Spacer(modifier = Modifier.height(2.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(frameBorderColor.copy(alpha = 0.5f))
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                            .clickable { onPageClick() },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "الحزب ${pageInfo.hizbNumber}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 10.sp,
                                color = ImmersiveTextMuted
                            )
                        )
                        Text(
                            text = "المصحف المدني الشريف",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 10.sp,
                                color = if (isNightMode) GoldAccent.copy(alpha = 0.8f) else GoldAccent,
                                fontFamily = FontFamily.Serif
                            )
                        )
                    }
                }
            }
        }

        // Bookmark Ribbon on top start (Slim authentic Quranic bookmark tab that does not cover text)
        if (isBookmarked) {
            Surface(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = if (isFullscreen) 16.dp else 24.dp)
                    .width(22.dp)
                    .height(32.dp),
                shape = RoundedCornerShape(bottomStart = 6.dp, bottomEnd = 6.dp),
                color = Color(0xFFC62828),
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent),
                shadowElevation = 3.dp
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Bookmark,
                        contentDescription = "علامة القراءة",
                        tint = GoldAccent,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun MushafPageFallbackView(
    pageInfo: com.example.data.quran.QuranPageInfo,
    isNightMode: Boolean,
    dynamicAyahs: List<Ayah>?,
    onAyahClick: (Ayah) -> Unit,
    onAyahLongClick: (Ayah) -> Unit = onAyahClick,
    modifier: Modifier = Modifier
) {
    val surahAyahs = dynamicAyahs ?: QuranTextProvider.getAyahsForSurah(pageInfo.surahNumber)
    val textColor = if (isNightMode) Color(0xFFF0F0F0) else Color(0xFF1B1B1B)
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Ornate Surah Frame if opening page
        if (pageInfo.pageNumber == QuranDataProvider.surahs.find { it.number == pageInfo.surahNumber }?.pageNumber) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isNightMode) EmeraldDark else EmeraldLight)
                    .border(1.dp, GoldAccent, RoundedCornerShape(12.dp))
                    .padding(vertical = 8.dp, horizontal = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "سُورَةُ ${pageInfo.surahNameAr}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        fontFamily = FontFamily.Serif,
                        color = if (isNightMode) GoldAccent else EmeraldPrimary
                    )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Basmalah (except for Surah 9 At-Tawbah)
            if (pageInfo.surahNumber != 9) {
                Text(
                    text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = if (isNightMode) GoldAccent else Color(0xFF1B1B1B)
                    )
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
        }

        // Continuous Flow of Ayahs
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            surahAyahs.forEach { ayah ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .pointerInput(ayah) {
                            detectTapGestures(
                                onTap = { onAyahClick(ayah) },
                                onLongPress = { onAyahLongClick(ayah) }
                            )
                        }
                        .padding(vertical = 4.dp, horizontal = 6.dp)
                ) {
                    Text(
                        text = "${ayah.text} ﴿${toArabicDigits(ayah.numberInSurah)}﴾",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = 19.sp,
                            lineHeight = 36.sp,
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Medium,
                            color = textColor,
                            textAlign = TextAlign.Justify
                        )
                    )
                }
            }
        }
    }
}

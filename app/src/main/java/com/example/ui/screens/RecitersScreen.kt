package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AudioPlaybackState
import com.example.data.model.Reciter
import com.example.data.model.Surah
import com.example.data.quran.QuranDataProvider
import com.example.data.quran.QuranTextProvider
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent

@Composable
fun RecitersScreen(
    audioState: AudioPlaybackState,
    onSelectReciter: (Reciter) -> Unit,
    onPlaySurahWithReciter: (Surah, Reciter) -> Unit,
    onTogglePlayPause: () -> Unit,
    onOpenFullPlayer: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedStyleFilter by remember { mutableStateOf("الكل") }
    var expandedReciterId by remember { mutableStateOf(audioState.currentReciter.id) }

    val styleOptions = listOf("الكل", "مرتل", "مجود")
    val isDark = MaterialTheme.colorScheme.background.red < 0.25f

    val filteredReciters = remember(searchQuery, selectedStyleFilter) {
        QuranDataProvider.reciters.filter { reciter ->
            val matchesStyle = if (selectedStyleFilter == "الكل") true else reciter.style == selectedStyleFilter
            val matchesSearch = if (searchQuery.isBlank()) true else {
                val norm = QuranTextProvider.normalizeArabic(searchQuery).lowercase()
                QuranTextProvider.normalizeArabic(reciter.nameAr).lowercase().contains(norm) ||
                        reciter.nameEn.lowercase().contains(norm)
            }
            matchesStyle && matchesSearch
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Hero Header Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clip(RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(EmeraldPrimary, EmeraldDark)
                        )
                    )
                    .padding(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Headphones,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "أصوات مشاهير القراء",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 18.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "استمع للقرآن الكريم كاملاً بأعذب التلاوات الخاشعة بجودة عالية",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color.White.copy(alpha = 0.95f),
                                fontSize = 12.sp
                            )
                        )
                    }
                }
            }
        }

        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = {
                Text(
                    text = "ابحث عن اسم القارئ...",
                    color = if (isDark) Color(0xFF88A096) else Color(0xFF5E796F),
                    fontSize = 13.sp
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = if (isDark) EmeraldPrimary else Color(0xFF006C4C),
                    modifier = Modifier.size(20.dp)
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(18.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (isDark) EmeraldPrimary else Color(0xFF006C4C),
                unfocusedBorderColor = if (isDark) Color(0xFF2B443A) else Color(0xFFB8CEC5),
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .testTag("reciters_search_field")
        )

        // Filter chips: All, Murattal, Mujawwad (Pill Style)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            styleOptions.forEach { style ->
                val isSelected = selectedStyleFilter == style
                val chipBg = if (isSelected) {
                    if (isDark) EmeraldPrimary else Color(0xFF006C4C)
                } else {
                    if (isDark) Color(0xFF1B2B24) else Color(0xFFEAEFE9)
                }
                val chipTextColor = if (isSelected) {
                    Color.White
                } else {
                    if (isDark) Color(0xFFB0C4BC) else Color(0xFF2B4E41)
                }
                val chipBorder = if (isSelected) {
                    if (isDark) EmeraldPrimary else Color(0xFF006C4C)
                } else {
                    if (isDark) Color(0xFF2B443A) else Color(0xFFCDDFD6)
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(chipBg)
                        .border(1.dp, chipBorder, RoundedCornerShape(50))
                        .clickable { selectedStyleFilter = style }
                        .padding(horizontal = 16.dp, vertical = 7.dp)
                        .testTag("reciter_filter_$style"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = style,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = chipTextColor
                        )
                    )
                }
            }
        }

        // Reciters List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .testTag("reciters_list")
        ) {
            items(filteredReciters, key = { it.id }) { reciter ->
                val isCurrentReciter = reciter.id == audioState.currentReciter.id
                val isExpanded = expandedReciterId == reciter.id

                val cardBg = if (isDark) Color(0xFF14201B) else Color(0xFFFAFBF9)
                val cardBorder = if (isCurrentReciter) {
                    if (isDark) EmeraldPrimary else Color(0xFF006C4C)
                } else {
                    if (isDark) Color(0xFF263D33) else Color(0xFFD2E0D8)
                }

                Surface(
                    shape = RoundedCornerShape(22.dp),
                    color = cardBg,
                    shadowElevation = if (isDark) 2.dp else 1.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .border(
                            width = if (isCurrentReciter) 2.dp else 1.dp,
                            color = cardBorder,
                            shape = RoundedCornerShape(22.dp)
                        )
                        .testTag("reciter_card_${reciter.id}")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onSelectReciter(reciter)
                                    expandedReciterId = if (isExpanded) "" else reciter.id
                                },
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Reciter Avatar
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isCurrentReciter) (if (isDark) EmeraldPrimary else Color(0xFF006C4C))
                                        else if (isDark) Color(0xFF1E352B)
                                        else Color(0xFFE2EFE9)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Headphones,
                                    contentDescription = null,
                                    tint = if (isCurrentReciter) Color.White else (if (isDark) Color(0xFF8CE0C0) else Color(0xFF006C4C)),
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = reciter.nameAr,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    )
                                    if (isCurrentReciter) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = "القارئ المختار",
                                            tint = if (isDark) EmeraldPrimary else Color(0xFF006C4C),
                                            modifier = Modifier.size(17.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(2.dp))

                                Text(
                                    text = "رواية ${reciter.riwayah} • مصحف ${reciter.style}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = if (isCurrentReciter) {
                                            if (isDark) EmeraldPrimary else Color(0xFF006C4C)
                                        } else {
                                            if (isDark) Color(0xFF98B3A8) else Color(0xFF456658)
                                        },
                                        fontSize = 12.sp,
                                        fontWeight = if (isCurrentReciter) FontWeight.Bold else FontWeight.Medium
                                    )
                                )
                            }

                            // Play/Toggle button if current reciter
                            if (isCurrentReciter && audioState.isPlaying) {
                                IconButton(
                                    onClick = onTogglePlayPause,
                                    modifier = Modifier
                                        .size(38.dp)
                                        .testTag("reciter_toggle_play_${reciter.id}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Pause,
                                        contentDescription = "إيقاف مؤقت",
                                        tint = if (isDark) EmeraldPrimary else Color(0xFF006C4C),
                                        modifier = Modifier.size(26.dp)
                                    )
                                }
                            }
                        }

                        // Bio
                        Text(
                            text = reciter.bioAr,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = if (isDark) Color(0xFFB0C5BD) else Color(0xFF385346),
                                fontSize = 12.sp,
                                lineHeight = 18.sp,
                                fontWeight = FontWeight.Normal
                            ),
                            modifier = Modifier.padding(top = 8.dp)
                        )

                        // Surah Quick Selector for this Reciter
                        AnimatedVisibility(visible = isExpanded) {
                            Column(modifier = Modifier.padding(top = 12.dp)) {
                                Text(
                                    text = "اختر سورة للاستماع بصوت ${reciter.nameAr}:",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isDark) GoldAccent else Color(0xFF006C4C)
                                    )
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    items(QuranDataProvider.surahs.take(20)) { surah ->
                                        val pillBg = if (isDark) Color(0xFF1B2B24) else Color(0xFFEAF3EE)
                                        val pillBorder = if (isDark) Color(0xFF2D463B) else Color(0xFFBBD7C9)
                                        val pillIconTint = if (isDark) EmeraldPrimary else Color(0xFF006C4C)
                                        val pillTextColor = if (isDark) Color(0xFFE2F0EA) else Color(0xFF123425)

                                        Surface(
                                            shape = RoundedCornerShape(12.dp),
                                            color = pillBg,
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(12.dp))
                                                .border(1.dp, pillBorder, RoundedCornerShape(12.dp))
                                                .clickable { onPlaySurahWithReciter(surah, reciter) }
                                                .testTag("reciter_${reciter.id}_surah_${surah.number}")
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.PlayArrow,
                                                    contentDescription = null,
                                                    tint = pillIconTint,
                                                    modifier = Modifier.size(15.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = surah.nameAr,
                                                    style = MaterialTheme.typography.bodySmall.copy(
                                                        fontSize = 12.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = pillTextColor
                                                    )
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

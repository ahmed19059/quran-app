package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AudioPlaybackState
import com.example.data.model.Reciter
import com.example.data.quran.QuranDataProvider
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.ImmersiveBorder
import com.example.ui.theme.ImmersiveHeaderBg
import com.example.ui.theme.ImmersiveTextMuted
import com.example.ui.theme.OnEmeraldContainer

@Composable
fun MiniAudioPlayerBar(
    audioState: AudioPlaybackState,
    onBarClick: () -> Unit,
    onPlayPauseClick: () -> Unit,
    onNextClick: () -> Unit,
    onCloseClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = MaterialTheme.colorScheme.background.red < 0.2f

    AnimatedVisibility(
        visible = audioState.currentSurah != null,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it }),
        modifier = modifier
    ) {
        val surah = audioState.currentSurah ?: return@AnimatedVisibility

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .clip(RoundedCornerShape(22.dp))
                .clickable(onClick = onBarClick)
                .border(
                    width = 1.dp,
                    color = if (isDark) MaterialTheme.colorScheme.outline.copy(alpha = 0.2f) else ImmersiveBorder,
                    shape = RoundedCornerShape(22.dp)
                )
                .testTag("mini_audio_player_bar"),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 4.dp
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Progress line
                val progress = if (audioState.durationMs > 0) {
                    audioState.currentPositionMs.toFloat() / audioState.durationMs.toFloat()
                } else 0f

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .background(if (isDark) Color.DarkGray else ImmersiveBorder)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progress.coerceIn(0f, 1f))
                            .height(3.dp)
                            .background(EmeraldPrimary)
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Reciter Icon
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(if (isDark) EmeraldPrimary.copy(alpha = 0.2f) else EmeraldLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Headphones,
                            contentDescription = null,
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Title & Reciter Info
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        val title = if (audioState.currentAyahNumber != null) {
                            "سورة ${surah.nameAr} - الآية ${toArabicDigits(audioState.currentAyahNumber)}"
                        } else {
                            "سورة ${surah.nameAr}"
                        }
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = audioState.currentReciter.nameAr,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.sp,
                                color = if (isDark) MaterialTheme.colorScheme.onSurfaceVariant else ImmersiveTextMuted
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Play/Pause Button
                    if (audioState.isBuffering) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(36.dp)
                                .padding(6.dp),
                            color = EmeraldPrimary,
                            strokeWidth = 2.5.dp
                        )
                    } else {
                        IconButton(
                            onClick = onPlayPauseClick,
                            modifier = Modifier.testTag("mini_player_play_pause")
                        ) {
                            Icon(
                                imageVector = if (audioState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (audioState.isPlaying) "إيقاف مؤقت" else "تشغيل",
                                tint = EmeraldPrimary,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }

                    // Next Surah Button
                    IconButton(
                        onClick = onNextClick,
                        modifier = Modifier.testTag("mini_player_next")
                    ) {
                        Icon(
                            imageVector = Icons.Default.SkipNext,
                            contentDescription = "التالي",
                            tint = if (isDark) MaterialTheme.colorScheme.onSurfaceVariant else ImmersiveTextMuted,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    // Close Button
                    IconButton(
                        onClick = onCloseClick,
                        modifier = Modifier.testTag("mini_player_close")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "إغلاق",
                            tint = if (isDark) MaterialTheme.colorScheme.onSurfaceVariant else ImmersiveTextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullAudioPlayerBottomSheet(
    audioState: AudioPlaybackState,
    onDismiss: () -> Unit,
    onPlayPauseClick: () -> Unit,
    onNextClick: () -> Unit,
    onPrevClick: () -> Unit,
    onSeekForward10s: () -> Unit,
    onSeekBackward10s: () -> Unit,
    onSeekTo: (Long) -> Unit,
    onSelectReciter: (Reciter) -> Unit,
    onPlaybackSpeedChange: (Float) -> Unit,
    onToggleRepeat: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val isDark = MaterialTheme.colorScheme.background.red < 0.2f

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        modifier = modifier.testTag("full_audio_player_sheet")
    ) {
        val surah = audioState.currentSurah

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Title
            Text(
                text = "مشغل التلاوات القرآنية",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = EmeraldPrimary,
                    fontSize = 15.sp
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Surah Big Emblem
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(if (isDark) EmeraldPrimary.copy(alpha = 0.2f) else EmeraldLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Headphones,
                    contentDescription = null,
                    tint = EmeraldPrimary,
                    modifier = Modifier.size(46.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Surah and Reciter names
            Text(
                text = if (surah != null) "سورة ${surah.nameAr}" else "اختر سورة للبدء",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 22.sp
                )
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = audioState.currentReciter.nameAr,
                style = MaterialTheme.typography.titleMedium.copy(
                    color = EmeraldPrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                )
            )

            Text(
                text = "رواية ${audioState.currentReciter.riwayah} • مصحف ${audioState.currentReciter.style}",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = if (isDark) MaterialTheme.colorScheme.onSurfaceVariant else ImmersiveTextMuted,
                    fontSize = 12.sp
                )
            )

            if (audioState.errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = audioState.errorMessage,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Slider & Time
            val progress = if (audioState.durationMs > 0) {
                (audioState.currentPositionMs.toFloat() / audioState.durationMs.toFloat()).coerceIn(0f, 1f)
            } else 0f

            Slider(
                value = progress,
                onValueChange = { newProg ->
                    val targetMs = (newProg * audioState.durationMs).toLong()
                    onSeekTo(targetMs)
                },
                colors = SliderDefaults.colors(
                    thumbColor = EmeraldPrimary,
                    activeTrackColor = EmeraldPrimary,
                    inactiveTrackColor = if (isDark) Color.DarkGray else ImmersiveBorder
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("audio_player_slider")
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatMs(audioState.currentPositionMs),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = if (isDark) MaterialTheme.colorScheme.onSurfaceVariant else ImmersiveTextMuted,
                        fontSize = 12.sp
                    )
                )
                Text(
                    text = formatMs(audioState.durationMs),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = if (isDark) MaterialTheme.colorScheme.onSurfaceVariant else ImmersiveTextMuted,
                        fontSize = 12.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Main Playback Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Repeat Toggle
                IconButton(
                    onClick = onToggleRepeat,
                    modifier = Modifier.testTag("audio_repeat_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Replay,
                        contentDescription = "تكرار",
                        tint = if (audioState.isRepeatEnabled) EmeraldPrimary else (if (isDark) MaterialTheme.colorScheme.onSurfaceVariant else ImmersiveTextMuted)
                    )
                }

                // Seek -10s
                IconButton(
                    onClick = onSeekBackward10s,
                    modifier = Modifier.testTag("audio_seek_backward_10s")
                ) {
                    Icon(
                        imageVector = Icons.Default.Replay10,
                        contentDescription = "تأخير 10 ثوان",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(28.dp)
                    )
                }

                // Previous Surah
                IconButton(
                    onClick = onPrevClick,
                    modifier = Modifier.testTag("audio_prev_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipPrevious,
                        contentDescription = "السورة السابقة",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(30.dp)
                    )
                }

                // Big Play/Pause (EmeraldPrimary circle with white icon)
                Surface(
                    shape = CircleShape,
                    color = EmeraldPrimary,
                    modifier = Modifier
                        .size(62.dp)
                        .clip(CircleShape)
                        .clickable(onClick = onPlayPauseClick)
                        .testTag("audio_main_play_pause")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        if (audioState.isBuffering) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(28.dp),
                                color = Color.White,
                                strokeWidth = 3.dp
                            )
                        } else {
                            Icon(
                                imageVector = if (audioState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (audioState.isPlaying) "إيقاف" else "تشغيل",
                                tint = Color.White,
                                modifier = Modifier.size(34.dp)
                            )
                        }
                    }
                }

                // Next Surah
                IconButton(
                    onClick = onNextClick,
                    modifier = Modifier.testTag("audio_next_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "السورة التالية",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(30.dp)
                    )
                }

                // Seek +10s
                IconButton(
                    onClick = onSeekForward10s,
                    modifier = Modifier.testTag("audio_seek_forward_10s")
                ) {
                    Icon(
                        imageVector = Icons.Default.Forward10,
                        contentDescription = "تقديم 10 ثوان",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Playback Speed Options (Pill style)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "السرعة: ",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = if (isDark) MaterialTheme.colorScheme.onSurfaceVariant else ImmersiveTextMuted,
                        fontSize = 12.sp
                    )
                )
                listOf(0.75f, 1.0f, 1.25f, 1.5f).forEach { speed ->
                    val isSelected = audioState.playbackSpeed == speed
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .clip(RoundedCornerShape(50))
                            .background(if (isSelected) EmeraldLight else Color.Transparent)
                            .clickable { onPlaybackSpeedChange(speed) }
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${speed}x",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) EmeraldPrimary else ImmersiveTextMuted,
                                fontSize = 12.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Reciter Quick Switcher List
            Text(
                text = "اختر القارئ",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 14.sp
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(QuranDataProvider.reciters) { reciter ->
                    val isSelected = reciter.id == audioState.currentReciter.id
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected) EmeraldPrimary else if (isDark) MaterialTheme.colorScheme.surfaceVariant else ImmersiveHeaderBg,
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .border(
                                width = 1.dp,
                                color = if (isSelected) EmeraldPrimary else if (isDark) MaterialTheme.colorScheme.outline.copy(alpha = 0.2f) else ImmersiveBorder,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable { onSelectReciter(reciter) }
                            .testTag("select_reciter_${reciter.id}")
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = reciter.nameAr,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                    fontSize = 13.sp
                                )
                            )
                            Text(
                                text = reciter.style,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = if (isSelected) Color.White.copy(alpha = 0.8f) else ImmersiveTextMuted,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

fun formatMs(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}

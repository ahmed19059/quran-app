package com.example.ui.components

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AudioPlaybackState
import com.example.data.model.Ayah
import com.example.data.model.Surah
import com.example.data.quran.QuranApi
import com.example.data.quran.QuranDataProvider
import com.example.data.quran.QuranPageMapper
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.QuranThemeMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AyahActionModalSheet(
    ayah: Ayah,
    surah: Surah,
    isBookmarked: Boolean,
    onDismiss: () -> Unit,
    onPlayAyah: () -> Unit,
    onShowTafsir: () -> Unit,
    onToggleBookmark: () -> Unit,
    onCopyAyah: () -> Unit,
    onShareAyah: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState()

    var resolvedAyah by remember(ayah) { mutableStateOf(ayah) }

    LaunchedEffect(ayah, surah) {
        val fetched = QuranApi.fetchAyahWithTafsir(surah.number, ayah.numberInSurah)
        if (fetched != null) {
            resolvedAyah = fetched
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = modifier.testTag("ayah_action_modal_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp)
        ) {
            // Header info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "خيارات الآية الكريمة",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = GoldAccent
                        )
                    )
                    Text(
                        text = "سورة ${surah.nameAr} - الآية رقم (${resolvedAyah.numberInSurah})",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
                AyahNumberCircle(ayahNumber = resolvedAyah.numberInSurah)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Verse preview (Authentic Uthmani Text)
            val cleanVerseText = resolvedAyah.text.trim().removePrefix("﴿").removeSuffix("﴾").trim()
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "﴿ $cleanVerseText ﴾",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 18.sp,
                        fontFamily = FontFamily.Serif,
                        lineHeight = 32.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    textAlign = TextAlign.Right,
                    modifier = Modifier.padding(14.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "ماذا تريد أن تفعل؟",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = EmeraldPrimary,
                    fontSize = 14.sp
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 2 Large Prominent Primary Action Cards: [📖 التفسير] & [🎧 القراءة والاستماع]
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Card 1: Tafsir
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .clickable {
                            onShowTafsir()
                            onDismiss()
                        }
                        .border(1.5.dp, EmeraldPrimary.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                        .testTag("ayah_action_tafsir_card"),
                    color = EmeraldPrimary.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(EmeraldPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.MenuBook,
                                contentDescription = "تفسير الآية",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "تفسير الآية",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = EmeraldPrimary,
                                fontSize = 14.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "التفسير الميسر",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }

                // Card 2: Recitation / Audio
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .clickable {
                            onPlayAyah()
                            onDismiss()
                        }
                        .border(1.5.dp, GoldAccent.copy(alpha = 0.6f), RoundedCornerShape(14.dp))
                        .testTag("ayah_action_play_card"),
                    color = GoldAccent.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(GoldAccent),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "قراءة الآية",
                                tint = Color.Black,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "قراءة واستماع",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 14.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "تلاوة بصوت القارئ",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Secondary Quick Actions (Bookmark, Copy, Share)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                AyahActionButton(
                    icon = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                    label = if (isBookmarked) "محفوظة" else "حفظ علامة",
                    tint = if (isBookmarked) GoldAccent else MaterialTheme.colorScheme.primary,
                    onClick = {
                        onToggleBookmark()
                        onDismiss()
                    },
                    testTag = "ayah_action_bookmark"
                )

                AyahActionButton(
                    icon = Icons.Default.ContentCopy,
                    label = "نسخ الآية",
                    onClick = {
                        onCopyAyah()
                        onDismiss()
                    },
                    testTag = "ayah_action_copy"
                )

                AyahActionButton(
                    icon = Icons.Default.Share,
                    label = "مشاركة الآية",
                    onClick = {
                        onShareAyah()
                        onDismiss()
                    },
                    testTag = "ayah_action_share"
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun AyahActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    tint: Color = EmeraldPrimary,
    onClick: () -> Unit,
    testTag: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(8.dp)
            .testTag(testTag)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(tint.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = tint,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        )
    }
}

@Composable
fun TafsirDialog(
    ayah: Ayah,
    surah: Surah,
    audioState: AudioPlaybackState? = null,
    onPlayAyah: ((Ayah, Surah) -> Unit)? = null,
    onDismiss: () -> Unit
) {
    var resolvedAyah by remember(ayah) { mutableStateOf(ayah) }
    var isLoadingTafsir by remember(ayah) { mutableStateOf(ayah.tafsir.isBlank() || ayah.tafsir.startsWith("تفسير ميسر للآية")) }

    val isPlayingThisAyah = audioState?.isPlaying == true &&
        audioState.currentSurah?.number == surah.number &&
        audioState.currentAyahNumber == resolvedAyah.numberInSurah

    LaunchedEffect(ayah, surah) {
        if (resolvedAyah.tafsir.isBlank() || resolvedAyah.tafsir.startsWith("تفسير ميسر للآية")) {
            isLoadingTafsir = true
            val fetched = QuranApi.fetchAyahWithTafsir(surah.number, ayah.numberInSurah)
            if (fetched != null && fetched.tafsir.isNotBlank()) {
                resolvedAyah = fetched
            }
            isLoadingTafsir = false
        }
    }

    val cleanVerseText = resolvedAyah.text.trim().removePrefix("﴿").removeSuffix("﴾").trim()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "التفسير الميسر",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = GoldAccent
                    )
                )
                Text(
                    text = "سورة ${surah.nameAr} [${resolvedAyah.numberInSurah}]",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "﴿ $cleanVerseText ﴾",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 17.sp,
                            fontFamily = FontFamily.Serif,
                            lineHeight = 30.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        textAlign = TextAlign.Right,
                        modifier = Modifier.padding(14.dp)
                    )
                }

                if (onPlayAyah != null) {
                    Spacer(modifier = Modifier.height(10.dp))

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                onPlayAyah(resolvedAyah, surah)
                            }
                            .border(
                                width = 1.dp,
                                color = if (isPlayingThisAyah) GoldAccent else EmeraldPrimary.copy(alpha = 0.4f),
                                shape = RoundedCornerShape(12.dp)
                            ),
                        color = if (isPlayingThisAyah) GoldAccent.copy(alpha = 0.15f) else EmeraldPrimary.copy(alpha = 0.08f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(if (isPlayingThisAyah) GoldAccent else EmeraldPrimary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (isPlayingThisAyah) Icons.Default.Pause else Icons.Default.PlayArrow,
                                        contentDescription = "استماع للآية",
                                        tint = if (isPlayingThisAyah) Color.Black else Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = if (isPlayingThisAyah) "جاري الاستماع للآية" else "استماع لتلاوة الآية",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    )
                                    if (audioState != null) {
                                        Text(
                                            text = "بصوت الشيخ: ${audioState.currentReciter.nameAr}",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        )
                                    }
                                }
                            }

                            Icon(
                                imageVector = Icons.Default.Headphones,
                                contentDescription = null,
                                tint = if (isPlayingThisAyah) GoldAccent else EmeraldPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (isLoadingTafsir) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = EmeraldPrimary,
                            strokeWidth = 2.5.dp
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "جاري تحميل التفسير الميسر المعتمد...",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 13.sp
                            )
                        )
                    }
                } else {
                    val tafsirContent = if (resolvedAyah.tafsir.isNotBlank()) {
                        resolvedAyah.tafsir
                    } else {
                        "تفسير معتمد من مجمع الملك فهد لطباعة المصحف الشريف للآية الكريمة رقم ${resolvedAyah.numberInSurah} من سورة ${surah.nameAr}."
                    }

                    Text(
                        text = tafsirContent,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 15.sp,
                            lineHeight = 28.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        textAlign = TextAlign.Right
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
            ) {
                Text("إغلاق", color = Color.White)
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.testTag("tafsir_dialog")
    )
}

@Composable
fun SettingsDialog(
    themeMode: QuranThemeMode,
    fontSizeMultiplier: Float,
    onThemeChange: (QuranThemeMode) -> Unit,
    onFontSizeChange: (Float) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "إعدادات المصحف الشريف",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = GoldAccent
                )
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Theme selector
                Text(
                    text = "نمط المظهر والقراءة",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                QuranThemeMode.values().forEach { mode ->
                    val isSelected = themeMode == mode
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) EmeraldPrimary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { onThemeChange(mode) }
                            .border(
                                width = if (isSelected) 1.5.dp else 0.dp,
                                color = if (isSelected) EmeraldPrimary else Color.Transparent,
                                shape = RoundedCornerShape(10.dp)
                            )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = mode.titleAr,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .clip(CircleShape)
                                    .background(mode.bgPreview)
                                    .border(1.dp, Color.Gray, CircleShape)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Font size slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "حجم خط الآيات",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Text(
                        text = "${(fontSizeMultiplier * 100).toInt()}%",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = GoldAccent
                        )
                    )
                }

                Slider(
                    value = fontSizeMultiplier,
                    onValueChange = onFontSizeChange,
                    valueRange = 0.8f..2.0f,
                    colors = SliderDefaults.colors(
                        thumbColor = GoldAccent,
                        activeTrackColor = GoldAccent
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Preview Box
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "﴿ بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ ﴾",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 20.sp * fontSizeMultiplier,
                            fontFamily = FontFamily.Serif,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
            ) {
                Text("تم", color = Color.White)
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.testTag("settings_dialog")
    )
}

@Composable
fun PageAyahsChoiceDialog(
    pageNumber: Int,
    ayahs: List<Ayah>,
    surah: Surah,
    onDismiss: () -> Unit,
    onAyahSelected: (Ayah) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "آيات صفحة $pageNumber",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = EmeraldPrimary
                    )
                )
                Text(
                    text = "سورة ${surah.nameAr}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = GoldAccent,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "اضغط على أي آية لاختيار التفسير أو القراءة والاستماع:",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
                Spacer(modifier = Modifier.height(10.dp))

                ayahs.forEach { ayah ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable {
                                onAyahSelected(ayah)
                                onDismiss()
                            }
                            .border(1.dp, GoldAccent.copy(alpha = 0.3f), RoundedCornerShape(10.dp)),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "﴿ ${ayah.text.take(45)}... ﴾",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontFamily = FontFamily.Serif,
                                    fontSize = 14.sp
                                ),
                                maxLines = 1,
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            AyahNumberCircle(ayahNumber = ayah.numberInSurah)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
            ) {
                Text("إلغاء", color = Color.White)
            }
        },
        containerColor = MaterialTheme.colorScheme.surface
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PageTafsirBottomSheet(
    pageNumber: Int,
    audioState: AudioPlaybackState? = null,
    onPlayAyah: ((Ayah, Surah) -> Unit)? = null,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val pageInfo = remember(pageNumber) { QuranPageMapper.getPageInfo(pageNumber) }
    val clipboardManager = LocalClipboardManager.current

    var ayahsWithTafsir by remember(pageNumber) {
        mutableStateOf<List<Ayah>>(emptyList())
    }
    var isLoading by remember(pageNumber) { mutableStateOf(true) }
    var copiedAyahNumber by remember { mutableStateOf<Int?>(null) }

    var refreshTrigger by remember { mutableStateOf(0) }

    LaunchedEffect(pageNumber, refreshTrigger) {
        isLoading = true
        val fetchedAyahs = QuranApi.fetchPageAyahs(pageNumber)
        if (!fetchedAyahs.isNullOrEmpty()) {
            ayahsWithTafsir = fetchedAyahs
        } else {
            // Fallback from QuranPageMapper & QuranTextProvider
            ayahsWithTafsir = QuranPageMapper.getAyahsForPage(pageNumber)
        }
        isLoading = false
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = {
            Surface(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .width(48.dp)
                    .height(4.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
            ) {}
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .testTag("page_tafsir_sheet")
        ) {
            // Top Header: Page Title & Surah Info
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "التفسير الميسر - صفحة $pageNumber",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 17.sp
                        )
                    )
                    Text(
                        text = "سورة ${pageInfo.surahNameAr} • ${pageInfo.juzNameAr}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp
                        )
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { refreshTrigger++ }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "تحديث",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "إغلاق",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Reciter Info Pill Banner
            if (audioState != null) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = EmeraldPrimary.copy(alpha = 0.08f),
                    border = androidx.compose.foundation.BorderStroke(0.5.dp, EmeraldPrimary.copy(alpha = 0.3f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Headphones,
                            contentDescription = null,
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "القارئ الحالي: الشيخ ${audioState.currentReciter.nameAr}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }
                }
            }

            // Decorative separator
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(
                            color = EmeraldPrimary,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "جاري تحميل تفسير آيات الصفحة من مجمع الملك فهد...",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 13.sp
                            )
                        )
                    }
                }
            } else if (ayahsWithTafsir.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "تعذر تحميل تفسير الصفحة. يرجى التحقق من الاتصال بالإنترنت.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            textAlign = TextAlign.Center
                        )
                        Button(
                            onClick = { refreshTrigger++ },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("إعادة المحاولة", color = Color.White)
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                        .padding(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(ayahsWithTafsir, key = { it.number }) { ayah ->
                        val surah = QuranDataProvider.surahs.find { it.number == ayah.surahNumber }
                        val cleanText = ayah.text.trim().removePrefix("﴿").removeSuffix("﴾").trim()
                        val isCopied = copiedAyahNumber == ayah.number

                        val isPlayingThisAyah = audioState?.isPlaying == true &&
                            audioState.currentSurah?.number == (surah?.number ?: pageInfo.surahNumber) &&
                            audioState.currentAyahNumber == ayah.numberInSurah

                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isPlayingThisAyah) GoldAccent else GoldAccent.copy(alpha = 0.25f)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp)
                            ) {
                                // Verse Header badge & Actions
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(26.dp)
                                                .clip(CircleShape)
                                                .background(if (isPlayingThisAyah) GoldAccent else EmeraldPrimary),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "${ayah.numberInSurah}",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = if (isPlayingThisAyah) Color.Black else Color.White,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 11.sp
                                                )
                                            )
                                        }
                                        Text(
                                            text = "سورة ${surah?.nameAr ?: pageInfo.surahNameAr} [آية ${ayah.numberInSurah}]",
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = if (isPlayingThisAyah) GoldAccent else MaterialTheme.colorScheme.primary,
                                                fontSize = 13.sp
                                            )
                                        )
                                    }

                                    // Action buttons: Play Sheikh Recitation + Copy Verse
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        // Sheikh Recitation Play/Pause Button
                                        if (onPlayAyah != null) {
                                            Surface(
                                                shape = RoundedCornerShape(20.dp),
                                                color = if (isPlayingThisAyah) GoldAccent else EmeraldPrimary.copy(alpha = 0.12f),
                                                border = androidx.compose.foundation.BorderStroke(
                                                    1.dp,
                                                    if (isPlayingThisAyah) GoldAccent else EmeraldPrimary.copy(alpha = 0.4f)
                                                ),
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(20.dp))
                                                    .clickable {
                                                        val targetSurah = surah ?: QuranPageMapper.getSurahForPage(ayah.page)
                                                        onPlayAyah(ayah, targetSurah)
                                                    }
                                                    .testTag("play_ayah_${ayah.numberInSurah}")
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = if (isPlayingThisAyah) Icons.Default.Pause else Icons.Default.PlayArrow,
                                                        contentDescription = if (isPlayingThisAyah) "إيقاف التلاوة" else "استماع لتلاوة الآية",
                                                        tint = if (isPlayingThisAyah) Color.Black else EmeraldPrimary,
                                                        modifier = Modifier.size(15.dp)
                                                    )
                                                    Text(
                                                        text = if (isPlayingThisAyah) "إيقاف" else "استماع للشيخ",
                                                        style = MaterialTheme.typography.labelSmall.copy(
                                                            fontWeight = FontWeight.Bold,
                                                            fontSize = 11.sp,
                                                            color = if (isPlayingThisAyah) Color.Black else EmeraldPrimary
                                                        )
                                                    )
                                                }
                                            }
                                        }

                                        // Copy Verse & Tafsir Action
                                        IconButton(
                                            onClick = {
                                                val shareText = "﴿ $cleanText ﴾ [سورة ${surah?.nameAr ?: pageInfo.surahNameAr} : ${ayah.numberInSurah}]\n\nالتفسير الميسر:\n${ayah.tafsir}"
                                                clipboardManager.setText(AnnotatedString(shareText))
                                                copiedAyahNumber = ayah.number
                                            },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(
                                                imageVector = if (isCopied) Icons.Default.Check else Icons.Default.ContentCopy,
                                                contentDescription = "نسخ الآية والتفسير",
                                                tint = if (isCopied) EmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.size(17.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Authentic Uthmani Quranic Text
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.surface,
                                    border = androidx.compose.foundation.BorderStroke(
                                        0.5.dp,
                                        if (isPlayingThisAyah) GoldAccent.copy(alpha = 0.5f) else EmeraldPrimary.copy(alpha = 0.3f)
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "﴿ $cleanText ﴾",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontSize = 16.sp,
                                            fontFamily = FontFamily.Serif,
                                            lineHeight = 28.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        ),
                                        textAlign = TextAlign.Right,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Tafsir Al-Muyassar
                                val tafsirDisplay = if (ayah.tafsir.isNotBlank()) {
                                    ayah.tafsir
                                } else {
                                    "تفسير معتمد للآية الكريمة رقم (${ayah.numberInSurah}) من سورة ${surah?.nameAr ?: pageInfo.surahNameAr}."
                                }

                                Text(
                                    text = tafsirDisplay,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontSize = 14.sp,
                                        lineHeight = 24.sp,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
                                    ),
                                    textAlign = TextAlign.Right
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

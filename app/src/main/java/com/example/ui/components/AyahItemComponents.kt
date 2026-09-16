package com.example.ui.components

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Ayah
import com.example.data.model.Surah
import com.example.ui.theme.ActiveAyahHighlight
import com.example.ui.theme.GoldAccent

@Composable
fun AyahVerseCard(
    ayah: Ayah,
    surah: Surah,
    isPlaying: Boolean,
    isBookmarked: Boolean,
    fontSizeMultiplier: Float,
    onAyahClick: () -> Unit,
    onPlayClick: () -> Unit,
    onBookmarkClick: () -> Unit,
    onTafsirClick: () -> Unit,
    onCopyClick: () -> Unit,
    onShareClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val baseFontSize = 22.sp * fontSizeMultiplier
    val baseLineHeight = 40.sp * fontSizeMultiplier

    val bgColor by animateColorAsState(
        targetValue = if (isPlaying) {
            GoldAccent.copy(alpha = 0.18f)
        } else {
            Color.Transparent
        },
        label = "ayah_bg_anim"
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onAyahClick)
            .testTag("ayah_item_${surah.number}_${ayah.numberInSurah}"),
        color = bgColor,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Verse Header: Ayah Number Emblem & Quick Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Actions on the left
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onPlayClick,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("play_ayah_btn_${ayah.numberInSurah}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "استماع للآية",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(
                        onClick = onBookmarkClick,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("bookmark_ayah_btn_${ayah.numberInSurah}")
                    ) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "حفظ علامة مرجعية",
                            tint = if (isBookmarked) GoldAccent else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(
                        onClick = onTafsirClick,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("tafsir_ayah_btn_${ayah.numberInSurah}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = "التفسير الميسر",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = onShareClick,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("share_ayah_btn_${ayah.numberInSurah}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "مشاركة الآية",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Ayah Number Ornament (Right side)
                AyahNumberCircle(ayahNumber = ayah.numberInSurah)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Ayah Arabic Text
            Text(
                text = "${ayah.text} ﴿${toArabicDigits(ayah.numberInSurah)}﴾",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = baseFontSize,
                    lineHeight = baseLineHeight,
                    fontWeight = FontWeight.Normal,
                    fontFamily = FontFamily.Serif,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                textAlign = TextAlign.Right,
                modifier = Modifier.fillMaxWidth()
            )

            // Optional Sajdah indicator
            if (ayah.sajdah) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "۩ موضع سجدة تلاوة",
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = GoldAccent,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Thin subtle divider
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(0.8.dp)
                    .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            )
        }
    }
}

@Composable
fun AyahNumberCircle(
    ayahNumber: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(GoldAccent.copy(alpha = 0.15f))
            .border(1.2.dp, GoldAccent, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = toArabicDigits(ayahNumber),
            style = MaterialTheme.typography.labelLarge.copy(
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = GoldAccent
            )
        )
    }
}

fun toArabicDigits(number: Int): String {
    val arabicDigits = charArrayOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')
    val str = number.toString()
    val builder = StringBuilder()
    for (char in str) {
        if (char in '0'..'9') {
            builder.append(arabicDigits[char - '0'])
        } else {
            builder.append(char)
        }
    }
    return builder.toString()
}

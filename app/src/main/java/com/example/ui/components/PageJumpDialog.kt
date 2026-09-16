package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.quran.QuranPageMapper
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.ImmersiveBorder
import com.example.ui.theme.ImmersiveTextMuted

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PageJumpDialog(
    currentPage: Int,
    onDismiss: () -> Unit,
    onJumpToPage: (Int) -> Unit
) {
    var pageInput by remember { mutableStateOf(currentPage.toString()) }
    var sliderValue by remember { mutableFloatStateOf(currentPage.toFloat()) }
    val isDark = MaterialTheme.colorScheme.background.red < 0.2f

    val previewInfo = remember(sliderValue.toInt()) {
        QuranPageMapper.getPageInfo(sliderValue.toInt())
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "انتقال إلى صفحة في المصحف",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = EmeraldPrimary,
                    fontSize = 18.sp
                )
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Info preview card
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (isDark) MaterialTheme.colorScheme.surfaceVariant else EmeraldLight,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "صفحة ${sliderValue.toInt()}",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = EmeraldPrimary,
                                fontSize = 24.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "سورة ${previewInfo.surahNameAr} • ${previewInfo.juzNameAr}",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = if (isDark) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Medium,
                                fontSize = 13.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Slider (1 to 604)
                Slider(
                    value = sliderValue,
                    onValueChange = { newVal ->
                        sliderValue = newVal
                        pageInput = newVal.toInt().toString()
                    },
                    valueRange = 1f..604f,
                    colors = SliderDefaults.colors(
                        thumbColor = EmeraldPrimary,
                        activeTrackColor = EmeraldPrimary,
                        inactiveTrackColor = if (isDark) Color.DarkGray else ImmersiveBorder
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("page_jump_slider")
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Direct number text field
                OutlinedTextField(
                    value = pageInput,
                    onValueChange = { text ->
                        if (text.all { it.isDigit() } && text.length <= 3) {
                            pageInput = text
                            val parsed = text.toIntOrNull()
                            if (parsed != null && parsed in 1..604) {
                                sliderValue = parsed.toFloat()
                            }
                        }
                    },
                    label = { Text("رقم الصفحة (1 - 604)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            val target = pageInput.toIntOrNull()?.coerceIn(1, 604) ?: sliderValue.toInt()
                            onJumpToPage(target)
                        }
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("page_jump_text_input")
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Quick jump pills
                Text(
                    text = "محطات شهيرة:",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = ImmersiveTextMuted,
                        fontSize = 11.sp
                    ),
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(6.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val quickPicks = listOf(
                        "الفاتحة (1)" to 1,
                        "البقرة (2)" to 2,
                        "الكهف (293)" to 293,
                        "يس (440)" to 440,
                        "الملك (562)" to 562,
                        "الناس (604)" to 604
                    )
                    quickPicks.forEach { (label, pageNum) ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (sliderValue.toInt() == pageNum) EmeraldPrimary else if (isDark) MaterialTheme.colorScheme.surfaceVariant else Color(0xFFF0F4F2))
                                .clickable {
                                    sliderValue = pageNum.toFloat()
                                    pageInput = pageNum.toString()
                                }
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (sliderValue.toInt() == pageNum) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val target = pageInput.toIntOrNull()?.coerceIn(1, 604) ?: sliderValue.toInt()
                    onJumpToPage(target)
                },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                modifier = Modifier.testTag("confirm_jump_button")
            ) {
                Text("انتقال", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء", color = if (isDark) Color.White else ImmersiveTextMuted)
            }
        }
    )
}

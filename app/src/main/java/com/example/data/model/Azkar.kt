package com.example.data.model

data class ZikrItem(
    val id: Int,
    val title: String,
    val text: String,
    val repeatCount: Int,
    val rewardOrVirtue: String = "",
    val reference: String = ""
)

data class AzkarCategory(
    val id: String,
    val nameAr: String,
    val iconName: String,
    val description: String,
    val items: List<ZikrItem>
)

package com.example.data.model

data class Surah(
    val number: Int,
    val nameAr: String,
    val nameEn: String,
    val revelationType: String, // "مكية" or "مدنية"
    val numberOfAyahs: Int,
    val pageNumber: Int,
    val juzNumber: Int,
    val englishTranslation: String
)

data class Ayah(
    val number: Int,
    val numberInSurah: Int,
    val surahNumber: Int,
    val text: String,
    val juz: Int,
    val page: Int,
    val sajdah: Boolean = false,
    val tafsir: String = ""
)

data class Juz(
    val number: Int,
    val startSurahNumber: Int,
    val startSurahName: String,
    val startAyahNumber: Int,
    val startPage: Int,
    val endPage: Int
)

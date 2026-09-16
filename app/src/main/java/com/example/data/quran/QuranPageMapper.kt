package com.example.data.quran

data class QuranPageInfo(
    val pageNumber: Int,
    val surahNumber: Int,
    val surahNameAr: String,
    val juzNumber: Int,
    val juzNameAr: String,
    val hizbNumber: Int,
    val startAyahNumber: Int,
    val endAyahNumber: Int
)

object QuranPageMapper {

    private val arabicNumbers = mapOf(
        1 to "الأول", 2 to "الثاني", 3 to "الثالث", 4 to "الرابع", 5 to "الخامس",
        6 to "السادس", 7 to "السابع", 8 to "الثامن", 9 to "التاسع", 10 to "العاشر",
        11 to "الحادي عشر", 12 to "الثاني عشر", 13 to "الثالث عشر", 14 to "الرابع عشر", 15 to "الخامس عشر",
        16 to "السادس عشر", 17 to "السابع عشر", 18 to "الثامن عشر", 19 to "التاسع عشر", 20 to "العشرون",
        21 to "الحادي والعشرون", 22 to "الثاني والعشرون", 23 to "الثالث والعشرون", 24 to "الرابع والعشرون", 25 to "الخامس والعشرون",
        26 to "السادس والعشرون", 27 to "السابع والعشرون", 28 to "الثامن والعشرون", 29 to "التاسع والعشرون", 30 to "الثلاثون"
    )

    fun getJuzName(juz: Int): String {
        return "الجزء ${arabicNumbers[juz] ?: "$juz"}"
    }

    fun getJuzForPage(page: Int): Int {
        return when {
            page <= 21 -> 1
            page <= 41 -> 2
            page <= 61 -> 3
            page <= 81 -> 4
            page <= 101 -> 5
            page <= 121 -> 6
            page <= 141 -> 7
            page <= 161 -> 8
            page <= 181 -> 9
            page <= 201 -> 10
            page <= 221 -> 11
            page <= 241 -> 12
            page <= 261 -> 13
            page <= 281 -> 14
            page <= 301 -> 15
            page <= 321 -> 16
            page <= 341 -> 17
            page <= 361 -> 18
            page <= 381 -> 19
            page <= 401 -> 20
            page <= 421 -> 21
            page <= 441 -> 22
            page <= 461 -> 23
            page <= 481 -> 24
            page <= 501 -> 25
            page <= 521 -> 26
            page <= 541 -> 27
            page <= 561 -> 28
            page <= 581 -> 29
            else -> 30
        }
    }

    fun getHizbForPage(page: Int): Int {
        val juz = getJuzForPage(page)
        val juzStartPage = (juz - 1) * 20 + 2
        return if (page < juzStartPage + 10) (juz * 2) - 1 else juz * 2
    }

    fun getSurahForPage(page: Int): com.example.data.model.Surah {
        val surahs = QuranDataProvider.surahs
        for (i in surahs.indices.reversed()) {
            if (page >= surahs[i].pageNumber) {
                return surahs[i]
            }
        }
        return surahs.first()
    }

    fun getSurahsForPage(page: Int): List<com.example.data.model.Surah> {
        val validPage = page.coerceIn(1, 604)
        val surahs = QuranDataProvider.surahs
        val matchingSurahs = mutableListOf<com.example.data.model.Surah>()

        for (i in surahs.indices) {
            val s = surahs[i]
            val nextStartPage = if (i + 1 < surahs.size) surahs[i + 1].pageNumber else 605
            if (validPage in s.pageNumber until nextStartPage || (s.pageNumber == validPage)) {
                if (!matchingSurahs.contains(s)) {
                    matchingSurahs.add(s)
                }
            }
        }
        return if (matchingSurahs.isNotEmpty()) matchingSurahs else listOf(getSurahForPage(validPage))
    }

    fun getAyahsForPage(page: Int, dynamicMap: Map<Int, List<com.example.data.model.Ayah>> = emptyMap()): List<com.example.data.model.Ayah> {
        val validPage = page.coerceIn(1, 604)
        val surahsOnPage = getSurahsForPage(validPage)
        val pageAyahs = mutableListOf<com.example.data.model.Ayah>()

        for (surah in surahsOnPage) {
            val allSurahAyahs = dynamicMap[surah.number] ?: QuranTextProvider.getAyahsForSurah(surah.number)
            if (allSurahAyahs.isEmpty()) continue

            val surahsList = QuranDataProvider.surahs
            val index = surahsList.indexOfFirst { it.number == surah.number }
            val nextStartPage = if (index >= 0 && index + 1 < surahsList.size) surahsList[index + 1].pageNumber else 605

            val totalPagesForSurah = (nextStartPage - surah.pageNumber).coerceAtLeast(1)
            val pageOffsetInSurah = (validPage - surah.pageNumber).coerceIn(0, totalPagesForSurah - 1)

            val ayahsPerPage = surah.numberOfAyahs.toFloat() / totalPagesForSurah.toFloat()
            val startIdx = (pageOffsetInSurah * ayahsPerPage).toInt().coerceIn(0, allSurahAyahs.size - 1)
            val endIdx = if (pageOffsetInSurah == totalPagesForSurah - 1) {
                allSurahAyahs.size
            } else {
                ((pageOffsetInSurah + 1) * ayahsPerPage).toInt().coerceIn(startIdx + 1, allSurahAyahs.size)
            }

            val sublist = allSurahAyahs.subList(startIdx, endIdx)
            pageAyahs.addAll(sublist)
        }

        return if (pageAyahs.isNotEmpty()) pageAyahs else {
            val fallbackSurah = getSurahForPage(validPage)
            dynamicMap[fallbackSurah.number] ?: QuranTextProvider.getAyahsForSurah(fallbackSurah.number)
        }
    }

    fun getAyahAtPosition(
        page: Int,
        relativeY: Float,
        dynamicMap: Map<Int, List<com.example.data.model.Ayah>> = emptyMap()
    ): com.example.data.model.Ayah? {
        val ayahs = getAyahsForPage(page, dynamicMap)
        if (ayahs.isEmpty()) return null
        if (ayahs.size == 1) return ayahs.first()
        val clampedY = relativeY.coerceIn(0f, 0.999f)
        val index = (clampedY * ayahs.size).toInt().coerceIn(0, ayahs.size - 1)
        return ayahs[index]
    }

    fun getPageInfo(page: Int): QuranPageInfo {
        val validPage = page.coerceIn(1, 604)
        val surah = getSurahForPage(validPage)
        val juz = getJuzForPage(validPage)
        val hizb = getHizbForPage(validPage)
        return QuranPageInfo(
            pageNumber = validPage,
            surahNumber = surah.number,
            surahNameAr = surah.nameAr,
            juzNumber = juz,
            juzNameAr = getJuzName(juz),
            hizbNumber = hizb,
            startAyahNumber = 1,
            endAyahNumber = surah.numberOfAyahs
        )
    }

    // High resolution standard Madani Quran page image URLs
    fun getPageImageUrl(page: Int): String {
        val formattedPage = String.format("%03d", page.coerceIn(1, 604))
        return "https://android.quran.com/data/width_1260/page$formattedPage.png"
    }

    fun getPageImageBackupUrl(page: Int): String {
        val formattedPage = String.format("%03d", page.coerceIn(1, 604))
        return "https://everyayah.com/data/quranpngs/width_1024/$formattedPage.png"
    }
}

package com.example.data.quran

import android.util.Log
import com.example.data.model.Ayah
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object QuranApi {
    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    private val inMemoryCache = mutableMapOf<Int, List<Ayah>>()
    private val pageAyahsCache = mutableMapOf<Int, List<Ayah>>()
    private val singleAyahCache = mutableMapOf<String, Ayah>()

    suspend fun fetchPageAyahs(pageNumber: Int): List<Ayah>? = withContext(Dispatchers.IO) {
        val validPage = pageNumber.coerceIn(1, 604)
        if (pageAyahsCache.containsKey(validPage)) {
            val cached = pageAyahsCache[validPage]
            if (!cached.isNullOrEmpty()) {
                return@withContext cached
            }
        }

        // Attempt 1: AlQuran Cloud multi-edition (Uthmani + Tafsir Al-Muyassar)
        try {
            val url = "https://api.alquran.cloud/v1/page/$validPage/editions/quran-uthmani,ar.muyassar"
            val request = Request.Builder()
                .url(url)
                .header("Accept", "application/json")
                .build()
            val response = client.newCall(request).execute()

            if (response.isSuccessful) {
                val bodyString = response.body?.string()
                if (!bodyString.isNullOrBlank()) {
                    val json = JSONObject(bodyString)
                    val dataObj = json.opt("data")
                    if (dataObj is JSONArray && dataObj.length() > 0) {
                        val uthmaniEdition = dataObj.getJSONObject(0)
                        val tafsirEdition = if (dataObj.length() > 1) dataObj.getJSONObject(1) else null

                        val uthmaniAyahs = uthmaniEdition.getJSONArray("ayahs")
                        val tafsirAyahs = tafsirEdition?.optJSONArray("ayahs")

                        val resultList = mutableListOf<Ayah>()
                        for (i in 0 until uthmaniAyahs.length()) {
                            val aObj = uthmaniAyahs.getJSONObject(i)
                            val number = aObj.getInt("number")
                            val numberInSurah = aObj.getInt("numberInSurah")
                            var text = aObj.getString("text")
                            val surahObj = aObj.optJSONObject("surah")
                            val surahNumber = surahObj?.optInt("number", 1) ?: 1

                            if (surahNumber != 1 && surahNumber != 9 && numberInSurah == 1) {
                                text = text.removePrefix("بِسْمِ ٱللَّهِ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ")
                                    .removePrefix("بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ")
                                    .trim()
                            }

                            val juz = aObj.optInt("juz", QuranPageMapper.getJuzForPage(validPage))
                            val page = aObj.optInt("page", validPage)

                            val tafsirText = if (tafsirAyahs != null && i < tafsirAyahs.length()) {
                                tafsirAyahs.getJSONObject(i).optString("text", "")
                            } else {
                                ""
                            }

                            val ayahItem = Ayah(
                                number = number,
                                numberInSurah = numberInSurah,
                                surahNumber = surahNumber,
                                text = text,
                                juz = juz,
                                page = page,
                                sajdah = aObj.optBoolean("sajda", false),
                                tafsir = tafsirText
                            )
                            resultList.add(ayahItem)
                            singleAyahCache["$surahNumber:$numberInSurah"] = ayahItem
                        }

                        if (resultList.isNotEmpty()) {
                            pageAyahsCache[validPage] = resultList
                            return@withContext resultList
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.w("QuranApi", "AlQuran Cloud multi-edition failed for page $validPage", e)
        }

        // Attempt 2: AlQuran Cloud separate edition endpoints
        try {
            val uthmaniUrl = "https://api.alquran.cloud/v1/page/$validPage/quran-uthmani"
            val tafsirUrl = "https://api.alquran.cloud/v1/page/$validPage/ar.muyassar"

            val uthmaniResp = client.newCall(Request.Builder().url(uthmaniUrl).build()).execute()
            val tafsirResp = client.newCall(Request.Builder().url(tafsirUrl).build()).execute()

            if (uthmaniResp.isSuccessful) {
                val uBody = uthmaniResp.body?.string()
                val tBody = if (tafsirResp.isSuccessful) tafsirResp.body?.string() else null

                if (!uBody.isNullOrBlank()) {
                    val uJson = JSONObject(uBody)
                    val uData = uJson.optJSONObject("data")
                    val uAyahs = uData?.optJSONArray("ayahs")

                    val tAyahs = if (!tBody.isNullOrBlank()) {
                        val tJson = JSONObject(tBody)
                        tJson.optJSONObject("data")?.optJSONArray("ayahs")
                    } else null

                    if (uAyahs != null && uAyahs.length() > 0) {
                        val resultList = mutableListOf<Ayah>()
                        for (i in 0 until uAyahs.length()) {
                            val aObj = uAyahs.getJSONObject(i)
                            val number = aObj.getInt("number")
                            val numberInSurah = aObj.getInt("numberInSurah")
                            var text = aObj.getString("text")
                            val surahObj = aObj.optJSONObject("surah")
                            val surahNumber = surahObj?.optInt("number", 1) ?: 1

                            if (surahNumber != 1 && surahNumber != 9 && numberInSurah == 1) {
                                text = text.removePrefix("بِسْمِ ٱللَّهِ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ")
                                    .removePrefix("بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ")
                                    .trim()
                            }

                            val juz = aObj.optInt("juz", QuranPageMapper.getJuzForPage(validPage))
                            val page = aObj.optInt("page", validPage)
                            val tafsirText = if (tAyahs != null && i < tAyahs.length()) {
                                tAyahs.getJSONObject(i).optString("text", "")
                            } else ""

                            val ayahItem = Ayah(
                                number = number,
                                numberInSurah = numberInSurah,
                                surahNumber = surahNumber,
                                text = text,
                                juz = juz,
                                page = page,
                                sajdah = aObj.optBoolean("sajda", false),
                                tafsir = tafsirText
                            )
                            resultList.add(ayahItem)
                            singleAyahCache["$surahNumber:$numberInSurah"] = ayahItem
                        }

                        if (resultList.isNotEmpty()) {
                            pageAyahsCache[validPage] = resultList
                            return@withContext resultList
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.w("QuranApi", "AlQuran Cloud separate editions failed for page $validPage", e)
        }

        // Attempt 3: Quran.com API v4
        try {
            val quranComUrl = "https://api.quran.com/api/v4/verses/by_page/$validPage?language=ar&words=false&tafsirs=16&fields=text_uthmani,chapter_id,verse_number,juz_number,page_number"
            val resp = client.newCall(Request.Builder().url(quranComUrl).build()).execute()
            if (resp.isSuccessful) {
                val body = resp.body?.string()
                if (!body.isNullOrBlank()) {
                    val json = JSONObject(body)
                    val verses = json.optJSONArray("verses")
                    if (verses != null && verses.length() > 0) {
                        val resultList = mutableListOf<Ayah>()
                        for (i in 0 until verses.length()) {
                            val vObj = verses.getJSONObject(i)
                            val id = vObj.optInt("id", i + 1)
                            val verseNumber = vObj.optInt("verse_number", i + 1)
                            val chapterId = vObj.optInt("chapter_id", 1)
                            var text = vObj.optString("text_uthmani", "")
                            val juz = vObj.optInt("juz_number", QuranPageMapper.getJuzForPage(validPage))
                            val page = vObj.optInt("page_number", validPage)

                            var tafsirText = ""
                            val tafsirs = vObj.optJSONArray("tafsirs")
                            if (tafsirs != null && tafsirs.length() > 0) {
                                tafsirText = tafsirs.getJSONObject(0).optString("text", "")
                                    .replace("<[^>]*>".toRegex(), "") // Strip HTML tags
                            }

                            if (chapterId != 1 && chapterId != 9 && verseNumber == 1) {
                                text = text.removePrefix("بِسْمِ ٱللَّهِ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ")
                                    .removePrefix("بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ")
                                    .trim()
                            }

                            val ayahItem = Ayah(
                                number = id,
                                numberInSurah = verseNumber,
                                surahNumber = chapterId,
                                text = text,
                                juz = juz,
                                page = page,
                                tafsir = tafsirText
                            )
                            resultList.add(ayahItem)
                            singleAyahCache["$chapterId:$verseNumber"] = ayahItem
                        }

                        if (resultList.isNotEmpty()) {
                            pageAyahsCache[validPage] = resultList
                            return@withContext resultList
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.w("QuranApi", "Quran.com API v4 failed for page $validPage", e)
        }

        return@withContext null
    }

    suspend fun fetchSurahAyahs(surahNumber: Int): List<Ayah>? = withContext(Dispatchers.IO) {
        if (inMemoryCache.containsKey(surahNumber)) {
            return@withContext inMemoryCache[surahNumber]
        }

        try {
            val url = "https://api.alquran.cloud/v1/surah/$surahNumber/editions/quran-uthmani,ar.muyassar"
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()

            if (!response.isSuccessful) {
                Log.w("QuranApi", "Failed to fetch surah $surahNumber: ${response.code}")
                return@withContext null
            }

            val bodyString = response.body?.string() ?: return@withContext null
            val json = JSONObject(bodyString)
            val dataArray = json.getJSONArray("data")
            
            val uthmaniEdition = dataArray.getJSONObject(0)
            val tafsirEdition = if (dataArray.length() > 1) dataArray.getJSONObject(1) else null
            
            val uthmaniAyahs = uthmaniEdition.getJSONArray("ayahs")
            val tafsirAyahs = tafsirEdition?.getJSONArray("ayahs")

            val resultList = mutableListOf<Ayah>()
            for (i in 0 until uthmaniAyahs.length()) {
                val aObj = uthmaniAyahs.getJSONObject(i)
                val number = aObj.getInt("number")
                val numberInSurah = aObj.getInt("numberInSurah")
                var text = aObj.getString("text")
                
                // If Surah is not Al-Fatihah (1) and it's Ayah 1, remove prefix Bismillah if duplicated
                if (surahNumber != 1 && surahNumber != 9 && numberInSurah == 1) {
                    text = text.removePrefix("بِسْمِ ٱللَّهِ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ")
                        .removePrefix("بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ")
                        .trim()
                }

                val juz = aObj.optInt("juz", QuranPageMapper.getJuzForPage(aObj.optInt("page", 1)))
                val page = aObj.optInt("page", 1)

                val tafsirText = if (tafsirAyahs != null && i < tafsirAyahs.length()) {
                    tafsirAyahs.getJSONObject(i).optString("text", "")
                } else {
                    ""
                }

                val ayahItem = Ayah(
                    number = number,
                    numberInSurah = numberInSurah,
                    surahNumber = surahNumber,
                    text = text,
                    juz = juz,
                    page = page,
                    sajdah = aObj.optBoolean("sajda", false),
                    tafsir = tafsirText
                )
                resultList.add(ayahItem)
                singleAyahCache["$surahNumber:$numberInSurah"] = ayahItem
            }

            if (resultList.isNotEmpty()) {
                inMemoryCache[surahNumber] = resultList
            }
            return@withContext resultList
        } catch (e: Exception) {
            Log.e("QuranApi", "Error fetching surah $surahNumber", e)
            return@withContext null
        }
    }

    suspend fun fetchAyahWithTafsir(surahNumber: Int, ayahNumberInSurah: Int): Ayah? = withContext(Dispatchers.IO) {
        val key = "$surahNumber:$ayahNumberInSurah"
        if (singleAyahCache.containsKey(key)) {
            val cached = singleAyahCache[key]
            if (cached != null && cached.tafsir.isNotBlank()) {
                return@withContext cached
            }
        }

        // Try loading entire surah first
        val surahAyahs = fetchSurahAyahs(surahNumber)
        val found = surahAyahs?.find { it.numberInSurah == ayahNumberInSurah }
        if (found != null) {
            return@withContext found
        }

        // Direct Ayah endpoint fallback
        try {
            val url = "https://api.alquran.cloud/v1/ayah/$surahNumber:$ayahNumberInSurah/editions/quran-uthmani,ar.muyassar"
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()

            if (!response.isSuccessful) return@withContext null
            val bodyString = response.body?.string() ?: return@withContext null
            val json = JSONObject(bodyString)
            val dataArray = json.getJSONArray("data")

            val uthmaniObj = dataArray.getJSONObject(0)
            val tafsirObj = if (dataArray.length() > 1) dataArray.getJSONObject(1) else null

            var text = uthmaniObj.getString("text")
            if (surahNumber != 1 && surahNumber != 9 && ayahNumberInSurah == 1) {
                text = text.removePrefix("بِسْمِ ٱللَّهِ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ")
                    .removePrefix("بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ")
                    .trim()
            }

            val tafsirText = tafsirObj?.optString("text", "") ?: ""
            val number = uthmaniObj.getInt("number")
            val page = uthmaniObj.optInt("page", 1)
            val juz = uthmaniObj.optInt("juz", 1)

            val ayah = Ayah(
                number = number,
                numberInSurah = ayahNumberInSurah,
                surahNumber = surahNumber,
                text = text,
                juz = juz,
                page = page,
                tafsir = tafsirText
            )
            singleAyahCache[key] = ayah
            return@withContext ayah
        } catch (e: Exception) {
            Log.e("QuranApi", "Error fetching single ayah $key", e)
            return@withContext null
        }
    }
}


package com.example.data.model

data class Reciter(
    val id: String,
    val nameAr: String,
    val nameEn: String,
    val riwayah: String = "حفص عن عاصم",
    val style: String = "مرتل", // مرتل / مجود
    val serverUrl: String,
    val bioAr: String,
    val isFeatured: Boolean = false
) {
    fun getSurahAudioUrl(surahNumber: Int): String {
        val formattedSurah = String.format("%03d", surahNumber)
        return if (serverUrl.endsWith("/")) {
            "$serverUrl$formattedSurah.mp3"
        } else {
            "$serverUrl/$formattedSurah.mp3"
        }
    }

    fun getAyahAudioUrl(surahNumber: Int, ayahNumberInSurah: Int): String {
        // High reliability EveryAyah CDN format for individual verses
        val s = String.format("%03d", surahNumber)
        val a = String.format("%03d", ayahNumberInSurah)
        val subfolder = when (id) {
            "alafasy" -> "Alafasy_128kbps"
            "abdulbasit_murattal" -> "Abdul_Basit_Murattal_192kbps"
            "abdulbasit_mujawwad" -> "Abdul_Basit_Mujawwad_128kbps"
            "husary" -> "Husary_128kbps"
            "husary_muallim" -> "Husary_Muallim_128kbps"
            "minshawi_murattal" -> "Minshawy_Murattal_128kbps"
            "minshawi_mujawwad" -> "Minshawy_Mujawwad_192kbps"
            "ghamadi" -> "Ghamadi_40kbps"
            "shatri" -> "Abu_Bakr_Ash-Shaatree_128kbps"
            "ajamy" -> "Ahmed_ibn_Ali_al-Ajamy_128kbps_ketaballah.net"
            "sudais" -> "Abdurrahmaan_As-Sudais_192kbps"
            "maher" -> "MaherAlMuaiqly128kbps"
            "dossari" -> "Yasser_Ad-Dussary_128kbps"
            else -> "Alafasy_128kbps"
        }
        return "https://everyayah.com/data/$subfolder/$s$a.mp3"
    }
}

data class AudioPlaybackState(
    val isPlaying: Boolean = false,
    val isBuffering: Boolean = false,
    val currentSurah: Surah? = null,
    val currentAyahNumber: Int? = null,
    val currentReciter: Reciter,
    val currentPositionMs: Long = 0L,
    val durationMs: Long = 0L,
    val playbackSpeed: Float = 1.0f,
    val isRepeatEnabled: Boolean = false,
    val errorMessage: String? = null
)

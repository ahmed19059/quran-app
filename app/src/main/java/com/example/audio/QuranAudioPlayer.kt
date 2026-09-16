package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Build
import android.os.Handler
import android.os.Looper
import com.example.data.model.AudioPlaybackState
import com.example.data.model.Ayah
import com.example.data.model.Reciter
import com.example.data.model.Surah
import com.example.data.quran.QuranDataProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class QuranAudioPlayer(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null
    private val handler = Handler(Looper.getMainLooper())

    private val _playbackState = MutableStateFlow(
        AudioPlaybackState(
            currentReciter = QuranDataProvider.reciters.first()
        )
    )
    val playbackState: StateFlow<AudioPlaybackState> = _playbackState.asStateFlow()

    private val progressUpdater = object : Runnable {
        override fun run() {
            mediaPlayer?.let { player ->
                if (player.isPlaying) {
                    _playbackState.update {
                        it.copy(
                            currentPositionMs = player.currentPosition.toLong(),
                            durationMs = player.duration.toLong()
                        )
                    }
                    handler.postDelayed(this, 500)
                }
            }
        }
    }

    fun setReciter(reciter: Reciter) {
        _playbackState.update { it.copy(currentReciter = reciter) }
        // If already playing, restart with new reciter
        val currentSurah = _playbackState.value.currentSurah
        if (currentSurah != null && _playbackState.value.isPlaying) {
            playSurah(currentSurah, reciter)
        }
    }

    fun playSurah(surah: Surah, reciter: Reciter = _playbackState.value.currentReciter) {
        val url = reciter.getSurahAudioUrl(surah.number)
        playUrl(
            url = url,
            surah = surah,
            ayahNumber = null,
            reciter = reciter
        )
    }

    fun playAyah(surah: Surah, ayah: Ayah, reciter: Reciter = _playbackState.value.currentReciter) {
        val url = reciter.getAyahAudioUrl(surah.number, ayah.numberInSurah)
        playUrl(
            url = url,
            surah = surah,
            ayahNumber = ayah.numberInSurah,
            reciter = reciter
        )
    }

    private fun playUrl(url: String, surah: Surah, ayahNumber: Int?, reciter: Reciter) {
        releasePlayer()

        _playbackState.update {
            it.copy(
                isBuffering = true,
                isPlaying = false,
                currentSurah = surah,
                currentAyahNumber = ayahNumber,
                currentReciter = reciter,
                currentPositionMs = 0L,
                durationMs = 0L,
                errorMessage = null
            )
        }

        try {
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(url)
                setOnPreparedListener { player ->
                    _playbackState.update {
                        it.copy(
                            isBuffering = false,
                            isPlaying = true,
                            durationMs = player.duration.toLong()
                        )
                    }
                    applyPlaybackSpeed(_playbackState.value.playbackSpeed)
                    player.start()
                    handler.post(progressUpdater)
                }
                setOnCompletionListener {
                    handler.removeCallbacks(progressUpdater)
                    handleTrackCompletion()
                }
                setOnErrorListener { _, what, extra ->
                    handler.removeCallbacks(progressUpdater)
                    _playbackState.update {
                        it.copy(
                            isBuffering = false,
                            isPlaying = false,
                            errorMessage = "تعذر تشغيل الصوت (تأكد من الاتصال بالإنترنت)"
                        )
                    }
                    true
                }
                prepareAsync()
            }
        } catch (e: Exception) {
            _playbackState.update {
                it.copy(
                    isBuffering = false,
                    isPlaying = false,
                    errorMessage = e.localizedMessage ?: "خطأ في تشغيل الصوت"
                )
            }
        }
    }

    fun togglePlayPause() {
        val player = mediaPlayer ?: return
        if (player.isPlaying) {
            player.pause()
            handler.removeCallbacks(progressUpdater)
            _playbackState.update { it.copy(isPlaying = false) }
        } else {
            player.start()
            handler.post(progressUpdater)
            _playbackState.update { it.copy(isPlaying = true) }
        }
    }

    fun seekTo(positionMs: Long) {
        mediaPlayer?.let { player ->
            player.seekTo(positionMs.toInt())
            _playbackState.update { it.copy(currentPositionMs = positionMs) }
        }
    }

    fun seekForward10s() {
        val current = _playbackState.value.currentPositionMs
        val total = _playbackState.value.durationMs
        val target = (current + 10000).coerceAtMost(total)
        seekTo(target)
    }

    fun seekBackward10s() {
        val current = _playbackState.value.currentPositionMs
        val target = (current - 10000).coerceAtLeast(0)
        seekTo(target)
    }

    fun setPlaybackSpeed(speed: Float) {
        _playbackState.update { it.copy(playbackSpeed = speed) }
        applyPlaybackSpeed(speed)
    }

    private fun applyPlaybackSpeed(speed: Float) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                mediaPlayer?.let { player ->
                    if (player.isPlaying || _playbackState.value.isPlaying) {
                        player.playbackParams = player.playbackParams.setSpeed(speed)
                    }
                }
            } catch (e: Exception) {
                // Ignore speed change error on unsupported devices
            }
        }
    }

    fun toggleRepeat() {
        _playbackState.update { it.copy(isRepeatEnabled = !it.isRepeatEnabled) }
    }

    fun playNextSurah() {
        val current = _playbackState.value.currentSurah ?: return
        val nextNumber = if (current.number < 114) current.number + 1 else 1
        val nextSurah = QuranDataProvider.surahs.find { it.number == nextNumber } ?: return
        playSurah(nextSurah)
    }

    fun playPreviousSurah() {
        val current = _playbackState.value.currentSurah ?: return
        val prevNumber = if (current.number > 1) current.number - 1 else 114
        val prevSurah = QuranDataProvider.surahs.find { it.number == prevNumber } ?: return
        playSurah(prevSurah)
    }

    private fun handleTrackCompletion() {
        val state = _playbackState.value
        if (state.isRepeatEnabled && state.currentSurah != null) {
            if (state.currentAyahNumber != null) {
                // Replay Ayah if repeat is enabled
                playAyah(
                    state.currentSurah,
                    Ayah(0, state.currentAyahNumber, state.currentSurah.number, "", 1, 1),
                    state.currentReciter
                )
            } else {
                // Replay Surah
                playSurah(state.currentSurah, state.currentReciter)
            }
        } else if (state.currentAyahNumber != null) {
            // When playing an individual Ayah, stop playback automatically once the Ayah finishes
            _playbackState.update {
                it.copy(isPlaying = false, currentPositionMs = 0L)
            }
        } else if (state.currentSurah != null && state.currentSurah.number < 114) {
            // Auto advance to next Surah only when playing full Surahs
            playNextSurah()
        } else {
            _playbackState.update {
                it.copy(isPlaying = false, currentPositionMs = 0L)
            }
        }
    }

    fun stop() {
        releasePlayer()
        _playbackState.update {
            it.copy(
                isPlaying = false,
                isBuffering = false,
                currentSurah = null,
                currentAyahNumber = null,
                currentPositionMs = 0L,
                durationMs = 0L
            )
        }
    }

    private fun releasePlayer() {
        handler.removeCallbacks(progressUpdater)
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        } catch (e: Exception) {
            // Ignore
        }
        mediaPlayer = null
    }

    fun destroy() {
        releasePlayer()
    }
}

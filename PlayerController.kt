package com.musicnova.app.ui.bridge

import com.musicnova.app.ui.model.PlaybackUiState
import com.musicnova.app.ui.model.RepeatMode
import com.musicnova.app.ui.model.UiPlaylist
import com.musicnova.app.ui.model.UiSong
import com.musicnova.app.ui.model.UiStats
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * The ONLY things the UI knows about your engine.
 * Implement this on top of your MusicPlayer (see MusicPlayerBridge.kt).
 */
interface PlayerController {
    val state: StateFlow<PlaybackUiState>
    fun play(queue: List<UiSong>, startIndex: Int)
    fun togglePlayPause()
    fun next()
    fun previous()
    fun seekTo(positionMs: Long)
    fun toggleShuffle()
    fun cycleRepeat()
} interface SearchController {
    suspend fun searchSongs(query: String): List<UiSong>
}

/** Songs, playlists and stats source (your existing scanner / database). */
interface LibraryProvider {
    val songs: StateFlow<List<UiSong>>
    val playlists: StateFlow<List<UiPlaylist>>
    val stats: StateFlow<UiStats>
    fun toggleFavorite(songId: Long)
    fun createPlaylist(name: String, songIds: List<Long> = emptyList())
    fun addToPlaylist(playlistId: Long, songId: Long)
}

// ---------------------------------------------------------------------------
// Demo implementations so the UI runs on its own (no audio). Replace in MainActivity.
// ---------------------------------------------------------------------------

class FakePlayerController : PlayerController {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val _state = MutableStateFlow(PlaybackUiState())
    override val state: StateFlow<PlaybackUiState> = _state.asStateFlow()

    private var queue: List<UiSong> = emptyList()
    private var index = 0
    private var ticker: Job? = null

    override fun play(queue: List<UiSong>, startIndex: Int) {
        if (queue.isEmpty()) return
        this.queue = queue
        index = startIndex.coerceIn(0, queue.lastIndex)
        load()
    }

    private fun load() {
        val song = queue[index]
        _state.update {
            it.copy(currentSong = song, positionMs = 0L, durationMs = song.durationMs, isPlaying = true)
        }
        restartTicker()
    }

    private fun restartTicker() {
        ticker?.cancel()
        ticker = scope.launch {
            while (isActive) {
                delay(500)
                val s = _state.value
                if (!s.isPlaying) continue
                val newPos = s.positionMs + 500
                if (newPos >= s.durationMs) next() else _state.update { it.copy(positionMs = newPos) }
            }
        }
    }

    override fun togglePlayPause() {
        if (_state.value.currentSong == null) return
        _state.update { it.copy(isPlaying = !it.isPlaying) }
    }

    override fun next() {
        if (queue.isEmpty()) return
        index = if (_state.value.shuffle) queue.indices.random() else (index + 1) % queue.size
        load()
    }

    override fun previous() {
        if (queue.isEmpty()) return
        if (_state.value.positionMs > 3000) {
            _state.update { it.copy(positionMs = 0L) }
        } else {
            index = (index - 1 + queue.size) % queue.size
            load()
        }
    }

    override fun seekTo(positionMs: Long) {
        _state.update { it.copy(positionMs = positionMs.coerceIn(0L, it.durationMs)) }
    }

    override fun toggleShuffle() = _state.update { it.copy(shuffle = !it.shuffle) }

    override fun cycleRepeat() = _state.update {
        it.copy(
            repeat = when (it.repeat) {
                RepeatMode.OFF -> RepeatMode.ALL
                RepeatMode.ALL -> RepeatMode.ONE
                RepeatMode.ONE -> RepeatMode.OFF
            }
        )
    }
}

class FakeLibraryProvider : LibraryProvider {
    private val _songs = MutableStateFlow(sampleSongs())
    private val _playlists = MutableStateFlow(
        listOf(
            UiPlaylist(1, "Late night drive", listOf(1, 3, 5, 7)),
            UiPlaylist(2, "Focus", listOf(2, 4, 6)),
        )
    )
    override val songs: StateFlow<List<UiSong>> = _songs.asStateFlow()
    override val playlists: StateFlow<List<UiPlaylist>> = _playlists.asStateFlow()
    override val stats: StateFlow<UiStats> = MutableStateFlow(
        UiStats(
            totalListenMs = 45_600_000L,
            totalPlays = 142,
            topSongs = _songs.value.take(5).mapIndexed { i, s -> s to (24 - i * 4) },
            topArtists = listOf("Aarav Sen" to 61, "Nila Rao" to 44, "Mira Kapoor" to 21),
        )
    ).asStateFlow()

    override fun toggleFavorite(songId: Long) = _songs.update { list ->
        list.map { if (it.id == songId) it.copy(isFavorite = !it.isFavorite) else it }
    }

    override fun createPlaylist(name: String, songIds: List<Long>) = _playlists.update { list ->
        list + UiPlaylist(id = (list.maxOfOrNull { it.id } ?: 0L) + 1L, name = name, songIds = songIds)
    }

    override fun addToPlaylist(playlistId: Long, songId: Long) = _playlists.update { list ->
        list.map {
            if (it.id == playlistId && songId !in it.songIds) it.copy(songIds = it.songIds + songId) else it
        }
    }

    private fun sampleSongs(): List<UiSong> {
        val names = listOf(
            Triple("Midnight Circuit", "Aarav Sen", "Neon Hours"),
            Triple("Glass Horizon", "Nila Rao", "Skyline"),
            Triple("Slow Static", "Aarav Sen", "Neon Hours"),
            Triple("Paper Moon", "Mira Kapoor", "Lanterns"),
            Triple("Echo Garden", "Nila Rao", "Skyline"),
            Triple("Violet Hour", "Kabir Das", "After Rain"),
            Triple("Satellite", "Mira Kapoor", "Lanterns"),
            Triple("Afterglow", "Kabir Das", "After Rain"),
            Triple("Tidal", "Ishaan Roy", "Deep Blue"),
            Triple("Soft Reset", "Ishaan Roy", "Deep Blue"),
            Triple("Neon Drift", "Aarav Sen", "Neon Hours"),
            Triple("Last Train Home", "Nila Rao", "Skyline"),
        )
        return names.mapIndexed { i, (title, artist, album) ->
            UiSong(
                id = (i + 1).toLong(),
                title = title,
                artist = artist,
                album = album,
                durationMs = (170_000L + i * 13_000L),
                isFavorite = i % 4 == 0,
            )
        }
    }
}

package com.musicnova.app.ui.model

/** Plain data the UI needs. Map your own Song / player state into these in the bridge. */
data class UiSong(
    val id: Long,
    val title: String,
    val artist: String,
    val album: String = "",
    val durationMs: Long = 0L,
    val artworkUri: String? = null,
    val isFavorite: Boolean = false,
) val videoId: String? = null,

data class UiPlaylist(
    val id: Long,
    val name: String,
    val songIds: List<Long> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
)

enum class RepeatMode { OFF, ALL, ONE }

data class PlaybackUiState(
    val currentSong: UiSong? = null,
    val isPlaying: Boolean = false,
    val positionMs: Long = 0L,
    val durationMs: Long = 0L,
    val shuffle: Boolean = false,
    val repeat: RepeatMode = RepeatMode.OFF,
)

/** Listening stats for the Stats tab. Leave empty until you track play history. */
data class UiStats(
    val totalListenMs: Long = 0L,
    val totalPlays: Int = 0,
    val topSongs: List<Pair<UiSong, Int>> = emptyList(),
    val topArtists: List<Pair<String, Int>> = emptyList(),
)

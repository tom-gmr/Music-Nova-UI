package com.musicnova.app.ui.bridge

import androidx.media3.common.Player
import com.lastwave.app.playback.MusicPlayer
import com.lastwave.app.playback.PlayableTrack
import com.musicnova.app.ui.model.PlaybackUiState
import com.musicnova.app.ui.model.RepeatMode
import com.musicnova.app.ui.model.UiSong
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class MusicPlayerBridge(
    private val player: MusicPlayer
) : PlayerController {

    private val scope = CoroutineScope(
        SupervisorJob() + Dispatchers.Main.immediate
    )

    override val state: StateFlow<PlaybackUiState> =
        player.state.map { engine ->
            val track = engine.current

            PlaybackUiState(
                currentSong = track?.let {
                    UiSong(
                        id = (it.videoId ?: it.title).hashCode().toLong(),
                        title = it.title,
                        artist = it.artist,
                        album = it.album.orEmpty(),
                        durationMs = it.durationMs ?: engine.durationMs,
                        artworkUri = it.artworkUrl,
                        videoId = it.videoId
                    )
                },
                isPlaying = engine.isPlaying,
                positionMs = engine.positionMs,
                durationMs = engine.durationMs,
                shuffle = engine.shuffleEnabled,
                repeat = when (engine.repeatMode) {
                    Player.REPEAT_MODE_ALL -> RepeatMode.ALL
                    Player.REPEAT_MODE_ONE -> RepeatMode.ONE
                    else -> RepeatMode.OFF
                }
            )
        }.stateIn(
            scope,
            SharingStarted.Eagerly,
            PlaybackUiState()
        )

    override fun play(queue: List<UiSong>, startIndex: Int) {
        if (queue.isEmpty()) return
        if (queue.any { it.videoId.isNullOrBlank() }) return

        val tracks = queue.map { song ->
            PlayableTrack(
                title = song.title,
                artist = song.artist,
                album = song.album.ifBlank { null },
                artworkUrl = song.artworkUri,
                videoId = song.videoId,
                durationMs = song.durationMs.takeIf { it > 0L }
            )
        }

        player.playQueue(
            tracks = tracks,
            startIndex = startIndex.coerceIn(0, tracks.lastIndex),
            sourceLabel = "Music Nova"
        )
    }

    override fun togglePlayPause() {
        player.togglePlayPause()
    }

    override fun next() {
        player.next()
    }

    override fun previous() {
        player.previous()
    }

    override fun seekTo(positionMs: Long) {
        player.seekTo(positionMs)
    }

    override fun toggleShuffle() {
        player.toggleShuffle()
    }

    override fun cycleRepeat() {
        player.cycleRepeatMode()
    }
}

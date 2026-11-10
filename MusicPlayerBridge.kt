package com.musicnova.app.ui.bridge

/*
 * TEMPLATE - this whole file is commented out so your build does not break.
 *
 * Steps:
 *  1. Open playback/MusicPlayer.kt and note: how it exposes the current song, isPlaying,
 *     position, how you start a queue, and its next/prev/seek functions.
 *  2. Un-comment the class below and replace each TODO line with the matching call.
 *  3. In MainActivity use  NovaApp(MusicPlayerBridge(yourMusicPlayer), yourLibraryProvider).
 *
 * Do NOT edit MusicPlayer.kt itself.
 *
 * import com.musicnova.app.playback.MusicPlayer
 * import com.musicnova.app.ui.model.*
 * import kotlinx.coroutines.flow.*
 *
 * class MusicPlayerBridge(private val player: MusicPlayer) : PlayerController {
 *
 *     // TODO: map your player's state to PlaybackUiState.
 *     // If your player already has a StateFlow, use .map { ... }.stateIn(...)
 *     // If it is a Media3 / ExoPlayer, poll position every 500 ms and emit a copy.
 *     override val state: StateFlow<PlaybackUiState> = TODO()
 *
 *     override fun play(queue: List<UiSong>, startIndex: Int) {
 *         // TODO: player.playQueue(queue.map { it.id }, startIndex)
 *     }
 *     override fun togglePlayPause() { // TODO: player.togglePlayPause()
 *     }
 *     override fun next() { // TODO: player.next()
 *     }
 *     override fun previous() { // TODO: player.previous()
 *     }
 *     override fun seekTo(positionMs: Long) { // TODO: player.seekTo(positionMs)
 *     }
 *     override fun toggleShuffle() { // TODO: player.toggleShuffle()
 *     }
 *     override fun cycleRepeat() { // TODO: player.cycleRepeat()
 *     }
 * }
 */

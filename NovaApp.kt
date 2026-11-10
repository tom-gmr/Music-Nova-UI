package com.musicnova.app.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.musicnova.app.ui.bridge.LibraryProvider
import com.musicnova.app.ui.bridge.PlayerController
import com.musicnova.app.ui.components.FloatingMiniPlayer
import com.musicnova.app.ui.components.NovaBottomBar
import com.musicnova.app.ui.components.NovaTab
import com.musicnova.app.ui.model.UiPlaylist
import com.musicnova.app.ui.model.UiSong
import com.musicnova.app.ui.screens.HomeScreen
import com.musicnova.app.ui.screens.NowPlayingScreen
import com.musicnova.app.ui.screens.PlaylistsScreen
import com.musicnova.app.ui.screens.SearchScreen
import com.musicnova.app.ui.screens.SettingsScreen
import com.musicnova.app.ui.screens.StatsScreen
import com.musicnova.app.ui.theme.NovaColors
import com.musicnova.app.ui.theme.NovaTheme
import com.musicnova.app.ui.bridge.SearchController
/**
 * Root of the whole UI. Call this from setContent { }.
 * Call enableEdgeToEdge() in MainActivity before setContent.
 *
 * Layout: 3 tabs (Feed, Stats, Playlists). Search and Settings open over the tabs,
 * Now Playing slides up over everything.
 */
@Composable
fun NovaApp(
    player: PlayerController,
    library: LibraryProvider,
    searchController: SearchController,
    onSettingChanged: (key: String, value: String) -> Unit = { _, _ -> },
) { 
    NovaTheme {
        var tab by rememberSaveable { mutableStateOf(NovaTab.Feed) }
        var showSearch by rememberSaveable { mutableStateOf(false) }
        var showSettings by rememberSaveable { mutableStateOf(false) }
        var showNowPlaying by rememberSaveable { mutableStateOf(false) }
        var songForPlaylist by remember { mutableStateOf<UiSong?>(null) }

        val playback by player.state.collectAsState()
        val songs by library.songs.collectAsState()
        val playlists by library.playlists.collectAsState()
        val stats by library.stats.collectAsState()
        val current = playback.currentSong

        BackHandler(enabled = showNowPlaying) { showNowPlaying = false }
        BackHandler(enabled = showSettings && !showNowPlaying) { showSettings = false }

        val onPlay: (List<UiSong>, Int) -> Unit = { queue, index -> player.play(queue, index) }
        val onMore: (UiSong) -> Unit = { songForPlaylist = it }

        Box(Modifier.fillMaxSize().background(NovaColors.Bg)) {
            Column(Modifier.fillMaxSize()) {
                Box(Modifier.weight(1f)) {
                    when (tab) {
                        NovaTab.Feed -> HomeScreen(
                            songs, playback, onPlay,
                            onOpenSearch = { showSearch = true },
                            onOpenSettings = { showSettings = true },
                            onSongMore = onMore
                        )
                        NovaTab.Stats -> StatsScreen(stats)
                        NovaTab.Playlists -> PlaylistsScreen(
                            songs, playlists, playback, onPlay,
                            onCreatePlaylist = { library.createPlaylist(it) },
                            onSongMore = onMore
                        )
                    }

                    // Mini player floats above the content, just over the bottom bar
                    if (current != null && !showSearch) {
                        FloatingMiniPlayer(
                            playback,
                            onOpen = { showNowPlaying = true },
                            onToggle = player::togglePlayPause,
                            onNext = player::next,
                            modifier = Modifier.align(androidx.compose.ui.Alignment.BottomCenter)
                        )
                    }
                }
                NovaBottomBar(tab) { tab = it }
            }

            // Search sits over the tabs; its mini player is drawn inside this layer
            AnimatedVisibility(visible = showSearch, enter = fadeIn(), exit = fadeOut()) {
                Box(Modifier.fillMaxSize()) {
                    SearchScreen(
                        songs, playback,
                        onBack = { showSearch = false },
                        onPlay = onPlay,
                        onSongMore = onMore,
                        onSearch = searchController::searchSongs
                    )
                    if (current != null) {
                        FloatingMiniPlayer(
                            playback,
                            onOpen = { showNowPlaying = true },
                            onToggle = player::togglePlayPause,
                            onNext = player::next,
                            modifier = Modifier.align(androidx.compose.ui.Alignment.BottomCenter)
                        )
                    }
                }
            }

            AnimatedVisibility(
                visible = showSettings,
                enter = slideInHorizontally { it },
                exit = slideOutHorizontally { it }
            ) {
                SettingsScreen(onBack = { showSettings = false }, onChange = onSettingChanged)
            }

            AnimatedVisibility(
                visible = showNowPlaying && current != null,
                enter = slideInVertically { it },
                exit = slideOutVertically { it }
            ) {
                val fav = songs.firstOrNull { it.id == current?.id }?.isFavorite ?: current?.isFavorite ?: false
                NowPlayingScreen(
                    playback = playback,
                    isFavorite = fav,
                    onClose = { showNowPlaying = false },
                    onTogglePlay = player::togglePlayPause,
                    onNext = player::next,
                    onPrevious = player::previous,
                    onSeek = player::seekTo,
                    onShuffle = player::toggleShuffle,
                    onRepeat = player::cycleRepeat,
                    onFavorite = { current?.let { library.toggleFavorite(it.id) } }
                )
            }
        }

        songForPlaylist?.let { song ->
            AddToPlaylistDialog(
                song = song,
                playlists = playlists,
                onPick = { library.addToPlaylist(it.id, song.id); songForPlaylist = null },
                onDismiss = { songForPlaylist = null }
            )
        }
    }
}

@Composable
private fun AddToPlaylistDialog(
    song: UiSong,
    playlists: List<UiPlaylist>,
    onPick: (UiPlaylist) -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = NovaColors.CardHigh,
        title = { Text("Add to playlist", color = Color.White) },
        text = {
            if (playlists.isEmpty()) {
                Text("Create a playlist first from the Playlists tab.", color = NovaColors.TextSecondary)
            } else {
                Column {
                    Text(song.title, color = NovaColors.TextSecondary, modifier = Modifier.padding(bottom = 8.dp))
                    playlists.forEach { pl ->
                        Text(
                            pl.name,
                            color = Color.White,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onPick(pl) }
                                .padding(vertical = 12.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close", color = NovaColors.Rose) }
        }
    )
}

package com.musicnova.app.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.PushPin
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material.icons.rounded.SwapVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.musicnova.app.ui.components.Artwork
import com.musicnova.app.ui.components.CircleIconButton
import com.musicnova.app.ui.components.EmptyState
import com.musicnova.app.ui.components.HeaderSurface
import com.musicnova.app.ui.components.MoreButton
import com.musicnova.app.ui.components.PillShape
import com.musicnova.app.ui.components.RoseButton
import com.musicnova.app.ui.components.SongRow
import com.musicnova.app.ui.model.PlaybackUiState
import com.musicnova.app.ui.model.UiPlaylist
import com.musicnova.app.ui.model.UiSong
import com.musicnova.app.ui.theme.NovaColors
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** One row in the list. System rows (Liked / Local) are pinned and built from the library. */
private data class PlaylistItem(
    val key: String,
    val name: String,
    val songs: List<UiSong>,
    val pinned: Boolean,
    val createdAt: Long,
)

@Composable
fun PlaylistsScreen(
    songs: List<UiSong>,
    playlists: List<UiPlaylist>,
    playback: PlaybackUiState,
    onPlay: (List<UiSong>, Int) -> Unit,
    onCreatePlaylist: (String) -> Unit,
    onSongMore: (UiSong) -> Unit,
) {
    var openKey by rememberSaveable { mutableStateOf<String?>(null) }
    var showCreate by rememberSaveable { mutableStateOf(false) }
    var query by rememberSaveable { mutableStateOf("") }
    var sort by rememberSaveable { mutableIntStateOf(0) }   // 0 recent, 1 name
    val now = remember { System.currentTimeMillis() }
    val dateFmt = remember { SimpleDateFormat("MMM d, yyyy", Locale.getDefault()) }

    val rows = remember(songs, playlists, query, sort) {
        val system = listOf(
            PlaylistItem("liked", "Liked Songs", songs.filter { it.isFavorite }, true, now),
            PlaylistItem("local", "Local songs", songs, true, now),
        )
        val user = playlists.map { pl ->
            PlaylistItem(
                "pl-${pl.id}", pl.name,
                pl.songIds.mapNotNull { id -> songs.firstOrNull { it.id == id } },
                false, pl.createdAt
            )
        }.let { list -> if (sort == 1) list.sortedBy { it.name.lowercase() } else list.sortedByDescending { it.createdAt } }
        (system + user).filter { query.isBlank() || it.name.contains(query.trim(), true) }
    }
    val open = rows.firstOrNull { it.key == openKey }

    BackHandler(enabled = open != null) { openKey = null }

    if (open != null) {
        PlaylistDetail(open.name, open.songs, playback, { openKey = null }, onPlay, onSongMore)
    } else {
        Column(Modifier.fillMaxSize()) {
            HeaderSurface {
                Row(
                    Modifier.fillMaxWidth().padding(start = 24.dp, end = 16.dp, top = 14.dp, bottom = 18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f)) {
                        Text("Playlist", style = MaterialTheme.typography.headlineLarge, color = Color.White)
                        Text(
                            "${rows.size} Playlists • ${songs.size} Tracks",
                            style = MaterialTheme.typography.bodyLarge, color = NovaColors.TextSecondary
                        )
                    }
                    Icon(
                        Icons.Rounded.Add, "New playlist", tint = Color.White,
                        modifier = Modifier.size(40.dp).clip(PillShape).clickable { showCreate = true }.padding(6.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Row(
                        Modifier
                            .clip(PillShape)
                            .background(NovaColors.Chip)
                            .clickable { sort = (sort + 1) % 2 }
                            .padding(horizontal = 20.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Rounded.SwapVert, null, tint = Color.White, modifier = Modifier.size(22.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            if (sort == 0) "Sort" else "A–Z",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = Color.White
                        )
                    }
                }
            }

            Row(
                Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    Modifier
                        .weight(1f)
                        .height(60.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(NovaColors.Card)
                        .padding(horizontal = 18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Rounded.Search, null, tint = NovaColors.Rose, modifier = Modifier.size(24.dp))
                    Spacer(Modifier.width(14.dp))
                    Box(Modifier.weight(1f)) {
                        if (query.isEmpty()) Text("Search", color = NovaColors.TextSecondary, style = MaterialTheme.typography.bodyLarge)
                        BasicTextField(
                            value = query, onValueChange = { query = it }, singleLine = true,
                            textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color.White),
                            cursorBrush = SolidColor(NovaColors.Rose),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                Row(
                    Modifier
                        .height(60.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(NovaColors.RoseDim)
                        .clickable { onPlay(songs.shuffled(), 0) }
                        .padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Rounded.AutoAwesome, null, tint = NovaColors.Rose, modifier = Modifier.size(24.dp))
                    Spacer(Modifier.width(10.dp))
                    Text("Generator", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold), color = NovaColors.Rose)
                }
            }

            if (rows.isEmpty()) {
                EmptyState("No playlists found", "Tap + to create a playlist.")
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(start = 12.dp, end = 12.dp, top = 4.dp, bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(rows, key = { it.key }) { pl ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(28.dp))
                                .background(NovaColors.Card)
                                .clickable { openKey = pl.key }
                                .padding(horizontal = 20.dp, vertical = 18.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val first = pl.songs.firstOrNull()
                            if (first != null) {
                                Artwork(first, 68.dp, corner = 20.dp)
                            } else {
                                Box(
                                    Modifier.size(68.dp).clip(RoundedCornerShape(20.dp)).background(NovaColors.CardHigh),
                                    contentAlignment = Alignment.Center
                                ) { Icon(Icons.Rounded.MusicNote, null, tint = NovaColors.Rose, modifier = Modifier.size(32.dp)) }
                            }
                            Spacer(Modifier.width(18.dp))
                            Column(Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(pl.name, style = MaterialTheme.typography.titleLarge.copy(fontSize = 21.sp), color = Color.White)
                                    if (pl.pinned) {
                                        Spacer(Modifier.width(8.dp))
                                        Icon(Icons.Rounded.PushPin, "Pinned", tint = NovaColors.Rose, modifier = Modifier.size(20.dp))
                                    }
                                }
                                Text(
                                    "${pl.songs.size} tracks • ${dateFmt.format(Date(pl.createdAt))}",
                                    style = MaterialTheme.typography.bodyLarge, color = NovaColors.TextSecondary
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showCreate) {
        var name by rememberSaveable { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showCreate = false },
            containerColor = NovaColors.CardHigh,
            title = { Text("New playlist", color = Color.White) },
            text = {
                OutlinedTextField(
                    value = name, onValueChange = { name = it },
                    singleLine = true, placeholder = { Text("Playlist name") }
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (name.isNotBlank()) onCreatePlaylist(name.trim())
                    showCreate = false
                }) { Text("Create", color = NovaColors.Rose) }
            },
            dismissButton = {
                TextButton(onClick = { showCreate = false }) { Text("Cancel", color = NovaColors.TextSecondary) }
            }
        )
    }
}

@Composable
private fun PlaylistDetail(
    name: String,
    list: List<UiSong>,
    playback: PlaybackUiState,
    onBack: () -> Unit,
    onPlay: (List<UiSong>, Int) -> Unit,
    onSongMore: (UiSong) -> Unit,
) {
    val currentId = playback.currentSong?.id
    Column(Modifier.fillMaxSize()) {
        HeaderSurface {
            Row(
                Modifier.fillMaxWidth().padding(start = 14.dp, end = 16.dp, top = 14.dp, bottom = 18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircleIconButton(Icons.AutoMirrored.Rounded.ArrowBack, "Back", onBack, size = 52.dp)
                Spacer(Modifier.width(14.dp))
                Column(Modifier.weight(1f)) {
                    Text(name, style = MaterialTheme.typography.headlineMedium, color = Color.White)
                    Text("${list.size} tracks", style = MaterialTheme.typography.bodyLarge, color = NovaColors.TextSecondary)
                }
            }
        }
        if (list.isEmpty()) {
            EmptyState("Empty playlist", "Use the ⋮ menu on any song to add it here.")
            return@Column
        }
        Row(Modifier.padding(horizontal = 16.dp, vertical = 14.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            RoseButton("Play", Icons.Rounded.PlayArrow, { onPlay(list, 0) })
            RoseButton("Shuffle", Icons.Rounded.Shuffle, { onPlay(list.shuffled(), 0) }, filled = false)
        }
        LazyColumn(contentPadding = PaddingValues(bottom = 24.dp), modifier = Modifier.fillMaxSize()) {
            itemsIndexed(list, key = { _, s -> s.id }) { index, song ->
                SongRow(song, song.id == currentId, { onPlay(list, index) }, { onSongMore(song) })
            }
        }
    }
}

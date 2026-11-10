package com.musicnova.app.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.musicnova.app.ui.components.Artwork
import com.musicnova.app.ui.components.CapsLabel
import com.musicnova.app.ui.components.ChipBar
import com.musicnova.app.ui.components.CircleIconButton
import com.musicnova.app.ui.components.EmptyState
import com.musicnova.app.ui.components.GroupRow
import com.musicnova.app.ui.components.NovaChip
import com.musicnova.app.ui.components.PillShape
import com.musicnova.app.ui.components.RoseCircleButton
import com.musicnova.app.ui.components.SongRow
import com.musicnova.app.ui.model.PlaybackUiState
import com.musicnova.app.ui.model.UiSong
import com.musicnova.app.ui.theme.NovaColors
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.delay
private val genres = listOf("Pop", "Rock", "Hip-Hop", "Lo-Fi", "Indie", "Classical", "Bollywood", "Folk")

@Composable
fun SearchScreen(
    songs: List<UiSong>,
    playback: PlaybackUiState,
    onBack: () -> Unit,
    onPlay: (List<UiSong>, Int) -> Unit,
    onSongMore: (UiSong) -> Unit,
    onSearch: suspend (String) -> List<UiSong>,
) {
    BackHandler(onBack = onBack)

    var query by rememberSaveable { mutableStateOf("") }
    var tab by rememberSaveable { mutableIntStateOf(0) }
    val recents = remember { mutableStateListOf<String>() }
    val q = query.trim()
    val currentId = playback.currentSong?.id
    var onlineResults by remember { mutableStateOf<List<UiSong>>(emptyList()) }
var isSearching by remember { mutableStateOf(false) }
var searchError by remember { mutableStateOf<String?>(null) }

LaunchedEffect(q, tab) {
    if (q.isBlank() || tab != 0) {
        onlineResults = emptyList()
        searchError = null
        isSearching = false
    } else {
        delay(400)
        isSearching = true
        searchError = null

        try {
            onlineResults = onSearch(q)
        } catch (e: Exception) {
            searchError = e.message ?: "Search failed"
            onlineResults = emptyList()
        } finally {
            isSearching = false
        }
    }
}

    fun commit() {
        if (q.isNotEmpty()) {
            recents.remove(q)
            recents.add(0, q)
            if (recents.size > 8) recents.removeAt(recents.lastIndex)
        }
    }

    val trackResults = onlineResults
    val artistResults = remember(trackResults) { trackResults.groupBy { it.artist }.toList() }
    val albumResults = remember(trackResults) { trackResults.groupBy { it.album.ifBlank { "Unknown album" } }.toList() }

    Column(Modifier.fillMaxSize().background(NovaColors.Bg)) {
        // Top: back, search field, clear
        Column(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp))
                .background(NovaColors.BgSoft)
                .padding(top = 0.dp)
        ) {
            Row(
                Modifier.padding(start = 14.dp, end = 14.dp, top = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircleIconButton(Icons.AutoMirrored.Rounded.ArrowBack, "Back", onBack, size = 52.dp)
                Spacer(Modifier.width(10.dp))
                Row(
                    Modifier
                        .weight(1f)
                        .height(52.dp)
                        .clip(PillShape)
                        .background(NovaColors.Chip)
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Rounded.Search, null, tint = NovaColors.Rose, modifier = Modifier.size(24.dp))
                    Spacer(Modifier.width(12.dp))
                    Box(Modifier.weight(1f)) {
                        if (query.isEmpty()) {
                            Text("Search your music…", color = NovaColors.TextMuted, style = MaterialTheme.typography.bodyLarge)
                        }
                        BasicTextField(
                            value = query,
                            onValueChange = { query = it },
                            singleLine = true,
                            textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color.White),
                            cursorBrush = SolidColor(NovaColors.Rose),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            keyboardActions = KeyboardActions(onSearch = { commit() }),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    if (query.isNotEmpty()) {
                        Icon(
                            Icons.Rounded.Close, "Clear", tint = NovaColors.TextSecondary,
                            modifier = Modifier.size(24.dp).clickable { query = "" }
                        )
                    }
                }
            }
            Spacer(Modifier.height(14.dp))
            ChipBar(Modifier.padding(horizontal = 12.dp).padding(bottom = 14.dp)) {
                listOf("Tracks", "Artists", "Albums").forEachIndexed { i, label ->
                    NovaChip(label, tab == i) { tab = i }
                }
            }
        }

        if (q.isEmpty()) {
            SearchIdle(
                recents = recents,
                onPickRecent = { query = it },
                onRemove = { recents.remove(it) },
                onClear = { recents.clear() },
                onGenre = { query = it }
            )
        } else when (tab) {
            0 -> {
                if (trackResults.isEmpty()) {
                    EmptyState("No results", "Nothing matches \"$q\".")
                } else {
                    LazyColumn(contentPadding = PaddingValues(top = 14.dp, bottom = 24.dp), modifier = Modifier.fillMaxSize()) {
                        val top = trackResults.first()
                        item {
                            Row(
                                Modifier
                                    .padding(horizontal = 12.dp)
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(30.dp))
                                    .background(NovaColors.BgSoft)
                                    .border(1.dp, NovaColors.OutlineSoft, RoundedCornerShape(30.dp))
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Artwork(top, 84.dp, corner = 18.dp)
                                Spacer(Modifier.width(16.dp))
                                Column(Modifier.weight(1f)) {
                                    Box(
                                        Modifier.clip(RoundedCornerShape(8.dp)).background(NovaColors.CardHigh)
                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                    ) { CapsLabel("Top song") }
                                    Spacer(Modifier.height(6.dp))
                                    Text(top.title, style = MaterialTheme.typography.titleLarge, color = Color.White,
                                        maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    Text(top.artist, style = MaterialTheme.typography.bodyLarge, color = NovaColors.TextSecondary,
                                        maxLines = 1, overflow = TextOverflow.Ellipsis)
                                }
                                RoseCircleButton(Icons.Rounded.PlayArrow, "Play", { commit(); onPlay(trackResults, 0) }, size = 58.dp)
                            }
                            Spacer(Modifier.height(10.dp))
                        }
                        itemsIndexed(trackResults, key = { _, s -> s.id }) { index, song ->
                            SongRow(song, song.id == currentId, { commit(); onPlay(trackResults, index) }, { onSongMore(song) })
                        }
                    }
                }
            }
            1 -> LazyColumn(contentPadding = PaddingValues(vertical = 14.dp), modifier = Modifier.fillMaxSize()) {
                if (artistResults.isEmpty()) item { EmptyState("No artists", "Nothing matches \"$q\".") }
                items(artistResults.size) { i ->
                    val (name, list) = artistResults[i]
                    GroupRow(name, "${list.size} ${if (list.size == 1) "song" else "songs"}", list.first(), true) {
                        commit(); onPlay(list, 0)
                    }
                }
            }
            else -> LazyColumn(contentPadding = PaddingValues(vertical = 14.dp), modifier = Modifier.fillMaxSize()) {
                if (albumResults.isEmpty()) item { EmptyState("No albums", "Nothing matches \"$q\".") }
                items(albumResults.size) { i ->
                    val (name, list) = albumResults[i]
                    GroupRow(name, "${list.size} ${if (list.size == 1) "song" else "songs"}", list.first(), false) {
                        commit(); onPlay(list, 0)
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchIdle(
    recents: List<String>,
    onPickRecent: (String) -> Unit,
    onRemove: (String) -> Unit,
    onClear: () -> Unit,
    onGenre: (String) -> Unit,
) {
    Column(Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
        if (recents.isNotEmpty()) {
            Row(Modifier.fillMaxWidth().padding(top = 26.dp, bottom = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("Recent searches", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = Color.White, modifier = Modifier.weight(1f))
                Text("Clear all", style = MaterialTheme.typography.titleMedium, color = NovaColors.Rose,
                    modifier = Modifier.clickable(onClick = onClear).padding(6.dp))
            }
            recents.forEach { r ->
                Row(
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).clickable { onPickRecent(r) }.padding(vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Rounded.History, null, tint = NovaColors.TextSecondary, modifier = Modifier.size(26.dp))
                    Spacer(Modifier.width(18.dp))
                    Text(r, style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp), color = Color.White, modifier = Modifier.weight(1f))
                    Icon(Icons.Rounded.Close, "Remove", tint = NovaColors.TextSecondary,
                        modifier = Modifier.size(24.dp).clickable { onRemove(r) })
                }
            }
        }

        Row(Modifier.padding(top = 26.dp, bottom = 14.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Rounded.Explore, null, tint = NovaColors.Rose, modifier = Modifier.size(30.dp))
            Spacer(Modifier.width(14.dp))
            Text("Explore genres and moods", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold), color = Color.White)
        }
        Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            genres.forEach { g ->
                Box(
                    Modifier
                        .clip(PillShape)
                        .background(NovaColors.Chip)
                        .border(1.dp, NovaColors.Outline, PillShape)
                        .clickable { onGenre(g) }
                        .padding(horizontal = 24.dp, vertical = 13.dp)
                ) {
                    Text(g, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold), color = Color.White)
                }
            }
        }
    }
}

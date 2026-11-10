package com.musicnova.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.musicnova.app.ui.components.Artwork
import com.musicnova.app.ui.components.CapsLabel
import com.musicnova.app.ui.components.ChipBar
import com.musicnova.app.ui.components.CircleIconButton
import com.musicnova.app.ui.components.EmptyState
import com.musicnova.app.ui.components.HeaderSurface
import com.musicnova.app.ui.components.NovaChip
import com.musicnova.app.ui.components.PillShape
import com.musicnova.app.ui.components.RoseButton
import com.musicnova.app.ui.components.SectionBlock
import com.musicnova.app.ui.components.SongRow
import com.musicnova.app.ui.model.PlaybackUiState
import com.musicnova.app.ui.model.UiSong
import com.musicnova.app.ui.theme.NovaBrush
import com.musicnova.app.ui.theme.NovaColors
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private fun greeting(): String {
    val h = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when {
        h < 12 -> "Good morning"
        h < 17 -> "Good afternoon"
        else -> "Good evening"
    }
}

@Composable
fun HomeScreen(
    songs: List<UiSong>,
    playback: PlaybackUiState,
    onPlay: (List<UiSong>, Int) -> Unit,
    onOpenSearch: () -> Unit,
    onOpenSettings: () -> Unit,
    onSongMore: (UiSong) -> Unit,
) {
    var filter by rememberSaveable { mutableIntStateOf(0) }
    val favorites = songs.filter { it.isFavorite }
    val recent = songs.takeLast(12).reversed()
    val artists = songs.groupBy { it.artist }.toList()
    val currentId = playback.currentSong?.id
    val dateText = remember { SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()).format(Date()) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            HeaderSurface {
                Row(
                    Modifier.fillMaxWidth().padding(start = 24.dp, end = 20.dp, top = 14.dp, bottom = 18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Home",
                        style = MaterialTheme.typography.headlineLarge,
                        color = Color.White,
                        modifier = Modifier.weight(1f)
                    )
                    CircleIconButton(Icons.Rounded.Search, "Search", onOpenSearch, size = 54.dp)
                    Spacer(Modifier.width(12.dp))
                    Box(
                        Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(NovaColors.RoseDim)
                            .border(2.dp, NovaColors.Rose.copy(alpha = 0.6f), CircleShape)
                            .clickable(onClick = onOpenSettings),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Rounded.AccountCircle, "Settings", tint = Color.White, modifier = Modifier.size(30.dp))
                    }
                }
            }
        }

        item {
            ChipBar(Modifier.padding(horizontal = 12.dp, vertical = 12.dp)) {
                NovaChip("All", filter == 0) { filter = 0 }
                NovaChip("Favorites", filter == 1) { filter = 1 }
                NovaChip("Recently added", filter == 2) { filter = 2 }
            }
        }

        item {
            Column(Modifier.padding(start = 24.dp, end = 24.dp, top = 12.dp, bottom = 12.dp)) {
                Text(greeting(), style = MaterialTheme.typography.displayMedium, color = Color.White)
                CapsLabel(dateText)
            }
        }

        if (songs.isEmpty()) {
            item {
                EmptyState(
                    "No songs yet",
                    "Songs from your phone will show up here once they are scanned.",
                    Modifier.padding(top = 40.dp)
                )
            }
            return@LazyColumn
        }

        when (filter) {
            0 -> {
                item {
                    Column(
                        Modifier
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(30.dp))
                            .background(NovaBrush.hero)
                            .padding(22.dp)
                    ) {
                        Row(
                            Modifier
                                .clip(PillShape)
                                .background(Color(0x33FFFFFF))
                                .padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Rounded.AutoAwesome, null, tint = NovaColors.TextSecondary, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            CapsLabel("Made for you")
                        }
                        Spacer(Modifier.height(18.dp))
                        Text("Your mix", style = MaterialTheme.typography.headlineLarge, color = Color.White)
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "An endless shuffle shaped by your library",
                            style = MaterialTheme.typography.bodyLarge,
                            color = NovaColors.TextSecondary
                        )
                        Spacer(Modifier.height(22.dp))
                        RoseButton("Play", Icons.Rounded.PlayArrow, { onPlay(songs.shuffled(), 0) })
                    }
                }

                item {
                    SectionBlock("Your artists", "Tap a name to start playing") {
                        Row(
                            Modifier.horizontalScroll(rememberScrollState()).padding(horizontal = 14.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            artists.forEach { (name, list) ->
                                Row(
                                    Modifier
                                        .clip(PillShape)
                                        .background(NovaColors.Chip)
                                        .border(1.dp, NovaColors.OutlineSoft, PillShape)
                                        .clickable { onPlay(list, 0) }
                                        .padding(horizontal = 18.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(Modifier.size(10.dp).clip(CircleShape).background(NovaColors.Rose))
                                    Spacer(Modifier.width(10.dp))
                                    Text(name, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold), color = Color.White)
                                }
                            }
                        }
                    }
                }

                item {
                    SectionBlock(
                        "Recently added", "Fresh on your phone",
                        actionText = "See all", onAction = { filter = 2 }
                    ) {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 14.dp),
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            itemsIndexed(recent.take(8), key = { _, s -> s.id }) { index, song ->
                                Column(
                                    Modifier
                                        .width(160.dp)
                                        .clip(RoundedCornerShape(18.dp))
                                        .clickable { onPlay(recent, index) }
                                ) {
                                    Box {
                                        Artwork(song, 160.dp, corner = 18.dp)
                                        Box(
                                            Modifier
                                                .padding(10.dp)
                                                .clip(PillShape)
                                                .background(NovaColors.Rose)
                                                .padding(horizontal = 12.dp, vertical = 5.dp)
                                        ) { CapsLabel("New", color = NovaColors.OnRose) }
                                    }
                                    Spacer(Modifier.height(8.dp))
                                    Text(song.title, style = MaterialTheme.typography.titleMedium, color = Color.White,
                                        maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    Text(song.artist, style = MaterialTheme.typography.bodyMedium, color = NovaColors.TextSecondary,
                                        maxLines = 1, overflow = TextOverflow.Ellipsis)
                                }
                            }
                        }
                    }
                }
            }

            1 -> {
                if (favorites.isEmpty()) {
                    item { EmptyState("No favorites yet", "Tap the heart in the player to add songs here.") }
                } else {
                    itemsIndexed(favorites, key = { _, s -> "fav-${s.id}" }) { index, song ->
                        SongRow(song, song.id == currentId, { onPlay(favorites, index) }, { onSongMore(song) })
                    }
                }
            }

            else -> {
                itemsIndexed(recent, key = { _, s -> "rec-${s.id}" }) { index, song ->
                    SongRow(song, song.id == currentId, { onPlay(recent, index) }, { onSongMore(song) })
                }
            }
        }
    }
}

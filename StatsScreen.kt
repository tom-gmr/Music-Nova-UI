package com.musicnova.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.musicnova.app.ui.components.Artwork
import com.musicnova.app.ui.components.CapsLabel
import com.musicnova.app.ui.components.EmptyState
import com.musicnova.app.ui.components.HeaderSurface
import com.musicnova.app.ui.components.SectionBlock
import com.musicnova.app.ui.model.UiStats
import com.musicnova.app.ui.theme.NovaColors

@Composable
fun StatsScreen(stats: UiStats) {
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 24.dp)) {
        item {
            HeaderSurface {
                Column(Modifier.padding(start = 24.dp, end = 20.dp, top = 14.dp, bottom = 18.dp)) {
                    Text("Stats", style = MaterialTheme.typography.headlineLarge, color = Color.White)
                    Text("What you listen to the most", style = MaterialTheme.typography.bodyLarge, color = NovaColors.TextSecondary)
                }
            }
        }

        if (stats.totalPlays == 0) {
            item {
                EmptyState(
                    "No listening data yet",
                    "Play a few songs and your top tracks and artists will show up here.",
                    Modifier.padding(top = 40.dp)
                )
            }
            return@LazyColumn
        }

        item {
            Row(
                Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard("Listening time", "${stats.totalListenMs / 3_600_000L} h ${(stats.totalListenMs / 60_000L) % 60} m", Modifier.weight(1f))
                StatCard("Plays", stats.totalPlays.toString(), Modifier.weight(1f))
            }
        }

        item {
            SectionBlock("Top songs", "Your most played") {
                stats.topSongs.forEachIndexed { i, (song, plays) ->
                    Row(Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 7.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("${i + 1}", style = MaterialTheme.typography.titleLarge, color = NovaColors.Rose, modifier = Modifier.width(28.dp))
                        Artwork(song, 52.dp, corner = 14.dp)
                        Spacer(Modifier.width(14.dp))
                        Column(Modifier.weight(1f)) {
                            Text(song.title, style = MaterialTheme.typography.titleMedium, color = Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text(song.artist, style = MaterialTheme.typography.bodyMedium, color = NovaColors.TextSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                        Text("$plays plays", style = MaterialTheme.typography.labelMedium, color = NovaColors.TextSecondary)
                    }
                }
            }
        }

        item {
            SectionBlock("Top artists") {
                val max = (stats.topArtists.maxOfOrNull { it.second } ?: 1).toFloat()
                stats.topArtists.forEach { (name, plays) ->
                    Column(Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 8.dp)) {
                        Row {
                            Text(name, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold), color = Color.White, modifier = Modifier.weight(1f))
                            Text("$plays", style = MaterialTheme.typography.labelMedium, color = NovaColors.TextSecondary)
                        }
                        Spacer(Modifier.height(8.dp))
                        Row(Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)).background(NovaColors.Chip)) {
                            Spacer(Modifier.fillMaxWidth(plays / max).height(8.dp).background(NovaColors.Rose))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(label: String, value: String, modifier: Modifier) {
    Column(
        modifier
            .clip(RoundedCornerShape(26.dp))
            .background(NovaColors.Card)
            .padding(20.dp)
    ) {
        CapsLabel(label)
        Spacer(Modifier.height(8.dp))
        Text(value, style = MaterialTheme.typography.headlineMedium, color = Color.White)
    }
}

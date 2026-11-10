package com.musicnova.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.QueueMusic
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.musicnova.app.ui.model.PlaybackUiState
import com.musicnova.app.ui.model.UiSong
import com.musicnova.app.ui.theme.LabelCaps
import com.musicnova.app.ui.theme.NovaBrush
import com.musicnova.app.ui.theme.NovaColors

val PillShape = RoundedCornerShape(percent = 50)

fun formatTime(ms: Long): String {
    val total = (ms / 1000).coerceAtLeast(0)
    return "%d:%02d".format(total / 60, total % 60)
}

// ---------------------------------------------------------------------------
// Artwork. Gradient placeholder; swap for Coil's AsyncImage(model = song.artworkUri)
// once you have real covers.
// ---------------------------------------------------------------------------

private val artworkPalette = listOf(
    Color(0xFF9C6B6B) to Color(0xFF2A1414),
    Color(0xFF8A7AA8) to Color(0xFF1C1430),
    Color(0xFFB59A5E) to Color(0xFF2B2110),
    Color(0xFF6B8F9C) to Color(0xFF10202A),
    Color(0xFFB06C8F) to Color(0xFF2A1022),
    Color(0xFF7A9C6B) to Color(0xFF14240F),
)

fun artworkBrush(song: UiSong?): Brush {
    val seed = ((song?.title ?: "").hashCode() and 0x7fffffff) % artworkPalette.size
    val (c1, c2) = artworkPalette[seed]
    return Brush.linearGradient(listOf(c1, c2))
}

@Composable
fun Artwork(song: UiSong?, size: Dp, modifier: Modifier = Modifier, corner: Dp = 14.dp) {
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(corner))
            .background(artworkBrush(song)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            Icons.Rounded.MusicNote, null,
            tint = Color.White.copy(alpha = 0.8f),
            modifier = Modifier.size(size * 0.42f)
        )
    }
}

// ---------------------------------------------------------------------------
// Buttons, chips, labels
// ---------------------------------------------------------------------------

@Composable
fun CircleIconButton(
    icon: ImageVector,
    description: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    container: Color = NovaColors.Chip,
    tint: Color = Color.White,
    outlined: Boolean = false,
) {
    Box(
        modifier
            .size(size)
            .clip(CircleShape)
            .background(container)
            .then(if (outlined) Modifier.border(1.dp, NovaColors.Outline, CircleShape) else Modifier)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, description, tint = tint, modifier = Modifier.size(size * 0.5f))
    }
}

@Composable
fun RoseCircleButton(icon: ImageVector, description: String, onClick: () -> Unit, size: Dp = 54.dp) {
    CircleIconButton(icon, description, onClick, size = size, container = NovaColors.Rose, tint = NovaColors.OnRose)
}

@Composable
fun RoseButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    filled: Boolean = true,
) {
    Row(
        modifier
            .clip(PillShape)
            .background(if (filled) NovaColors.Rose else NovaColors.RoseDim)
            .clickable(onClick = onClick)
            .padding(horizontal = 26.dp, vertical = 15.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(icon, null, tint = if (filled) NovaColors.OnRose else NovaColors.Rose, modifier = Modifier.size(22.dp))
        Spacer(Modifier.width(8.dp))
        Text(
            text,
            color = if (filled) NovaColors.OnRose else NovaColors.Rose,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
        )
    }
}

@Composable
fun NovaChip(text: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        Modifier
            .clip(PillShape)
            .background(if (selected) NovaColors.Rose else NovaColors.Chip)
            .clickable(onClick = onClick)
            .padding(horizontal = 22.dp, vertical = 12.dp)
    ) {
        Text(
            text,
            color = if (selected) NovaColors.OnRose else NovaColors.TextPrimary,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
        )
    }
}

/** Rounded dark tray that holds a scrollable row of chips (Home filters, Search tabs). */
@Composable
fun ChipBar(modifier: Modifier = Modifier, content: @Composable RowScope.() -> Unit) {
    Row(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(30.dp))
            .background(NovaColors.Card)
            .padding(6.dp)
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        content = content
    )
}

@Composable
fun CapsLabel(text: String, modifier: Modifier = Modifier, color: Color = NovaColors.TextSecondary) {
    Text(text.uppercase(), style = LabelCaps, color = color, modifier = modifier)
}

@Composable
fun MoreButton(onClick: () -> Unit) {
    Box(
        Modifier
            .size(42.dp)
            .clip(RoundedCornerShape(15.dp))
            .background(Color(0xFF2B1F1F))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(Icons.Rounded.MoreVert, "More", tint = Color.White, modifier = Modifier.size(22.dp))
    }
}

/** Top header with rounded bottom corners, used by Home / Playlists / Stats. */
@Composable
fun HeaderSurface(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    val shape = RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp)
    Column(
        modifier
            .fillMaxWidth()
            .clip(shape)
            .background(NovaBrush.header)
            .border(1.dp, NovaColors.OutlineSoft, shape)
            .statusBarsPadding(),
        content = content
    )
}

/** Dark block with a title, optional subtitle and optional "See all" pill. */
@Composable
fun SectionBlock(
    title: String,
    subtitle: String? = null,
    actionText: String? = null,
    onAction: () -> Unit = {},
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        Modifier
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(26.dp))
            .background(NovaColors.BgSoft)
            .padding(vertical = 16.dp)
    ) {
        Row(Modifier.padding(horizontal = 18.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleLarge, color = NovaColors.TextPrimary)
                if (subtitle != null) {
                    Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = NovaColors.TextSecondary,
                        maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
            if (actionText != null) {
                Row(
                    Modifier
                        .clip(PillShape)
                        .background(Color(0xFF2B1F1F))
                        .clickable(onClick = onAction)
                        .padding(start = 12.dp, end = 18.dp, top = 9.dp, bottom = 9.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Rounded.ChevronRight, null, tint = Color.White, modifier = Modifier.size(20.dp))
                    Text(actionText, color = Color.White, style = MaterialTheme.typography.titleMedium)
                }
            }
        }
        Spacer(Modifier.height(14.dp))
        content()
    }
}

@Composable
fun EmptyState(title: String, subtitle: String, modifier: Modifier = Modifier) {
    Column(
        modifier.fillMaxWidth().padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Rounded.MusicNote, null, tint = NovaColors.TextMuted, modifier = Modifier.size(48.dp))
        Spacer(Modifier.height(12.dp))
        Text(title, style = MaterialTheme.typography.titleMedium, color = NovaColors.TextPrimary, textAlign = TextAlign.Center)
        Spacer(Modifier.height(4.dp))
        Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = NovaColors.TextSecondary, textAlign = TextAlign.Center)
    }
}

@Composable
fun Segmented(options: List<String>, selected: Int, onSelect: (Int) -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier
            .fillMaxWidth()
            .clip(PillShape)
            .background(NovaColors.Chip)
            .padding(4.dp)
    ) {
        options.forEachIndexed { i, label ->
            val sel = i == selected
            Box(
                Modifier
                    .weight(1f)
                    .clip(PillShape)
                    .background(if (sel) NovaColors.Rose else Color.Transparent)
                    .clickable { onSelect(i) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    label,
                    color = if (sel) NovaColors.OnRose else NovaColors.TextSecondary,
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                )
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Rows
// ---------------------------------------------------------------------------

@Composable
fun SongRow(
    song: UiSong,
    isCurrent: Boolean,
    onClick: () -> Unit,
    onMore: (() -> Unit)? = null,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Artwork(song, 64.dp, corner = 14.dp)
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(
                song.title,
                style = MaterialTheme.typography.titleMedium,
                color = if (isCurrent) NovaColors.Rose else NovaColors.TextPrimary,
                maxLines = 1, overflow = TextOverflow.Ellipsis
            )
            Text(
                if (song.album.isBlank()) song.artist else "${song.artist} • ${song.album}",
                style = MaterialTheme.typography.bodyMedium,
                color = NovaColors.TextSecondary,
                maxLines = 1, overflow = TextOverflow.Ellipsis
            )
        }
        if (onMore != null) {
            Spacer(Modifier.width(10.dp))
            MoreButton(onMore)
        }
    }
}

@Composable
fun GroupRow(name: String, subtitle: String, cover: UiSong?, circle: Boolean, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Artwork(cover, 64.dp, corner = if (circle) 32.dp else 14.dp)
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(name, style = MaterialTheme.typography.titleMedium, color = NovaColors.TextPrimary,
                maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = NovaColors.TextSecondary)
        }
    }
}

// ---------------------------------------------------------------------------
// Floating pill mini player + bottom bar
// (For a true frosted-glass blur behind the pill, add the Haze library later.)
// ---------------------------------------------------------------------------

@Composable
fun FloatingMiniPlayer(
    playback: PlaybackUiState,
    onOpen: () -> Unit,
    onToggle: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val song = playback.currentSong ?: return
    Row(
        modifier
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .fillMaxWidth()
            .height(88.dp)
            .clip(PillShape)
            .background(NovaColors.Scrim)
            .border(1.dp, Brush.verticalGradient(listOf(Color(0x66FFFFFF), Color(0x14FFFFFF))), PillShape)
            .clickable(onClick = onOpen)
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Artwork(song, 58.dp, corner = 18.dp)
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(song.title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(song.artist, style = MaterialTheme.typography.bodyMedium,
                color = NovaColors.TextSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        RoseCircleButton(
            if (playback.isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
            "Play or pause", onToggle, size = 54.dp
        )
        Spacer(Modifier.width(8.dp))
        CircleIconButton(
            Icons.Rounded.SkipNext, "Next", onNext,
            size = 54.dp, container = NovaColors.RoseDim
        )
    }
}

enum class NovaTab(val label: String, val icon: ImageVector) {
    Feed("Feed", Icons.Rounded.Home),
    Stats("Stats", Icons.Rounded.BarChart),
    Playlists("Playlists", Icons.Rounded.QueueMusic),
}

@Composable
fun NovaBottomBar(selected: NovaTab, onSelect: (NovaTab) -> Unit) {
    Column(Modifier.fillMaxWidth().background(Color(0xFF0A0808))) {
        Box(Modifier.fillMaxWidth().height(1.dp).background(NovaColors.OutlineSoft))
        Row(
            Modifier.fillMaxWidth().navigationBarsPadding().padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            NovaTab.values().forEach { tab ->
                val sel = tab == selected
                Column(
                    Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { onSelect(tab) },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        Modifier
                            .clip(PillShape)
                            .background(if (sel) NovaColors.CardHigh else Color.Transparent)
                            .padding(horizontal = 26.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            tab.icon, tab.label,
                            tint = if (sel) Color.White else NovaColors.TextSecondary,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    Text(
                        tab.label,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontSize = 13.sp,
                            fontWeight = if (sel) FontWeight.Bold else FontWeight.Medium
                        ),
                        color = if (sel) Color.White else NovaColors.TextSecondary
                    )
                }
            }
        }
    }
}

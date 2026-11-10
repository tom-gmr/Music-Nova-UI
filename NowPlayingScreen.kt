package com.musicnova.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.FormatQuote
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Repeat
import androidx.compose.material.icons.rounded.RepeatOne
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material.icons.rounded.SmartDisplay
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.musicnova.app.ui.components.CapsLabel
import com.musicnova.app.ui.components.PillShape
import com.musicnova.app.ui.components.artworkBrush
import com.musicnova.app.ui.components.formatTime
import com.musicnova.app.ui.model.PlaybackUiState
import com.musicnova.app.ui.model.RepeatMode
import com.musicnova.app.ui.theme.NovaColors

/**
 * Full-bleed cover on top, dark purple-maroon fade at the bottom, outlined round controls.
 * To show a real cover, put Coil's AsyncImage(model = song.artworkUri) where the gradient Box is.
 */
@Composable
fun NowPlayingScreen(
    playback: PlaybackUiState,
    isFavorite: Boolean,
    onClose: () -> Unit,
    onTogglePlay: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onSeek: (Long) -> Unit,
    onShuffle: () -> Unit,
    onRepeat: () -> Unit,
    onFavorite: () -> Unit,
) {
    val song = playback.currentSong ?: return

    var dragging by remember { mutableStateOf(false) }
    var dragValue by remember { mutableFloatStateOf(0f) }
    var swipe by remember { mutableFloatStateOf(0f) }
    val duration = playback.durationMs.coerceAtLeast(1L)
    val shownPos = if (dragging) dragValue else playback.positionMs.toFloat()
    val remaining = (duration - shownPos.toLong()).coerceAtLeast(0L)

    Box(
        Modifier
            .fillMaxSize()
            .background(Color(0xFF14040F))
            .pointerInput(Unit) {
                detectVerticalDragGestures(
                    onDragStart = { swipe = 0f },
                    onDragEnd = { if (swipe > 180f) onClose() },
                    onDragCancel = { swipe = 0f }
                ) { _, dy -> swipe += dy }
            }
    ) {
        // Cover fills the top ~62 %
        Box(
            Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.66f)
                .background(artworkBrush(song)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Rounded.MusicNote, null, tint = Color.White.copy(alpha = 0.35f), modifier = Modifier.size(140.dp))
        }
        // Fade into the dark purple bottom
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0.0f to Color(0x66000000),
                        0.30f to Color.Transparent,
                        0.52f to Color(0x66280A3C),
                        0.68f to Color(0xF2200A30),
                        1.0f to Color(0xFF0C0212)
                    )
                )
        )

        Column(
            Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 18.dp)
        ) {
            // Top bar
            Row(Modifier.fillMaxWidth().padding(top = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                GlassCircle(Icons.Rounded.KeyboardArrowDown, "Close", onClose, 50.dp)
                CapsLabel(
                    "Now playing", color = Color.White,
                    modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                )
                GlassCircle(Icons.Rounded.MoreVert, "More", {}, 50.dp)
            }

            Spacer(Modifier.weight(1f))

            // Title row
            Row(Modifier.fillMaxWidth().padding(horizontal = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(
                        song.title,
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        song.artist,
                        style = MaterialTheme.typography.titleMedium,
                        color = NovaColors.TextSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(Modifier.width(8.dp))
                OutlinedCircle(
                    if (isFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                    "Favorite", onFavorite, 56.dp,
                    tint = if (isFavorite) NovaColors.Heart else Color.White
                )
                Spacer(Modifier.width(8.dp))
                OutlinedCircle(Icons.Rounded.FormatQuote, "Lyrics", {}, 56.dp)
            }

            Spacer(Modifier.height(22.dp))

            Slider(
                value = shownPos.coerceIn(0f, duration.toFloat()),
                onValueChange = { dragging = true; dragValue = it },
                onValueChangeFinished = { onSeek(dragValue.toLong()); dragging = false },
                valueRange = 0f..duration.toFloat(),
                colors = SliderDefaults.colors(
                    thumbColor = Color(0xFFC9B6FF),
                    activeTrackColor = Color(0xFFC9B6FF),
                    inactiveTrackColor = Color(0x44FFFFFF)
                )
            )
            Row(Modifier.fillMaxWidth().padding(horizontal = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(formatTime(shownPos.toLong()), style = MaterialTheme.typography.bodyMedium, color = NovaColors.TextSecondary)
                Text("−${formatTime(remaining)}", style = MaterialTheme.typography.bodyMedium, color = NovaColors.TextSecondary)
            }

            // "Audio >" chip
            Box(Modifier.fillMaxWidth().padding(top = 4.dp), contentAlignment = Alignment.Center) {
                Row(
                    Modifier.clip(PillShape).background(Color(0x66000000)).padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Rounded.GraphicEq, null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Audio  ›", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold), color = Color.White)
                }
            }

            Spacer(Modifier.height(16.dp))

            // prev | big Play pill | next
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                OutlinedCircle(Icons.Rounded.SkipPrevious, "Previous", onPrevious, 76.dp)
                Spacer(Modifier.width(14.dp))
                Row(
                    Modifier
                        .weight(1f)
                        .height(76.dp)
                        .clip(PillShape)
                        .background(Color(0x33000000))
                        .border(1.dp, Color(0x40FFFFFF), PillShape)
                        .clickable(onClick = onTogglePlay),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        if (playback.isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                        null, tint = Color.White, modifier = Modifier.size(36.dp)
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        if (playback.isPlaying) "Pause" else "Play",
                        style = MaterialTheme.typography.titleLarge, color = Color.White, textAlign = TextAlign.Center
                    )
                }
                Spacer(Modifier.width(14.dp))
                OutlinedCircle(Icons.Rounded.SkipNext, "Next", onNext, 76.dp)
            }

            Spacer(Modifier.height(18.dp))

            // shuffle | volume + video | repeat
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedPill(Modifier.weight(1f)) {
                    Icon(
                        Icons.Rounded.Shuffle, "Shuffle",
                        tint = if (playback.shuffle) Color(0xFFC9B6FF) else Color.White,
                        modifier = Modifier.size(26.dp).clickable(onClick = onShuffle)
                    )
                }
                OutlinedPill(Modifier.weight(1f)) {
                    Icon(Icons.Rounded.VolumeUp, "Volume", tint = Color.White, modifier = Modifier.size(26.dp))
                    Spacer(Modifier.width(18.dp))
                    Box(Modifier.width(1.dp).height(24.dp).background(Color(0x55FFFFFF)))
                    Spacer(Modifier.width(18.dp))
                    Icon(Icons.Rounded.SmartDisplay, "Video", tint = Color.White, modifier = Modifier.size(26.dp))
                }
                OutlinedPill(Modifier.weight(1f)) {
                    Icon(
                        if (playback.repeat == RepeatMode.ONE) Icons.Rounded.RepeatOne else Icons.Rounded.Repeat,
                        "Repeat",
                        tint = if (playback.repeat == RepeatMode.OFF) Color.White else Color(0xFFC9B6FF),
                        modifier = Modifier.size(26.dp).clickable(onClick = onRepeat)
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
private fun GlassCircle(icon: ImageVector, desc: String, onClick: () -> Unit, size: androidx.compose.ui.unit.Dp) {
    Box(
        Modifier.size(size).clip(CircleShape).background(Color(0x55000000)).clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) { Icon(icon, desc, tint = Color.White, modifier = Modifier.size(size * 0.52f)) }
}

@Composable
private fun OutlinedCircle(
    icon: ImageVector, desc: String, onClick: () -> Unit,
    size: androidx.compose.ui.unit.Dp, tint: Color = Color.White,
) {
    Box(
        Modifier
            .size(size)
            .clip(CircleShape)
            .background(Color(0x33000000))
            .border(1.dp, Color(0x40FFFFFF), CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) { Icon(icon, desc, tint = tint, modifier = Modifier.size(size * 0.46f)) }
}

@Composable
private fun OutlinedPill(modifier: Modifier, content: @Composable androidx.compose.foundation.layout.RowScope.() -> Unit) {
    Row(
        modifier
            .height(64.dp)
            .clip(PillShape)
            .background(Color(0x33000000))
            .border(1.dp, Color(0x40FFFFFF), PillShape),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        content = content
    )
}

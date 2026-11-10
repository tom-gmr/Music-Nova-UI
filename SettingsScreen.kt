package com.musicnova.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.musicnova.app.ui.components.CapsLabel
import com.musicnova.app.ui.components.CircleIconButton
import com.musicnova.app.ui.components.HeaderSurface
import com.musicnova.app.ui.components.Segmented
import com.musicnova.app.ui.theme.NovaColors

/**
 * Settings UI. Values live in this screen only; hook them to your engine with [onChange]
 * (key, value) -> e.g. ("audio_quality", "High"), ("crossfade_sec", "4"), ("gapless", "true").
 */
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onChange: (key: String, value: String) -> Unit = { _, _ -> },
) {
    var quality by rememberSaveable { mutableIntStateOf(1) }
    var crossfade by rememberSaveable { mutableFloatStateOf(0f) }
    var gapless by rememberSaveable { mutableStateOf(true) }
    var normalize by rememberSaveable { mutableStateOf(false) }
    var pauseOnUnplug by rememberSaveable { mutableStateOf(true) }

    Column(Modifier.fillMaxSize().background(NovaColors.Bg)) {
        HeaderSurface {
            Row(
                Modifier.fillMaxWidth().padding(start = 14.dp, end = 16.dp, top = 14.dp, bottom = 18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircleIconButton(Icons.AutoMirrored.Rounded.ArrowBack, "Back", onBack, size = 52.dp)
                Spacer(Modifier.width(14.dp))
                Text("Settings", style = MaterialTheme.typography.headlineMedium, color = Color.White)
            }
        }

        Column(Modifier.verticalScroll(rememberScrollState()).padding(horizontal = 12.dp, vertical = 8.dp)) {
            Group("Audio") {
                Text("Quality", style = MaterialTheme.typography.titleMedium, color = Color.White)
                Spacer(Modifier.height(10.dp))
                val labels = listOf("Normal", "High", "Lossless")
                Segmented(labels, quality, { quality = it; onChange("audio_quality", labels[it]) })
                Spacer(Modifier.height(18.dp))
                Text(
                    if (crossfade < 0.5f) "Crossfade: off" else "Crossfade: ${crossfade.toInt()} s",
                    style = MaterialTheme.typography.titleMedium, color = Color.White
                )
                Slider(
                    value = crossfade,
                    onValueChange = { crossfade = it },
                    onValueChangeFinished = { onChange("crossfade_sec", crossfade.toInt().toString()) },
                    valueRange = 0f..12f,
                    colors = SliderDefaults.colors(
                        thumbColor = NovaColors.Rose,
                        activeTrackColor = NovaColors.Rose,
                        inactiveTrackColor = NovaColors.Chip
                    )
                )
            }

            Group("Playback") {
                ToggleRow("Gapless playback", "No silence between songs", gapless) { gapless = it; onChange("gapless", it.toString()) }
                ToggleRow("Normalize volume", "Keep all songs at a similar loudness", normalize) { normalize = it; onChange("normalize", it.toString()) }
                ToggleRow("Pause when headphones unplug", null, pauseOnUnplug) { pauseOnUnplug = it; onChange("pause_on_unplug", it.toString()) }
            }

            Group("Privacy") {
                Text(
                    "MusicNova plays music stored on your phone. Nothing is uploaded and no account is needed.",
                    style = MaterialTheme.typography.bodyLarge, color = NovaColors.TextSecondary
                )
            }

            Group("About") {
                Text("MusicNova", style = MaterialTheme.typography.titleMedium, color = Color.White)
                Text("Listen · Feel · Enjoy", style = MaterialTheme.typography.bodyMedium, color = NovaColors.TextSecondary)
            }
            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun Group(title: String, content: @Composable ColumnScope.() -> Unit) {
    CapsLabel(title, color = NovaColors.Rose, modifier = Modifier.padding(start = 12.dp, top = 22.dp, bottom = 8.dp))
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(26.dp))
            .background(NovaColors.Card)
            .padding(18.dp),
        content = content
    )
}

@Composable
private fun ToggleRow(title: String, subtitle: String?, checked: Boolean, onChecked: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth().padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium, color = Color.White)
            if (subtitle != null) {
                Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = NovaColors.TextSecondary)
            }
        }
        Spacer(Modifier.width(12.dp))
        Switch(
            checked = checked,
            onCheckedChange = onChecked,
            colors = SwitchDefaults.colors(
                checkedThumbColor = NovaColors.OnRose,
                checkedTrackColor = NovaColors.Rose,
                uncheckedThumbColor = NovaColors.TextSecondary,
                uncheckedTrackColor = NovaColors.Chip
            )
        )
    }
}

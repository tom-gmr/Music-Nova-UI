# MusicNova UI (Jetpack Compose), LastWave style

Screens: Home (Feed), Stats, Playlists, Search, Now Playing, Settings, floating mini player.
Engine (MusicPlayer.kt) ko is package ne nahi chhua.

## 1. Files kahan daalni hain
`app/src/main/java/com/musicnova/app/ui/` ke andar ye poora folder copy karo
(theme, model, bridge, components, screens, NovaApp.kt).

## 2. Gradle dependencies (app/build.gradle.kts)
```kotlin
android { buildFeatures { compose = true } }   // Kotlin 2.x ho to compose compiler plugin bhi lagao

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2024.09.00")
    implementation(composeBom)
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.activity:activity-compose:1.9.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
}
```

## 3. MainActivity (pehle demo mode mein chalao)
```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NovaApp(
                player = FakePlayerController(),     // demo, koi awaaz nahi
                library = FakeLibraryProvider()      // demo songs
            )
        }
    }
}
```

## 4. Real engine jodna
1. `bridge/MusicPlayerBridge.kt` kholo (poori file comment mein hai).
2. MusicPlayer.kt dekho: current song, isPlaying, position kahan se milta hai, aur play/next/prev/seek ke function naam.
3. Comment hatao, har `TODO` ko apne player ke call se badlo.
4. MainActivity mein `FakePlayerController()` ki jagah `MusicPlayerBridge(tumharaMusicPlayer)` daalo.
5. `LibraryProvider` apne scanner/database se implement karo (`UiSong` mein map karke). Isme ab `stats` bhi hai; play history track nahi karte to `UiStats()` khaali do, Stats tab "No listening data yet" dikhayega.

## 5. Baaki
- Cover art: `Artwork()` (Components.kt) abhi gradient dikhata hai. Real cover ke liye Coil add karke `AsyncImage(model = song.artworkUri, ...)` lagao. Now Playing ka bada cover `NowPlayingScreen.kt` mein hai.
- Asli frosted-glass blur (mini player ke peeche) ke liye Haze library add karni hogi. Abhi translucent dark pill hai.
- Settings ke toggles abhi sirf UI mein hain. `NovaApp(onSettingChanged = { key, value -> ... })` se engine tak bhejo.
- Rang sab `ui/theme/Theme.kt` mein hain (black + dusty rose).

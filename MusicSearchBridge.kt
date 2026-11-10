package com.musicnova.app.ui.bridge

import com.lastwave.app.data.search.SearchRepository
import com.lastwave.app.data.search.SearchTab
import com.musicnova.app.ui.model.UiSong
import javax.inject.Inject

class MusicSearchBridge @Inject 
    constructor(
    private val searchRepository: SearchRepository
) : SearchController {

    override suspend fun searchSongs(query: String): List<UiSong> {
        return searchRepository
            .search(SearchTab.TRACKS, query)
            .map { item ->
                UiSong(
                    id = (item.videoId ?: item.name).hashCode().toLong(),
                    title = item.name,
                    artist = item.artist.orEmpty(),
                    album = item.subtitle.orEmpty(),
                    artworkUri = item.artworkUrl,
                    videoId = item.videoId
                )
            }
    }
}

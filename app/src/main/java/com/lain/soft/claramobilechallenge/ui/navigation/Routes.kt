package com.lain.soft.claramobilechallenge.ui.navigation

object Routes {
    const val SEARCH_ARTIST_ROUTE = "searchArtist"

    const val ARTIST_ID = "artistId"

    const val ARTIST_DETAIL_ROUTE = "artistDetail/{$ARTIST_ID}"
    fun artistDetail(artistId: Int): String = "artistDetail/$artistId"

    const val ARTIST_RELEASES_ROUTE = "artistReleases/{$ARTIST_ID}"
    fun artistReleases(artistId: Int): String = "artistReleases/$artistId"
}

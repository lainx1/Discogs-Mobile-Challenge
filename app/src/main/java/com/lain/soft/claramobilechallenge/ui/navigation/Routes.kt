package com.lain.soft.claramobilechallenge.ui.navigation

import android.net.Uri

object Routes {
    const val SEARCH_ARTIST_ROUTE = "searchArtist"

    const val ARTIST_ID = "artistId"
    const val ARTIST_NAME = "artistName"

    const val ARTIST_DETAIL_ROUTE = "artistDetail/{$ARTIST_ID}"
    fun artistDetail(artistId: Int): String = "artistDetail/$artistId"

    const val ARTIST_RELEASES_ROUTE = "artistReleases/{$ARTIST_ID}/{$ARTIST_NAME}"
    fun artistReleases(artistId: Int, artistName: String): String =
        "artistReleases/$artistId/${Uri.encode(artistName)}"
}

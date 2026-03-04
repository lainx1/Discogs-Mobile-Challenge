package com.lain.soft.claramobilechallenge.util

import com.lain.soft.claramobilechallenge.BuildConfig

object Constants {
    const val CONNECTION_TIMEOUT = 20L
    const val DISCOGS_API_BASE_URL = BuildConfig.DISCOGS_API_BASE_URL
    const val DISCOGS_API_KEY = BuildConfig.DISCOGS_API_KEY
    const val SEARCH_ENDPOINT = "/database/search"
    const val ARTIST_DETAIL_ENDPOINT = "/artists/{artistId}"
    const val SEARCH_ENDPOINT_ITEMS_PER_PAGE = 30
    const val SEARCH_ENDPOINT_FIRST_LOAD_SIZE = SEARCH_ENDPOINT_ITEMS_PER_PAGE * 2
    const val SEARCH_ENDPOINT_START_PAGE = 1
    const val SEARCH_ENDPOINT_ARTIST_TYPE = "artist"
}

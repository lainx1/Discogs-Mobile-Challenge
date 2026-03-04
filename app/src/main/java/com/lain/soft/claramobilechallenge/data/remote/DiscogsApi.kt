package com.lain.soft.claramobilechallenge.data.remote

import com.lain.soft.claramobilechallenge.data.remote.dto.search.SearchArtistsResponseDto
import com.lain.soft.claramobilechallenge.util.Constants.SEARCH_ENDPOINT
import com.lain.soft.claramobilechallenge.util.Constants.SEARCH_ENDPOINT_ARTIST_TYPE
import retrofit2.http.GET
import retrofit2.http.Query

interface DiscogsApi {
    @GET(SEARCH_ENDPOINT)
    suspend fun searchArtists(
        @Query("q") query: String,
        @Query("type") type: String = SEARCH_ENDPOINT_ARTIST_TYPE,
        @Query("per_page") perPage: Int,
        @Query("page") page: Int
    ) : SearchArtistsResponseDto
}
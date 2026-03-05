package com.lain.soft.claramobilechallenge.data.remote

import com.lain.soft.claramobilechallenge.data.remote.dto.artist.ArtistDetailResponseDto
import com.lain.soft.claramobilechallenge.data.remote.dto.search.SearchArtistsResponseDto
import com.lain.soft.claramobilechallenge.data.remote.dto.search.SearchReleasesResponseDto
import com.lain.soft.claramobilechallenge.util.Constants.ARTIST_DETAIL_ENDPOINT
import com.lain.soft.claramobilechallenge.util.Constants.SEARCH_ENDPOINT
import com.lain.soft.claramobilechallenge.util.Constants.SEARCH_ENDPOINT_ARTIST_TYPE
import com.lain.soft.claramobilechallenge.util.Constants.SEARCH_ENDPOINT_RELEASE_TYPE
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface DiscogsApi {
    @GET(SEARCH_ENDPOINT)
    suspend fun searchArtists(
        @Query("q") query: String,
        @Query("type") type: String = SEARCH_ENDPOINT_ARTIST_TYPE,
        @Query("per_page") perPage: Int,
        @Query("page") page: Int
    ) : SearchArtistsResponseDto

    @GET(ARTIST_DETAIL_ENDPOINT)
    suspend fun getArtistDetail(
        @Path("artistId") artistId: Int
    ): ArtistDetailResponseDto

    @GET(SEARCH_ENDPOINT)
    suspend fun searchReleases(
        @Query("artist") artist: String,
        @Query("type") type: String = SEARCH_ENDPOINT_RELEASE_TYPE,
        @Query("year") year: Int? = null,
        @Query("genre") genre: String? = null,
        @Query("label") label: String? = null,
        @Query("per_page") perPage: Int,
        @Query("page") page: Int
    ): SearchReleasesResponseDto
}

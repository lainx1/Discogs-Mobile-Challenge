package com.lain.soft.claramobilechallenge.data.remote.dto.search

data class SearchArtistsResponseDto(
    val pagination: PaginationDto,
    val results: List<ResultDto>
)

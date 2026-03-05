package com.lain.soft.claramobilechallenge.data.remote.dto.search

import com.lain.soft.claramobilechallenge.data.remote.dto.pagination.PaginationDto

data class SearchReleasesResponseDto(
    val pagination: PaginationDto,
    val results: List<ReleaseSearchResultDto>
)

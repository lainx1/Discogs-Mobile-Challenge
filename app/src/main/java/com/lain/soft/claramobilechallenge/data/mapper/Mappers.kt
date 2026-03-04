package com.lain.soft.claramobilechallenge.data.mapper

import com.lain.soft.claramobilechallenge.data.remote.dto.search.ResultDto
import com.lain.soft.claramobilechallenge.domain.model.Artist

fun ResultDto.toDomain(): Artist =
    Artist(
        id = id,
        thumbnail = thumb,
        name = title
    )

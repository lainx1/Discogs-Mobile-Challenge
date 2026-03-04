package com.lain.soft.claramobilechallenge.domain.usecase

import com.lain.soft.claramobilechallenge.domain.model.Artist
import javax.inject.Inject

class PopulateLoadingArtistsUseCase @Inject constructor() : UseCase<Int, List<Artist>>() {
    override suspend fun execute(input: Int): List<Artist> {
        val loadingSeries = mutableListOf<Artist>()
        var index = 0
        repeat(input) {
            loadingSeries.add(
                Artist(
                    id = index,
                    name = "",
                    thumbnail = ""
                ),
            )
            index++
        }
        return loadingSeries
    }
}

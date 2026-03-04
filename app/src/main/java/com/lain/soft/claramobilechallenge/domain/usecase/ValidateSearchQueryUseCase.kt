package com.lain.soft.claramobilechallenge.domain.usecase

import com.lain.soft.claramobilechallenge.domain.model.AppException
import javax.inject.Inject

class ValidateSearchQueryUseCase @Inject constructor() : UseCase<String, String>() {
    override suspend fun execute(input: String): String =
        input.ifEmpty {
            throw ValidateSearchQueryException.QueryIsEmptyException()
        }

    sealed class ValidateSearchQueryException : AppException() {
        class QueryIsEmptyException : ValidateSearchQueryException()
    }
}

package com.lain.soft.claramobilechallenge.domain.usecase

import kotlinx.coroutines.CancellationException

abstract class UseCase<in Input, out Output> {
    abstract suspend fun execute(input: Input): Output

    suspend operator fun invoke(input: Input): Result<Output> =
        try {
            Result.success(execute(input))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
}

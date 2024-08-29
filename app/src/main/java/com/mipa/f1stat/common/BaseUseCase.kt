package com.mipa.f1stat.common

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.retryWhen
import java.io.IOException

abstract class BaseUseCase <in InputParameters, Model>(private var coroutineDispatcher: CoroutineDispatcher = Dispatchers.Default) {
    suspend operator fun invoke(parameters: InputParameters, forceLoad: Boolean = false): Flow<Resource<Model>> =
        execute(parameters, forceLoad)
            .applyCommonSideEffects()
            .catch { e -> emit(Resource.Fail(Exception(e))) }
            .flowOn(coroutineDispatcher)

    protected abstract suspend fun execute(parameters: InputParameters, forceLoad: Boolean): Flow<Resource<Model>>
}


fun <Model> Flow<Resource<Model>>.applyCommonSideEffects() =
    retryWhen { error, attempt ->
        when {
            error is IOException -> {
                false
            }
            else -> false
//            (error is IOException && attempt < Utils.MAX_RETRIES) -> {
//                delay(1000)
//                true
//            }
//            else -> {
//                false
//            }
        }
    }
        .onStart { emit(Resource.Loading) }
//        .onCompletion { emit(Resource.Empty) }
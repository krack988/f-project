package com.mipa.f1stat.common

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest

sealed class Resource<out T> {

    object Loading : Resource<Nothing>()

    object Empty : Resource<Nothing>()

    data class Success<out T>(
        val value: T
    ) : Resource<T>()

    data class Fail<out T>(
        val error: Throwable? = null,
        val valueError: T? = null
    ) : Resource<T>()

    val isLoading get() = this is Loading
    val isFail get() = this is Fail
    val valueOrNull get() = (this as? Success)?.value
    val isSuccess get() = this is Success

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[value = $value]"
            is Fail -> "Error[exception = $error]"
            is Loading -> "Loading"
            Empty -> "Empty"
        }
    }
}

val Resource<*>.succeeded
    get() = this is Resource.Success && value != null

fun <T> Resource<T>.successOr(fallback: T): T {
    return (this as? Resource.Success<T>)?.value ?: fallback
}

val <T> Resource<T>.value: T?
    get() = (this as? Resource.Success)?.value

suspend inline fun <reified T> Flow<Resource<T>>.updateData(liveData: MutableLiveData<Resource<T>>) {
    this.collectLatest {
        liveData.value = it
    }
}

suspend inline fun <reified T> Flow<Resource<T>>.updateOnSuccess(stateFlow: MutableStateFlow<Resource<T>>) {
    this.collectLatest {
        if (it is Resource.Success) {
            stateFlow.value = it
        }
    }
}

suspend inline fun <reified T> Flow<Resource<T>>.updateOnSuccess(liveData: MutableLiveData<Resource<T>>) {
    this.collectLatest {
        if (it is Resource.Success) {
            liveData.value = it
        }
    }
}

suspend inline fun <reified T> Flow<Resource<T>>.updateData(stateFlow: MutableStateFlow<Resource<T>>) {
    this.collectLatest {
        stateFlow.value = it
    }
}
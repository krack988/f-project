package com.mipa.f1stat.common

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job

abstract class BaseViewModel: ViewModel() {

    private val job: Job = Job()
    private val defaultExceptionHandler = CoroutineExceptionHandler { _, exception ->
        handleException(exception)
    }

    protected fun handleException(t: Throwable) {

    }

}
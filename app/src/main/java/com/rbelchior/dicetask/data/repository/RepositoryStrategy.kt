package com.rbelchior.dicetask.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

// Warm Load - with preload (show cached data while loading new data)
internal fun <D, DTO> warmDataWithCacheInitialAsFlow(
    getCacheData: suspend () -> D?,
    getRemoteData: suspend () -> Result<DTO>,
    setToLocalSource: suspend (DTO) -> Unit,
): Flow<DataWrapper.DataState<Result<D>>> {
    return flow {

        getCacheData()?.let {
            emit(DataWrapper.CacheData(Result.success(it)))
        }

        emit(
            fetchAndGetFromStorage(
                getCacheData, getRemoteData, setToLocalSource
            )
        )
    }
}

internal fun <D, DTO> fetchAndGetFromStorageFlow(
    getCacheData: suspend () -> D?,
    getRemoteData: suspend () -> Result<DTO>,
    setToLocalSource: suspend (DTO) -> Unit,
): Flow<DataWrapper.DataState<Result<D>>> {
    return flow {
        emit(fetchAndGetFromStorage(getCacheData, getRemoteData, setToLocalSource))
    }
}

private suspend fun <D, DTO> fetchAndGetFromStorage(
    getCacheData: suspend () -> D?,
    getRemoteData: suspend () -> Result<DTO>,
    setToLocalSource: suspend (DTO) -> Unit,
): DataWrapper.DataState<Result<D>> {

    val remoteDataResult = getRemoteData()

    if (remoteDataResult.isFailure) {
        return DataWrapper.Error(Result.failure(remoteDataResult.exceptionOrNull()!!))
    }

    setToLocalSource(remoteDataResult.getOrThrow())

    return getCacheData()
        ?.let { DataWrapper.LiveData(Result.success(it)) }
        ?: DataWrapper.Error(Result.failure(IllegalStateException("Could not get data from local storage")))
}

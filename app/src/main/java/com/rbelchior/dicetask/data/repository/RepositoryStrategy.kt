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

        // Load data from network,
        getRemoteData().let { result ->
            if (result.isSuccess) {
                setToLocalSource(result.getOrThrow())
                getCacheData()?.let {
                    emit(DataWrapper.LiveData(Result.success(it)))
                }
            } else {
                emit(DataWrapper.Error(Result.failure(result.exceptionOrNull()!!)))
            }
        }
    }
}

internal fun <D, DTO> fetchAndSavePartialData(
    getCacheData: suspend () -> D?,
    getRemoteData: suspend () -> Result<DTO>,
    setToLocalSource: suspend (DTO) -> Unit,
): Flow<DataWrapper.DataState<Result<D>>> {
    return flow {

        // Load data from network,
        getRemoteData().let { result ->
            // if success, save to storage, then emit value from storage
            if (result.isSuccess) {
                setToLocalSource(result.getOrThrow())
                getCacheData()?.let {
                    emit(DataWrapper.LiveData(Result.success(it)))
                }
            } else { // else emit DataWrapper<Error>
                emit(DataWrapper.Error(Result.failure(result.exceptionOrNull()!!)))
            }
        }
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

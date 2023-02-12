package com.rbelchior.dicetask.data.repository

/**
 * Class allowing upstream receivers to understand the 'freshness' of the data they are receiving.
 */
sealed class DataWrapper<out DomainObject> {
    abstract class DataState<DomainObject>(val data: DomainObject) : DataWrapper<DomainObject>()
    object NoCachedData : DataWrapper<Nothing>() // Used only when there is no cached data
    class CacheData<DomainObject>(data: DomainObject) : DataState<DomainObject>(data)
    class LiveData<DomainObject>(data: DomainObject) : DataState<DomainObject>(data)
    class Error<DomainObject>(data: DomainObject) : DataState<DomainObject>(data)
}
